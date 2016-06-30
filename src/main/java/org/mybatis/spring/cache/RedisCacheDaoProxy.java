package org.mybatis.spring.cache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperDelegateProxy;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.mybatis.spring.cache.RedisCacheService.NullObject;

import com.linda.common.mybatis.generator.annotation.Table;


/**
 * dao缓存处理器
 * @author lindezhi
 * 2015年12月4日 下午5:13:02
 */
public class RedisCacheDaoProxy extends MapperDelegateProxy implements CacheChangeListener{
	
	/**
	 * cache服务
	 */
	private RedisCacheService redisCacheService;
	
	/**
	 * 是否使用缓存标记，默认开启缓存
	 */
	private boolean openCache = true;
	
	private boolean logcacheKey = false;
	
	/**
	 * class->{prefix_key}->KeyspaceCache
	 */
	private ConcurrentHashMap<Class, ConcurrentHashMap<String,KeyspaceCache>> mapperKeyspaceCache = new ConcurrentHashMap<Class, ConcurrentHashMap<String,KeyspaceCache>>();
	
	private ConcurrentHashMap<Class,ConcurrentHashMap<String,MethodCache>> mapperMethodCache = new ConcurrentHashMap<Class,ConcurrentHashMap<String,MethodCache>>();
	
	/**
	 * dao cache 版本
	 */
	private ConcurrentHashMap<Class,String> cacheVersion = new ConcurrentHashMap<Class,String>();
	
	private ConcurrentHashMap<Class,Boolean> cacheOpen = new ConcurrentHashMap<Class,Boolean>();
	
	private ConcurrentHashMap<Class,Boolean> mapperInitedFlag = new ConcurrentHashMap<Class,Boolean>();
	
	/**
	 * 缓存key log，用于缓存出错查询
	 */
	private Log redisLogger = LogFactory.getLog("REDIS_CACHE");

	/**
	 * 业务log，记录业务异常
	 */
	private Log bizLogger = LogFactory.getLog("REDIS_CACHE");

	/**
	 * 统计log
	 */
	private Log redisStatLogger = LogFactory.getLog("REDIS_STAT");
	
	private Log mysqlStatLogger = LogFactory.getLog("MYSQL_STAT");
	
	
	/**
	 * 数据dao table
	 */
	private ConcurrentHashMap<Class,String> daoTableCache = new ConcurrentHashMap<Class,String>();
	
	/**
	 * 更新监听器
	 */
	private UpdateListener updateListener;
	
	private long sqlMaxTime = 500;
	
	/**
	 * 数据同步 table dao
	 */
	private ConcurrentHashMap<String,Class> tableDaoCache = new ConcurrentHashMap<String,Class>();
	
	/**
	 * 生成rediscache method映射表
	 * @param mapperClass
	 * @return
	 */
	private ConcurrentHashMap<String, MethodCache> genMethodCache(Class mapperClass){
		ConcurrentHashMap<String,MethodCache> map = new ConcurrentHashMap<String, MethodCache>();
		Method[] methods = mapperClass.getDeclaredMethods();
		for(Method m:methods){
			RedisCache redisCache = m.getAnnotation(RedisCache.class);
			if(redisCache!=null){
				MethodCache cache = new MethodCache();
				cache.setCacheDef(redisCache);
				cache.setMethod(m);
//				this.genCacheParamIndex(cache);
				map.put(m.getName(), cache);	
			}
		}
		return map;
	}
	
