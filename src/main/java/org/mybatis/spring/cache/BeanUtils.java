package org.mybatis.spring.cache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.linda.common.mybatis.generator.annotation.Column;
import com.linda.common.mybatis.generator.annotation.PrimaryKey;

/**
 * 
 * @author lindezhi
 * 2015年11月11日 下午4:38:14
 * bean result rpc转换
 */
public class BeanUtils {
	
	/*
	public static Method getMethod(ProceedingJoinPoint pjp){
		MethodSignature md = (MethodSignature)pjp.getSignature();
		return md.getMethod();
	}
	
	public static <T extends Annotation> T getAnnotation(ProceedingJoinPoint pjp,Class<T> clazz){
		Method method = getMethod(pjp);
		return method.getAnnotation(clazz);
	}
	
	*/
	
	public static <A,B> void parseFields(A a,B b){
		if(null == a){
			return;
		}
		Field[] aFields = a.getClass().getDeclaredFields();
		HashMap<String, Field> fieldMap = new HashMap<String,Field>();
		for(Field af:aFields){
			af.setAccessible(true);
			fieldMap.put(af.getName(), af);
		}
		
		Field[] fields = b.getClass().getDeclaredFields();
		for(Field field:fields){
			field.setAccessible(true);
			String name = field.getName();
			Class<?> type = field.getType();
			Field af = fieldMap.get(name);
			if(af!=null){
				//基本字段转换，java类型,相同类型
				if(isJavaLangType(af.getType())&&af.getType()==type){
					try{
						Object value = af.get(a);
						if(value!=null){
							field.set(b, value);
						}
					}catch(Exception e){
						// nothing
					}
				}else{
					if(isCollection(af.getType())&&isCollection(type)){
						//集合转换
						try{
							Class<?> btype = null;
							if(type.isArray()){
								btype = type.getComponentType();
							}else{
								btype = getCollectionItemType(field);
							}
							if(btype==null){
								continue;
							}
							List bvs = new ArrayList();
							Object value = af.get(a);
							if(af.getType().isArray()){
								Object[] avs = (Object[])value;
								for(Object av:avs){
									Object bValue = parseFields(av,btype);
									bvs.add(bValue);
								}
							}else{
								Collection c = (Collection)value;
								for(Object av:c){
									Object bValue = parseFields(av,btype);
									bvs.add(bValue);
								}
							}
							Object bValue = convertCollection(type,bvs);
							field.set(b, bValue);
						}catch(Exception e){
							// nothing
						}
						
					}else{
						//基本对象转换
						if(!isJavaLangType(af.getType())&&!isJavaLangType(type)
								&&!isMap(af.getType())&&!isMap(type)&&!isCollection(af.getType())&&!isCollection(type)){
							//对象直接转换
							try{
								Object aValue = af.get(a);
								if(aValue!=null){
									Object bValue = parseFields(aValue,type);
									field.set(b, bValue);
								}
							}catch(Exception e){
								// nothing
							}
						}
					}
				}
			}
		}
	}
	
	private static Object convertCollection(Class type,List list){
		if(type.isArray()){
			return list.toArray();
		}
		if(type==List.class){
			return list;
		}
		if(type==Set.class){
			Set set = new HashSet();
			for(Object obj:list){
				set.add(obj);
			}
			return set;
		}
		return null;
	}
	
	public static boolean isCandidateObject(Class type){
		return !isJavaLangType(type)&&!isCollection(type)&&!isMap(type);
	}
	
	public static boolean isJavaLangType(Class type){
		return isPrimitiveType(type)
				||type==Integer.class||type==Long.class||type==Short.class
				||type==Byte.class||type==Boolean.class||type==Character.class
				||type==Double.class||type==Float.class
				||type==String.class;
	}
	
	public static boolean isPrimitiveType(Class type){
		return type==int.class||type==long.class||type==short.class
				||type==byte.class||type==boolean.class||type==char.class
				||type==double.class||type==float.class;
	}
	
	private static Class getCollectionItemType(Field f){
		Type genericType = f.getGenericType();
		if(genericType instanceof ParameterizedType){
			ParameterizedType p = (ParameterizedType)genericType;
			Type[] types = p.getActualTypeArguments();
			for(Type t:types){
				Class clazz = (Class)t;
				return clazz;
			}
		}
		return null;
	}
	
	private static boolean isCollection(Class type){
		return type.isArray()||type==List.class||type==Set.class
				||type==ArrayList.class||type==LinkedList.class||type==HashSet.class;
	}
	
	public static boolean isMap(Class type){
		return type==Map.class||type==HashMap.class||type==TreeMap.class||type==Hashtable.class;
	}
	
	public static <A,B> B parseFields(A a,Class<B> b){
		if(null == a){
			return null;
		}
		
		try {
			if(a.getClass()==b){
				return (B)a;
			}
			B instance = b.newInstance();
			parseFields(a,instance);
			return instance;
		} catch (InstantiationException e) {
			// nothing
		} catch (IllegalAccessException e) {
			// nothing
		}
		return null;
	}
	