	/**
	 * 生成prefix——key的关联增删改查，关联查询
	 * @param methodCaches
	 * @return
	 */
	private ConcurrentHashMap<String,KeyspaceCache> genKeyspaceCache(ConcurrentHashMap<String, MethodCache> methodCaches){
		ConcurrentHashMap<String,KeyspaceCache> map = new ConcurrentHashMap<String,KeyspaceCache>();
		if(methodCaches!=null){
			Collection<MethodCache> methods = methodCaches.values();
			for(MethodCache method:methods){
				RedisCache cacheDef = method.getCacheDef();
				String key = StringUtils.isNotBlank(cacheDef.key())?cacheDef.key():cacheDef.refKey();
				String prefix = StringUtils.isNotBlank(cacheDef.prefix())?cacheDef.prefix():cacheDef.refPrefix();
				String mapkey = prefix+"_"+key;
				KeyspaceCache cache = map.get(mapkey);
				if(cache==null){
					cache = new KeyspaceCache();
					cache.setRefKey(cacheDef.refKey());
					cache.setRefPrefix(cacheDef.refPrefix());
					cache.setKey(key);
					cache.setPrefix(prefix);
					map.put(mapkey, cache);
				}
				
				if(cacheDef.operate()==OperateType.MULTISELECT){
					//批量
					cache.setMultiSelect(method);
				}else{
					if(cacheDef.operate()==OperateType.SELECT){
						cache.setSelect(method);
					}else if(cacheDef.operate()==OperateType.UPDATE){
						cache.setUpdate(method);
					}else if(cacheDef.operate()==OperateType.DELETE){
						cache.setDelete(method);
					}else if(cacheDef.operate()==OperateType.INSERT){
						cache.setInsert(method);
					}
					
					/**
					 * 获取ref select列表，方便做更新操作时，删除select cache
					 */
					if(cacheDef.operate()==OperateType.SELECT){
						String refKey = cacheDef.refKey();
						String refPrefix = cacheDef.refPrefix();
						if(StringUtils.isNotBlank(refKey)&&StringUtils.isNotBlank(refPrefix)){
							String refCacheKey = refPrefix+"_"+refKey;
							KeyspaceCache refCache = map.get(refCacheKey);
							if(refCache==null){
								refCache = new KeyspaceCache();
								refCache.setKey(refKey);
								refCache.setPrefix(refPrefix);
								map.put(refCacheKey, refCache);
							}
							List<MethodCache> refSelects = refCache.getRefSelects();
							if(refSelects==null){
								refSelects = new ArrayList<MethodCache>();
								refCache.setRefSelects(refSelects);
							}
							refSelects.add(method);
						}
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * 查询检查
	 * @param mapper
	 * @param method
	 * @return
	 */
	private Class<?> initIfNotInited(Class<?> dao){
		Class<?> mapperClass = dao;
		Boolean init = mapperInitedFlag.get(mapperClass);
		if(init==null||!init){
			ConcurrentHashMap<String,MethodCache> methodCache = mapperMethodCache.get(mapperClass);
			ConcurrentHashMap<String, KeyspaceCache> keyspaceCache = mapperKeyspaceCache.get(mapperClass);
			if(methodCache==null||keyspaceCache==null){
				if(methodCache==null){
					methodCache = this.genMethodCache(mapperClass);
					mapperMethodCache.put(mapperClass, methodCache);
				}
				if(keyspaceCache==null){
					keyspaceCache = this.genKeyspaceCache(methodCache);
					mapperKeyspaceCache.put(mapperClass, keyspaceCache);
				}
			}
			CacheVersion version = mapperClass.getAnnotation(CacheVersion.class);
			if(version!=null){
				cacheVersion.put(mapperClass, version.value());
				cacheOpen.put(mapperClass, version.open());
			}else{
				cacheVersion.put(mapperClass, "v1.0");
				cacheOpen.put(mapperClass, false);
			}
			init = true;
			mapperInitedFlag.put(mapperClass, init);
			redisLogger.info("method cache init:"+JSONUtils.toJSON(methodCache));
			redisLogger.info("keyspace cahce init:"+JSONUtils.toJSON(keyspaceCache));
		}
		
		String table = this.daoTableCache.get(mapperClass);
		if(table==null){
			Table annotation = mapperClass.getAnnotation(Table.class);
			if(annotation!=null){
				daoTableCache.put(mapperClass, annotation.name());
				tableDaoCache.put(annotation.name(), mapperClass);
			}else{
				throw new DaoCacheException("no table name define for:"+mapperClass);
			}
		}
		return mapperClass;
	}
	
	/**
	 * 生成cache key
	 * @param methodCache
	 * @param args
	 * @return
	 */
	private String genObjectCacheKey(Class clazz,MethodCache methodCache,Object[] args){
		if(args.length>0){
			StringBuilder sb = new StringBuilder();
			sb.append(methodCache.prefix());
			String version = cacheVersion.get(clazz);
			sb.append(version);
			sb.append("_");
			boolean first = true;
			for(Object arg:args){
				if(!first){
					sb.append("_");
				}
				if(arg!=null){
					if(arg instanceof Collection){
						boolean ccFirst = true;
						Collection cc = (Collection)arg;
						for(Object aa:cc){
							if(!ccFirst){
								sb.append("_");
							}
							sb.append(aa);
							ccFirst = true;
						}
					}else{
						sb.append(arg);
					}
				}else{
					sb.append("null");
				}
				first = false;
			}
			return sb.toString();
		}else{
			return methodCache.prefix();
		}
	}
	
	
	
	/**
	 * 拿到update方法里面的select方法的参数，方便做update删除缓存
	 * @param param
	 * @param updateMethod
	 * @param args
	 * @return
	 */
	private Object getMethodParameter(String param,Method updateMethod,Object[] args){
		Object obj = null;
		Annotation[][] pans = updateMethod.getParameterAnnotations();
		if(pans!=null&&pans.length>0){
			int index = 0;
			for(Annotation[] ans:pans){
				Param p = null;
				for(Annotation an:ans){
					if(an instanceof Param){
						p = (Param)an;
					}
				}
				if(p!=null&&param.equals(p.value())){
					obj = args[index];
					return obj;
				}else{
					Object o = args[index];
					if(BeanUtils.isCandidateObject(o.getClass())){
						try{
							Field field = o.getClass().getDeclaredField(param);
							if(field!=null){
								field.setAccessible(true);
								Object object = field.get(o);
								if(object!=null){
									obj = object;
								}
							}
						}catch(Exception e){
							bizLogger.error("get field error:"+e.getMessage(),e);
						}
					}
				}
				index++;
			}
		}
		return obj;
	}
	
	/**
	 * 拿到update方法里面的select方法的参数，方便做update删除缓存
	 * @param select
	 * @param updateMethod
	 * @param args
	 * @return
	 */
	private Object[] genSelectArgs(Method select,Method updateMethod,Object[] args){
		Annotation[][] pans = select.getParameterAnnotations();
		if(pans==null||pans.length==0){
			return new Object[0];
		}
		Object[] selectArgs = new Object[pans.length];
		int idx = 0;
		if(pans!=null&&pans.length>0){
			for(Annotation[] ans:pans){
				Param p = null;
				for(Annotation an:ans){
					if(an instanceof Param){
						p = (Param)an;
					}
				}
				if(p!=null){
					String name = p.value();
					Object arg = this.getMethodParameter(name, updateMethod, args);
					selectArgs[idx] = arg;
				}else{
					throw new DaoCacheException("can't find parameter for "+select.getName()+" from:"+updateMethod.getName());
				}
			}
			return selectArgs;
		}else{
			return null;
		}
	}
	
	/**
	 * 拿到更新受影响的对象集合
	 * @param mapper
	 * @param proxy
	 * @param methodCache
	 * @param args
	 * @return
	 */
	private Object getUpdateAffectedObjects(MapperProxy mapper, Object proxy,MethodCache methodCache,Object[] args){
		Class mapperClass = mapper.getMapperInterface();
		String key = methodCache.getCacheDef().refKey();
		String prefix = methodCache.getCacheDef().refPrefix();
		String cacheKey = prefix+"_"+key;
		KeyspaceCache keyspaceCache = mapperKeyspaceCache.get(mapperClass).get(cacheKey);
		Method updateMethod = methodCache.getMethod();
		MethodCache select = keyspaceCache.getSelect();
		if(select!=null){
			Method method = select.getMethod();
			Object[] selectArgs = this.genSelectArgs(method, updateMethod,args);
			if(selectArgs!=null){
				try {
					return this.invoke(mapper, proxy, method, selectArgs);
				} catch (Throwable e) {
					bizLogger.error("select for cache update failed",e);
				}
			}
		}
		return null;
	}
	
	/**
	 * 生成受影响cache key
	 * @param mapperClass
	 * @param object
	 * @param prefix
	 * @param key
	 * @return
	 */
	private String genAffectedCacheKey(Class mapperClass,Object object,String refPrefix,String key){
		try {
			Field field = object.getClass().getDeclaredField(key);
			if(field!=null){
				field.setAccessible(true);
				Object value = field.get(object);
				if(value!=null){
					return this.genAffectedCacheKey(mapperClass, refPrefix, key, value);
				}
			}
		} catch (NoSuchFieldException e) {
			bizLogger.error("mapper:"+mapperClass.getName()+" bean:"+object.getClass().getName()+" key:"+key,e);
		} catch (SecurityException e) {
			bizLogger.error("mapper:"+mapperClass.getName()+" bean:"+object.getClass().getName()+" key:"+key,e);
		} catch (IllegalArgumentException e) {
			bizLogger.error("mapper:"+mapperClass.getName()+" bean:"+object.getClass().getName()+" key:"+key,e);
		} catch (IllegalAccessException e) {
			bizLogger.error("mapper:"+mapperClass.getName()+" bean:"+object.getClass().getName()+" key:"+key,e);
		}
		return null;
	}
	
	private String genAffectedCacheKey(Class mapper,String refPrefix,String key,Object value){
		if(value!=null){
			String version = cacheVersion.get(mapper);
			return refPrefix+version+"_"+key+"_affected_"+value;
		}
		return null;
	}
	
	/**
	 * 添加到受影响cache key中
	 * @param mapperClass
	 * @param methodCache
	 * @param object
	 * @param objectCacheKey
	 */
	private void addAffectedCacheKey(String affectedCacheKey,String objectCacheKey){
		if(affectedCacheKey!=null&&objectCacheKey!=null){
			redisCacheService.sadd(affectedCacheKey, RedisCacheService.EXPIRE_DAY_3, objectCacheKey);
			if(this.logcacheKey){
				redisLogger.info("[ADDCACHE] affected:"+affectedCacheKey+" sadd:"+objectCacheKey);
			}
		}
	}
	
	/**
	 * 批量清除缓存
	 * @param mapperClass
	 * @param keyspaceCache
	 * @param clearers
	 */
	private void clearRefCache(Class mapperClass,KeyspaceCache keyspaceCache,Object oldAffecteds,Object newAffecteds){
		HashSet<String> keys = new HashSet<String>();
		if(oldAffecteds!=null){
			Set<String> oldAffectedKeys = this.genNeedDeleteKeysByObjects(mapperClass, keyspaceCache, oldAffecteds);
			if(oldAffectedKeys!=null){
				keys.addAll(oldAffectedKeys);
			}
		}
		if(newAffecteds!=null){
			Set<String> newAffectedKeys = this.genNeedDeleteKeysByObjects(mapperClass, keyspaceCache, newAffecteds);
			if(newAffectedKeys!=null){
				keys.addAll(newAffectedKeys);
			}
		}
		if(keys!=null&&keys.size()>0){
			/**
			 * 添加最后事物删除key
			 */
			CacheContext.addKeys(keys);
			redisCacheService.del(keys);
			if(this.logcacheKey){
				StringBuilder sb = new StringBuilder();
				sb.append("[CLEAR_CACHE] ");
				for(String key:keys){
					sb.append(key);
					sb.append(",");
				}
				redisLogger.info(sb.toString());
			}
		}
	}
	
	private Set<String> genNeedDeleteKeysByObjects(Class mapperClass,KeyspaceCache keyspaceCache,Object objs){
		HashSet<String> keys = new HashSet<String>();
		if(objs!=null){
			if(objs instanceof Collection){
				Collection cs = (Collection)objs;
				for(Object c:cs){
					Set<String> set = this.genNeedDeleteKeysByObject(mapperClass, keyspaceCache, c);
					keys.addAll(set);
				}
			}else{
				if(BeanUtils.isCandidateObject(objs.getClass())){
					Set<String> set = this.genNeedDeleteKeysByObject(mapperClass, keyspaceCache, objs);
					keys.addAll(set);
				}
			}
		}
		return keys;
	}
	
	private Set<String> genNeedDeleteKeysByObject(Class mapperClass,KeyspaceCache keyspaceCache,Object obj){
		HashSet<String> keys = new HashSet<String>();
		List<MethodCache> selects = keyspaceCache.getRefSelects();
		if(selects!=null){
			for(MethodCache select:selects){
				String affectedCacheKey = this.genAffectedCacheKey(mapperClass, obj, select.refPrefix(), select.key());
				if(StringUtils.isNotBlank(affectedCacheKey)){
					Set<String> set = redisCacheService.sget(affectedCacheKey);
					if(set!=null){
						keys.addAll(set);
					}
					keys.add(affectedCacheKey);
				}
			}
		}
		return keys;
	}
	
	private Object genMethodResult(Method method){
		Class<?> returnType = method.getReturnType();
		if(returnType==int.class){
			return new Long(1).intValue();
		}else if(returnType==short.class){
			return new Long(1).shortValue();
		}else if(returnType==long.class){
			return new Long(1).longValue();
		}else if(returnType==byte.class){
			return new Long(1).byteValue();
		}else if(returnType==boolean.class){
			return true;
		}else if(returnType==float.class){
			return new Double(1.0).floatValue();
		}else if(returnType==double.class){
			return new Double(1.0).doubleValue();
		}else if(returnType==char.class){
			return '1';
		}else if(returnType==Integer.class){
			return new Integer(1);
		}else if(returnType==Short.class){
			return new Short((short) 1);
		}else if(returnType==Long.class){
			return new Long(1);
		}else if(returnType==Byte.class){
			return new Byte((byte) 1);
		}else if(returnType==Boolean.class){
			return new Boolean(true);
		}else if(returnType==Float.class){
			return new Float(1.0);
		}else if(returnType==Double.class){
			return new Double(1.0);
		}else if(returnType==Character.class){
			return new Character('1');
		}else if(returnType==String.class){
			return "true";
		}else{
			return new Object();
		}
	}
	
	private Object[] genSingleSelectArgs(KeyspaceCache keyspaceCache,Object arg,Method method){
		MethodCache select = keyspaceCache.getSelect();
		Method selectMethod = select.getMethod();
		if(BeanUtils.isJavaLangType(arg.getClass())){
			Class<?>[] parameterTypes = selectMethod.getParameterTypes();
			if(parameterTypes.length==1&&BeanUtils.isJavaLangType(parameterTypes[0])){
				return new Object[]{arg};
			}else{
				throw new DaoCacheException("invalid multiselect method:"+method.getName()+" invalid len and type");
			}
		}else if(BeanUtils.isMap(arg.getClass())){
			return this.genSelectArgsFromMap(selectMethod, method, (Map)arg);
		}else if(BeanUtils.isCandidateObject(arg.getClass())){
			Map<String, Object> map = BeanUtils.beanToMap(arg,false);
			return this.genSelectArgsFromMap(selectMethod, method, map);
		}else{
			throw new DaoCacheException("invalid multiselect parameter type method:"+method.getName());
		}
	}
	
	private Object[] genSelectArgsFromMap(Method selectMethod,Method multiMethod,Map paramMap){
		Class<?>[] parameterTypes = selectMethod.getParameterTypes();
		Annotation[][] pans = selectMethod.getParameterAnnotations();
		if(pans==null||pans.length==0){
			throw new DaoCacheException("invalid select method "+selectMethod.getName()+" for multi get");
		}
		Object[] selectArgs = new Object[pans.length];
		int idx = 0;
		for(Annotation[] ans:pans){
			Param p = null;
			for(Annotation an:ans){
				if(an instanceof Param){
					p = (Param)an;
				}
			}
			if(p!=null){
				String name = p.value();
				Object paramValue = paramMap.get(name);
				selectArgs[idx] = paramValue;
			}else{
				throw new DaoCacheException("can't find parameter for "+selectMethod.getName()+" invalid cache param define");
			}
			idx++;
		}
		return selectArgs;
	}
	
	/**
	 * 批量操作
	 * @author lindezhi
	 * 2016年6月30日 上午10:40:24
	 */
	private static class MultiObject{
		
		private MethodCache methodCache;//方法
		
		private Object[] args; //方法入参
		
		private String objectCacheKey; //cache key
		
		private Object result;//执行结果
		
		public MultiObject(MethodCache methodCache, Object[] args, String objectCacheKey, Object result) {
			super();
			this.methodCache = methodCache;
			this.args = args;
			this.objectCacheKey = objectCacheKey;
			this.result = result;
		}
		public MethodCache getMethodCache() {
			return methodCache;
		}
		public void setMethodCache(MethodCache methodCache) {
			this.methodCache = methodCache;
		}
		public Object[] getArgs() {
			return args;
		}
		public void setArgs(Object[] args) {
			this.args = args;
		}
		public String getObjectCacheKey() {
			return objectCacheKey;
		}
		public void setObjectCacheKey(String objectCacheKey) {
			this.objectCacheKey = objectCacheKey;
		}
		public Object getResult() {
			return result;
		}
		public void setResult(Object result) {
			this.result = result;
		}
	}
	
	/**
	 * 执行批量查询，先批量从缓存取，没取到的从db取，加入缓存
	 * @param mapperClass
	 * @param mapper
	 * @param proxy
	 * @param keyspaceCache
	 * @param selectArgList
	 * @return
	 * @throws Throwable
	 */
	private List<Object> doMultiSelect(Class mapperClass,MapperProxy mapper, Object proxy,KeyspaceCache keyspaceCache,List<Object[]> selectArgList) throws Throwable{
		//生成单个cache key
		List<MultiObject> result = new ArrayList<MultiObject>();
		//关联单个单个查询方法
		MethodCache methodCache = keyspaceCache.getSelect();
		
		if(methodCache==null){
			throw new DaoCacheException("can't find single select method for multi select:"+mapperClass);
		}
		
		HashMap<String, MultiObject> resultMap = new HashMap<String,MultiObject>();
		for(Object[] args:selectArgList){
			String objectCacheKey = this.genObjectCacheKey(mapperClass,methodCache, args);
			MultiObject obj = new MultiObject(methodCache,args,objectCacheKey,null);
			result.add(obj);
			resultMap.put(objectCacheKey, obj);
		}
		
		int cacheCount = 0;
		int mysqlCount = 0;
		
		ArrayList<Object> multiResult = new ArrayList<Object>();
		long start = System.currentTimeMillis();
		//批量缓存获取
		Map<String, Object> cacheObject = new HashMap<String,Object>();
		if(openCache){//开启缓存才获取
			 cacheObject = redisCacheService.mget(resultMap.keySet());
		}

		long end = System.currentTimeMillis();
		long cacheCost = end-start;
		
		long mysqlCost = 0;
		
		Set<String> keys = resultMap.keySet();
		for(String key:keys){
			Object obj = cacheObject.get(key);
			if(obj==null){//cache
				Object[] args = resultMap.get(key).getArgs();
				//单个db获取
				start = System.currentTimeMillis();
				obj = this.doInvoke(mapper, proxy, methodCache.getMethod(), args);
				end = System.currentTimeMillis();
				 mysqlCost += end-start;
				mysqlCount++;
			}else{
				//避免缓存击穿的对象特殊处理
				if(obj instanceof NullObject){
					obj = null;
				}
				cacheCount++;
			}
			resultMap.get(key).setResult(obj);
			
			if(obj!=null){
				if(obj instanceof Collection){
					multiResult.addAll((Collection)obj);
				}else{
					multiResult.add(obj);
				}
			}
		}
		
		redisStatLogger.info("[MULTI] interface:"+mapperClass.getCanonicalName()+" method:"+keyspaceCache.getMultiSelect().getMethod().getName()+" cacheCount:"+cacheCount+" mysqlCount:"+mysqlCount+" cachetime:"+cacheCost+" mysqltime:"+mysqlCost);
		return multiResult;
	}
	
	
	/**
	 * cache aop方法
	 * 基本原则：select先从缓存，没有从db让后加入缓存，更新删除时先取出元数据，执行DBupdate delete，然后清除元数据相关缓存
	 * SQL执行：mapper.invoke(proxy, method, args);
	 * @param mapper
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	private Object doInvoke(MapperProxy mapper, Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;
		//预先生成缓存定义，如果不存在的情况下
		Class mapperClass = this.initIfNotInited(mapper.getMapperInterface());
		//从本地cache获取缓存定义
		ConcurrentHashMap<String, MethodCache> methodCacheMap = mapperMethodCache.get(mapperClass);
		ConcurrentHashMap<String, KeyspaceCache> keyspaceCacheMap = mapperKeyspaceCache.get(mapperClass);
		//没加缓存，直接获取DB
		if(methodCacheMap==null||methodCacheMap.get(method.getName())==null){
			return this.getFromMysql(mapper, proxy, method, args,null);
		}
		
		//SQL METHOD缓存定义
		MethodCache methodCache = methodCacheMap.get(method.getName());
		String mapkey = methodCache.refPrefix()+"_"+methodCache.refKey();
		if(methodCache.getCacheDef().operate()==OperateType.MULTISELECT){//multi select转换
			mapkey = methodCache.prefix()+"_"+methodCache.key();
		}

		if(keyspaceCacheMap==null||keyspaceCacheMap.get(mapkey)==null){
			return this.getFromMysql(mapper, proxy, method, args,null);
		}
		KeyspaceCache keyspaceCache = keyspaceCacheMap.get(mapkey);

		RedisCache redisCache = methodCache.getCacheDef();
		
		//不开启缓存
		if(!openCache&&redisCache.operate()!=OperateType.MULTISELECT){
			return this.getFromMysql(mapper, proxy, method, args,redisCache);
		}
		
		//没有开启缓存，直接更新db
		Boolean open = cacheOpen.get(mapperClass);
		//没有开启的，非multi select直接从数据库取
		if(open!=null&&open==false&&redisCache.operate()!=OperateType.MULTISELECT){
			return this.getFromMysql(mapper, proxy, method, args,null);
		}
		
		if(redisCache.operate()==OperateType.MULTISELECT){//批量查询，先走缓存批量获取，没有获取到的，从db获取
			Object collection = args[0];
			if(collection instanceof Collection){
				Collection cc = (Collection)collection;
				if(cc.isEmpty()){
					return Collections.emptyList();
				}
				List<Object[]> selectArgList = new ArrayList<Object[]>();
				for(Object arg:cc){
					Object[] selectArgs = this.genSingleSelectArgs(keyspaceCache, arg, method);
					selectArgList.add(selectArgs);
				}
				return this.doMultiSelect(mapperClass,mapper,proxy,keyspaceCache, selectArgList);
			}else{
				throw new DaoCacheException("invalid multiselect parameters method:"+method.getName()+" args:"+JSONUtils.toJSON(args));
			}
		}else if(redisCache.operate()==OperateType.SELECT){
			//key = prefix_arg1_arg2_arg3
			//优先从缓存获取
			String objectCacheKey = this.genObjectCacheKey(mapperClass,methodCache, args);
			String affectedCacheKey = null;
			
			long start = System.currentTimeMillis();
			//从缓存获取
			Object object = null;
			//默认从缓存或者DB获取
			if(RedisCacheContext.getSelectOp()==SelectOp.SELECT_CACHE_OR_DB){
				object = redisCacheService.getObject(objectCacheKey);
			}
			long end = System.currentTimeMillis();
			
			Object affectedShard = this.getMethodParameter(methodCache.key(), methodCache.getMethod(), args);
			if(affectedShard!=null){
				affectedCacheKey = this.genAffectedCacheKey(mapperClass,methodCache.refPrefix(), methodCache.key(), affectedShard);
			}
			//避免缓存被击穿
			if(object!=null){
				if(object instanceof NullObject){
					this.addAffectedCacheKey(affectedCacheKey, objectCacheKey);
					return null;
				}
			}
			
			if(object==null){
				start = System.currentTimeMillis();
				//缓存没取到，从DB获取
				object = this.doMysqlInvoke(mapper, proxy, method, args,redisCache);
				
				if(object!=null&&objectCacheKey!=null){
					end = System.currentTimeMillis();
					redisStatLogger.info("[MYSQL] class:"+mapperClass+" method:"+method.getName()+" cost:"+(end-start));
					//加入影响范围
					if(object!=null&&objectCacheKey!=null&&affectedCacheKey!=null){
						this.addAffectedCacheKey(affectedCacheKey, objectCacheKey);
					}
					//加入到缓存中
					int ttl = redisCache.ttl();
					if(ttl>RedisCacheService.EXPIRE_DAY){
						ttl = RedisCacheService.EXPIRE_DAY;
					}
					redisCacheService.setObject(objectCacheKey, object,ttl);
					if(this.logcacheKey){
						redisLogger.info("[ADDCACHE] key:"+objectCacheKey);
					}
				}else{//为空的情况避免缓存被击穿，使用null
					Object obj = new NullObject();
					end = System.currentTimeMillis();
					redisStatLogger.info("[MYSQL] class:"+mapperClass+" method:"+method.getName()+" cost:"+(end-start));
					//加入影响范围
					if(objectCacheKey!=null&&affectedCacheKey!=null){
						this.addAffectedCacheKey(affectedCacheKey, objectCacheKey);
					}
					//避免缓存被击穿，时间为1分钟
					int ttl = RedisCacheService.EXPIRE_MINUTE;
					redisCacheService.setObject(objectCacheKey, obj,ttl);
					if(this.logcacheKey){
						redisLogger.info("[ADDCACHE] key:"+objectCacheKey);
					}
				}
			}else{
				//加入影响范围
				if(object!=null&&objectCacheKey!=null&&affectedCacheKey!=null){
					this.addAffectedCacheKey(affectedCacheKey, objectCacheKey);
				}
				redisStatLogger.info("[CACHE] class:"+mapperClass+" method:"+method.getName()+" cost:"+(end-start));
			}
			result = object;
		}else if(redisCache.operate()==OperateType.INSERT){
			//插入直接更新DB
			result = this.doMysqlInvoke(mapper, proxy, method, args,redisCache);
			//更新插入只允许单个参数，或者list
			this.clearRefCache(mapperClass,keyspaceCache, null,args[0]);
			
			//清空缓存之后，通知php更新缓存
			this.notifyUpdate(mapperClass, methodCache, null,args);
			return result;
		}else if(redisCache.operate()==OperateType.UPDATE){
			//update 的都从数据库获取
			//获取老的对象，cache里面有会从cache获取，否则从DB获取
			try{
				RedisCacheContext.setSelectOp(SelectOp.SELECT_DB);
				Object oldAffectedObjects = this.getUpdateAffectedObjects(mapper, proxy, methodCache, args);

				redisLogger.info("old data:"+BeanUtils.valueToString(oldAffectedObjects));
				Object newAffectedObjects = null;
				//加入标记，方便php数据同步
				int updateOp = RedisCacheContext.getOp();
				//需要更新数据库
				if(updateOp==UpdateOp.UPDATE_DB||updateOp==UpdateOp.UPDATE_CACHE_AND_DB){
					//先删除缓存数据
					this.clearRefCache(mapperClass,keyspaceCache, oldAffectedObjects,null);
					//更新db
					result = this.doMysqlInvoke(mapper, proxy, method, args,redisCache);
					//db更新之后获取最新数据，刷新缓存
					newAffectedObjects = this.getUpdateAffectedObjects(mapper, proxy, methodCache, args);
					
					this.clearRefCache(mapperClass,keyspaceCache, null,newAffectedObjects);
					//清空缓存之后，通知php更新缓存
					this.notifyUpdate(mapperClass, methodCache, oldAffectedObjects,args);
				}else{//紧删除缓存数据
					//update by id，只有一个数据的时候，清空缓存，传的参数为老数据，php清缓存使用
					if(args.length==1&&BeanUtils.isCandidateObject(args[0].getClass())){
						newAffectedObjects = args[0];
					}
					this.clearRefCache(mapperClass,keyspaceCache, oldAffectedObjects,newAffectedObjects);
					result = this.genMethodResult(method);
				}
			}finally{
				RedisCacheContext.setSelectOp(SelectOp.SELECT_CACHE_OR_DB);
			}
		}else if(redisCache.operate()==OperateType.DELETE){
			Object oldAffectedObjects = this.getUpdateAffectedObjects(mapper, proxy, methodCache, args);
			result = this.doMysqlInvoke(mapper, proxy, method, args,redisCache);
			this.clearRefCache(mapperClass,keyspaceCache, oldAffectedObjects,null);
			//清空缓存之后，通知php更新缓存
			this.notifyUpdate(mapperClass, methodCache, oldAffectedObjects,args);
		}else{
			result = this.doMysqlInvoke(mapper, proxy, method, args,redisCache);
		}
		return result;
	}
	
	/**
	 * php 通知更新，id从名为id的参数中取值
	 * @param mapperClass
	 * @param methodCache
	 * @param oldobject
	 * @param args
	 */
	private void notifyUpdate(Class mapperClass,MethodCache methodCache,Object oldobject,Object[] args){
		if(this.updateListener!=null){
			String table = this.daoTableCache.get(mapperClass);
			Method method = methodCache.getMethod();
			Object id = this.getMethodParameter("id", method, args);
			if(id!=null){
				updateListener.onUpdate(method, table, id.toString(), oldobject);
			}else{
				updateListener.onUpdate(method, table, null, oldobject);
			}
		}
	}
	
	@Override
	public Object invoke(MapperProxy mapper, Object proxy, Method method, Object[] args) throws Throwable {
		if(this.logcacheKey){
			StringBuilder sb = new StringBuilder();
			sb.append("[MAPPER]:class:"+mapper.getMapperInterface());
			sb.append(" method:"+method.getName());
			sb.append(" args:[");
			if(args!=null){
				for(Object arg:args){
					if(arg!=null){
						if(BeanUtils.isJavaLangType(arg.getClass())){
							sb.append(arg);
						}else{
							sb.append(JSONUtils.toJSON(arg));
						}
					}else{
						sb.append("null");
					}
					sb.append(" ,");
				}
			}
			sb.append("]");
			bizLogger.info(sb.toString());
		}
		return this.doInvoke(mapper, proxy, method, args);
	}
	
	private Object getFromMysql(MapperProxy mapper, Object proxy, Method method, Object[] args,RedisCache redisCache) throws Throwable {
		return this.doMysqlInvoke(mapper, proxy, method, args,redisCache);
	}
	
	public Object doMysqlInvoke(MapperProxy mapper, Object proxy, Method method, Object[] args,RedisCache redisCache) throws Throwable {
		long start = System.currentTimeMillis();
		try {
			if (redisCache!=null&&RedisCacheContext.getOp() == UpdateOp.UPDATE_CACHE
					&&redisCache.operate()!=OperateType.SELECT&&redisCache.operate()!=OperateType.MULTISELECT) {
				return 1;
			} else {
				return mapper.invoke(proxy, method, args);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (RedisCacheContext.getOp() != UpdateOp.UPDATE_CACHE) {
				long end = System.currentTimeMillis();
				long cost = end - start;
				if (cost > sqlMaxTime) {
					mysqlStatLogger.error("[MYSQL] " + method.getDeclaringClass().getCanonicalName() + " method:" + method.getName()
							+ " cost:" + cost);
				} else {
					mysqlStatLogger.info("[MYSQL] " + method.getDeclaringClass().getCanonicalName() + " method:" + method.getName()
							+ " cost:" + cost);
				}
			}
		}
	}
	
	public RedisCacheService getRedisCacheService() {
		return redisCacheService;
	}

	public void setRedisCacheService(RedisCacheService redisCacheService) {
		this.redisCacheService = redisCacheService;
	}

	public boolean isOpenCache() {
		return openCache;
	}

	public void setOpenCache(boolean openCache) {
		this.openCache = openCache;
	}

	public boolean isLogcacheKey() {
		return logcacheKey;
	}

	public void setLogcacheKey(boolean logcacheKey) {
		this.logcacheKey = logcacheKey;
	}

	@Override
	public void onChange(String table, String id, Map<String, Object> olddata) {
		//仅更新缓存
		RedisCacheContext.setOp(UpdateOp.UPDATE_CACHE);
		
		if(olddata==null){
			olddata = new HashMap<String,Object>();
		}
		
		if(StringUtils.isNotBlank(id)){
			olddata.put("id", Long.parseLong(id));
		}
		
		redisLogger.info("[CACHE_UPDATE] table:"+table+" id:"+id+" olddata:"+JSONUtils.toJSON(olddata));
		
		Class daoClazz = this.tableDaoCache.get(table);
		
		//dc.table 的修正
		if(daoClazz==null){
			int idx = 3;
			if(table.startsWith("dc.")&&table.length()>idx){
				String newTable = table.substring(idx);
				daoClazz = this.tableDaoCache.get(newTable);
			}
		}
		
		if(daoClazz!=null){
			Boolean open = cacheOpen.get(daoClazz);
			//没有开启缓存，直接不做任何动作
			if(open!=null&&open==false){
				return ;
			}
			
			Method[] methods = daoClazz.getDeclaredMethods();
			Object daoBean = ApplicationContextHolder.getBean(daoClazz);
			Method method = null;
			Method getMethod = null;
			for(Method me:methods){
				if(me.getName().equals("updateById")){
					method = me;
				}
				if(me.getName().equals("getById")){
					getMethod = me;
				}
			}
			if(method!=null){
				//没开缓存，不更新
				MethodCache cache = this.mapperMethodCache.get(daoClazz).get(method.getName());
				if(cache==null){
					return ;
				}
				
				//如果是update，直接更新缓存
				Class<?>[] types = method.getParameterTypes();
				if(types!=null&&types.length==1){
					Object obj = BeanUtils.mapToBean(olddata, types[0]);
					try{
						method.invoke(daoBean, obj);
						redisLogger.info("update table:"+table+" id:"+id+" olddata:"+JSONUtils.toJSON(olddata)+" success");
					}catch(Exception e){
						redisLogger.error(daoClazz+" table:"+table+" updateById method "+e.getMessage(), e);
					}
				}
			}else{
				redisLogger.error("no updateById method found for dao class:"+daoClazz+" table:"+table);
			}
		}else{
			redisLogger.info("no mapper found for table:"+table);
		}
	}

	public UpdateListener getUpdateListener() {
		return updateListener;
	}

	public void setUpdateListener(UpdateListener updateListener) {
		this.updateListener = updateListener;
	}

	/**
	 * 初始化回调，关联
	 */
	@Override
	public void newInstanceCallback(Class<?> mapperInterface) {
		this.initIfNotInited(mapperInterface);
	}

	public long getSqlMaxTime() {
		return sqlMaxTime;
	}

	public void setSqlMaxTime(long sqlMaxTime) {
		this.sqlMaxTime = sqlMaxTime;
	}
}