	public static <A,B> List<B> parseFields(List<A> as,Class<B> b){
		ArrayList<B> result = new ArrayList<B>();
		if(as!=null&&as.size()>0){
			for(A a:as){
				B b2 = parseFields(a,b);
				result.add(b2);
			}
		}
		return result;
	}
	
	public static <T> T mapToBean(Map<String,Object> map,Class<T> clazz){
		
		try {
			T instance = clazz.newInstance();
			Set<String> keys = map.keySet();
			for(String key:keys){
				
				try{
					Field field = clazz.getDeclaredField(key);
					if(field!=null){
						field.setAccessible(true);
						Object value = map.get(key);
						
						//修正类型不一样的，避免注入错误
						Object fixedValue = parseField(field.getType(),value);
						
						if(fixedValue!=null){
							field.set(instance, fixedValue);
						}
					}
				}catch(NoSuchFieldException e){
					
				}catch(SecurityException e){
					
				}
			}
			return instance;
			
		} catch (InstantiationException e) {
			String log = genLog(map, clazz);
			throw new DaoCacheException(log+" "+e.getMessage(),e);
		} catch (IllegalAccessException e) {
			String log = genLog(map, clazz);
			throw new DaoCacheException(log+" "+e.getMessage(),e);
		}
	}
	
	public static <T> T strMapToBean(Map<String,String> map,Class<T> clazz){
		
		try {
			T instance = clazz.newInstance();
			Set<String> keys = map.keySet();
			for(String key:keys){
				
				try{
					Field field = clazz.getDeclaredField(key);
					if(field!=null){
						field.setAccessible(true);
						Object value = map.get(key);
						
						//修正类型不一样的，避免注入错误
						Object fixedValue = parseField(field.getType(),value);
						
						if(fixedValue!=null){
							field.set(instance, fixedValue);
						}
					}
				}catch(NoSuchFieldException e){
					
				}catch(SecurityException e){
					
				}
			}
			return instance;
			
		} catch (InstantiationException e) {
			String log = genStrMapLog(map, clazz);
			throw new DaoCacheException(log+" "+e.getMessage(),e);
		} catch (IllegalAccessException e) {
			String log = genStrMapLog(map, clazz);
			throw new DaoCacheException(log+" "+e.getMessage(),e);
		}
	}

	private static String genStrMapLog(Map<String,String> map,Class<?> clazz){
		
		return null;
	}
	
	private static String genLog(Map<String,Object> map,Class<?> clazz){
		
		return null;
	}
	
	public static Object parseField(Class type,Object value){
		if(value==null){
			return null;
		}
		if(value.getClass()==type){
			return value;
		}else{
			if(isJavaLangType(value.getClass())){
				String stringValue = value.toString();
				if(type==Integer.class||type==int.class){
					if(StringUtils.isBlank(stringValue)){
						return Integer.valueOf(0);
					}else{
						return Integer.valueOf(stringValue);
					}
				}else if(type==Short.class||type==short.class){
					if(StringUtils.isBlank(stringValue)){
						return Short.valueOf((short) 0);
					}else{
						return Short.valueOf(stringValue);
					}
				}else if(type==Boolean.class||type==boolean.class){
					if(StringUtils.isBlank(stringValue)){
						return false;
					}else{
						return Boolean.valueOf(stringValue);
					}
					
				}else if(type==Long.class||type==long.class){
					if(StringUtils.isBlank(stringValue)){
						return Long.valueOf(0l);
					}else{
						return Long.valueOf(stringValue);
					}
				}else if(type==String.class){
					return stringValue;
				}else if(type==Float.class||type==float.class){
					if(StringUtils.isBlank(stringValue)){
						return Float.valueOf(0.0f);
					}else{
						return Float.valueOf(stringValue);
					}
					
				}else if(type==Double.class||type==double.class){
					if(StringUtils.isBlank(stringValue)){
						return Double.valueOf(0.0d);
					}else{
						return Double.valueOf(stringValue);
					}
				}else if(type==Character.class||type==char.class){
					if(StringUtils.isBlank(stringValue)){
						return ' ';
					}else{
						return stringValue.trim().charAt(0);
					}
				}
				return null;
			}else{
				return null;
			}
		}
	}
	
	public static Map<String,Object> beanToMap(Object arg,boolean dbfield){
		if(arg!=null){
			HashMap<String, Object> beanMap = new HashMap<String,Object>();
			Field[] fields = arg.getClass().getDeclaredFields();
			for(Field f:fields){
				if(dbfield){
					PrimaryKey primary = f.getAnnotation(PrimaryKey.class);
					Column c = f.getAnnotation(Column.class);
					if(primary==null&&c==null){
						continue;
					}
				}
				
				f.setAccessible(true);
				try {
					beanMap.put(f.getName(), f.get(arg));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			return beanMap;
		}
		return null;
	}

	public static <A,B> void parseFields(List<A> as,List<B> bs){
		if(as!=null&&as.size()>0&&bs!=null&&bs.size()==as.size()){
			int size = as.size();
			int idx = 0;
			while(idx<size){
				A a = as.get(idx);
				B b = bs.get(idx);
				parseFields(a,b);
				idx++;
			}
		}
	}
	
	private static String collection2String(Object obj,Map<Object,Integer> refCount){
		StringBuilder sb = new StringBuilder();
		if(obj==null){
			return "";
		}
		if(obj.getClass().isArray()){
			Class<?> componentType = obj.getClass().getComponentType();
			if(BeanUtils.isPrimitiveType(componentType)){
				if(componentType==int.class){
					sb.append(Arrays.toString((int[])obj));
				}else if(componentType==short.class){
					sb.append(Arrays.toString((short[])obj));
				}else if(componentType==long.class){
					sb.append(Arrays.toString((long[])obj));
				}else if(componentType==double.class){
					sb.append(Arrays.toString((double[])obj));
				}else if(componentType==char.class){
					sb.append(Arrays.toString((char[])obj));
				}else if(componentType==byte.class){
					byte[] arr = (byte[])obj;
					sb.append("bytes["+arr.length+"]");
				}else if(componentType==boolean.class){
					sb.append(Arrays.toString((boolean[])obj));
				}else if(componentType==float.class){
					sb.append(Arrays.toString((float[])obj));
				}
			}else{
				if(componentType==Byte.class){
					Byte[] arr = (Byte[])obj;
					sb.append("Bytes["+arr.length+"]");
				}else{
					Object[] arr = (Object[])obj;
					boolean first = true;
					for(Object a:arr){
						if(!first){
							sb.append(",");
						}
						if(a!=null){
							sb.append(value2String(a,refCount));
						}else{
							sb.append("null");
						}
						first = false;
					}
				}
			}
		}else{
			Collection c = (Collection)obj;
			Iterator it = c.iterator();
			boolean first = true;
			while(it.hasNext()){
				Object a = it.next();
				if(!first){
					sb.append(",");
				}
				if(a!=null){
					sb.append(value2String(a,refCount));
				}else{
					sb.append("null");
				}
				first = false;
			}
		}
		return sb.toString();
	}
	
	private static String map2String(Object obj,Map<Object,Integer> refCount){
		StringBuilder sb = new StringBuilder();
		if(obj==null){
			return "";
		}
		sb.append("{");
		Map map = (Map)obj;
		Set keys = map.keySet();
		for(Object key:keys){
			String keyString = value2String(key,refCount);
			String valueString = value2String(map.get(key),refCount);
			sb.append(keyString);
			sb.append(":");
			sb.append(valueString);
			sb.append(",");
		}
		sb.append("}");
		return sb.toString();
	}
	
	private static String obj2String(Object obj,Map<Object,Integer> refCount){
		StringBuilder sb = new StringBuilder();
		if(obj==null){
			return "";
		}
		
		if(obj.getClass().equals(Object.class)){
			return "";
		}
		if(obj instanceof Class){
			return "";
		}
		
		//防止对象多次引用
		Integer cc = refCount.get(obj);
		if(cc==null){
			cc = 0;
		}
		if(cc>0){
			return "ref_obj";
		}
		cc++;
		refCount.put(obj, cc);
		
		sb.append(obj.getClass().getSimpleName());
		sb.append("{");
		Field[] fields = obj.getClass().getDeclaredFields();
		for(Field f:fields){
			int modifiers = f.getModifiers();
			if(Modifier.isStatic(modifiers)||Modifier.isFinal(modifiers)){
				continue;
			}
			f.setAccessible(true);
			try {
				Object value = f.get(obj);
				String valueString = value2String(value,refCount);
				sb.append(f.getName());
				sb.append(":");
				sb.append(valueString);
				sb.append(",");
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}
		}
		sb.append("}");
		return sb.toString();
	}
	
	public static String valueToString(Object obj){
		HashMap<Object, Integer> ref = new HashMap<Object,Integer>();
		return value2String(obj,ref);
	}
	
	/**
	 * 任意对象转String，可搜索
	 * @param obj
	 * @return
	 */
	private static String value2String(Object obj,Map<Object,Integer> refCount){
		if(obj==null){
			return "";
		}
		Class<? extends Object> objClass = obj.getClass();
		if(obj.getClass().isEnum()){
			return obj.toString();
		}
		
		if(isJavaLangType(objClass)){
			return obj.toString();
		}else if(isCollection(objClass)){
			return collection2String(obj,refCount);
		}else if(isMap(objClass)){
			return map2String(obj,refCount);
		}else if(isCandidateObject(objClass)){
			return obj2String(obj,refCount);
		}else{
			return obj.toString();
		}
	}
}
