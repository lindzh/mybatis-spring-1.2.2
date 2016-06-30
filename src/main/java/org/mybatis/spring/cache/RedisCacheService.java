package org.mybatis.spring.cache;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

/**
 * 
 * @author lindezhi
 * 2015年12月4日 上午11:26:22
 */
public class RedisCacheService {
	
	private JedisClientService jedisClientService;
	
	public static final int EXPIRE_SEC = 1;
	
	public static final int EXPIRE_SEC_10 = EXPIRE_SEC*10;
	
	public static final int EXPIRE_MINUTE = 60;
	
	public static final int EXPIRE_MINUTE_2 = EXPIRE_MINUTE*2;
	
	public static final int EXPIRE_MINUTE_5 = EXPIRE_MINUTE*5;
	
	public static final int EXPIRE_MINUTE_10 = EXPIRE_MINUTE*10;
	
	public static final int EXPIRE_HALF_HOUR = EXPIRE_MINUTE*30;
	
	public static final int EXPIRE_HOUR = EXPIRE_MINUTE*60;
	
	public static final int EXPIRE_DAY = EXPIRE_HOUR*24;
	
	public static final int EXPIRE_DAY_3 = EXPIRE_DAY*3;
	
	public static final int EXPIRE_WEEK = EXPIRE_DAY*7;
	
	public static final int EXPIRE_MONTH = EXPIRE_DAY*30;
	
	public static interface RedisExecutor<T>{
	
		public T execute(Jedis jedis);
	
	}
	
	/**
	 * 修复数据使用，一般不要使用
	 * @param pattern
	 * @return
	 */
	@Deprecated
	public Set<String> keys(final String pattern){
		return this.redisOp(new RedisExecutor<Set<String>>(){
			@Override
			public Set<String> execute(Jedis jedis) {
				Set<String> keys = jedis.keys(pattern);
				return keys;
			}
			
		}, pattern);
	}
	
	
	private <T> T redisOp(RedisExecutor<T> executor,Object ... args){
		Jedis jedis = jedisClientService.getResource();
		try{
			T result = executor.execute(jedis);
			jedisClientService.returnResource(jedis);
			return result;
		}catch(Exception e){
			String log = this.genLog(args);
			jedisClientService.returnBrokenResource(jedis);
			throw new DaoCacheException(log,e);
		}
	}
	
	private String genLog(Object ...args){
		StringBuilder sb = new StringBuilder();
		sb.append("[Redis] args:");
		for(Object arg:args){
			sb.append(arg+" ");
		}
		return sb.toString();
	}
	
	private byte[] genKey(String key){
		try {
			return key.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			//can't happen
			return key.getBytes();
		}
	}
	
	private String byte2String(byte[] key){
		try {
			return new String(key,"utf-8");
		} catch (UnsupportedEncodingException e) {
			return new String(key);
		}
	}
	
	private void setObject(final String key,final Object value){
		this.redisOp(new RedisExecutor<Void>(){
			@Override
			public Void execute(Jedis jedis) {
				byte[] genKey = genKey(key);
				byte[] valueBytes = SerializeUtils.serialize(value);
				jedis.set(genKey, valueBytes);
				return null;
			}
		}, key,value);
	}
	
	public void setObject(final String key,final Object value,final int expire){
		this.redisOp(new RedisExecutor<Void>(){
			@Override
			public Void execute(Jedis jedis) {
				byte[] genKey = genKey(key);
				byte[] valueBytes = SerializeUtils.serialize(value);
				jedis.set(genKey, valueBytes);
				jedis.expire(genKey, expire);
				return null;
			}
		},"set",key,value,expire);
	}

	public Object getObject(final String key){
		return this.redisOp(new RedisExecutor<Object>(){
			@Override
			public Object execute(Jedis jedis) {
				byte[] genKey = genKey(key);
				byte[] bs = jedis.get(genKey);
				Object res = null;
				if(bs!=null){
					res = SerializeUtils.deserialize(bs);
				}
				return res;
			}
		}, "get",key);
	}
	
	public long incr(final String key,final int expire){
		return this.redisOp(new RedisExecutor<Long>(){
			@Override
			public Long execute(Jedis jedis) {
				Long ttl = jedis.ttl(key);
				if(ttl==null||ttl<=0){
					Long vv = jedis.incr(key);
					if(vv!=null&&vv<3){
						jedis.expire(key, expire);
						return vv;
					}
					return 0L;
				}else{
					Long vv = jedis.incr(key);
					if(vv!=null){
						return vv;
					}else{
						return 0L;
					}
				}
			}
		}, key,expire);
	}
	
	public void setLong(final String key,final long value,final int expire){
		this.redisOp(new RedisExecutor<Void>(){
			@Override
			public Void execute(Jedis jedis) {
				byte[] genKey = genKey(key);
				String v = String.valueOf(value);
				byte[] valueBytes = SerializeUtils.serialize(v);
				jedis.set(genKey, valueBytes);
				jedis.expire(genKey, expire);
				return null;
			}
		}, "set",key,value,expire);
	}

	public long getLong(final String key){
		return this.redisOp(new RedisExecutor<Long>(){
			@Override
			public Long execute(Jedis jedis) {
				byte[] genKey = genKey(key);
				byte[] bs = jedis.get(genKey);
				long res = 0;
				if(bs!=null){
					String vv = new String(bs);
					res = Long.parseLong(vv);
				}
				return res;
			}
		}, "get",key);
	}
	
	public void setInt(final String key,final int value,final int expire){
		this.setLong(key, value, expire);
	}

	public int getInt(final String key){
		return (int)this.getLong(key);
	}
	
	public void setBoolean(final String key,final boolean value,final int expire){
		if(value){
			this.setInt(key, 1, expire);
		}else{
			this.setInt(key, 0, expire);
		}
	}

	public boolean getBoolean(final String key){
		int vv = this.getInt(key);
		return vv>0;
	}
	
	public void setDouble(final String key,final double value,final int expire){
		this.redisOp(new RedisExecutor<Void>(){
			@Override
			public Void execute(Jedis jedis) {
				byte[] genKey = genKey(key);
				String v = String.valueOf(value);
				byte[] valueBytes = SerializeUtils.serialize(v);
				jedis.set(genKey, valueBytes);
				jedis.expire(genKey, expire);
				return null;
			}
		}, "set",key,value,expire);
	}

	public double getDouble(final String key){
		return this.redisOp(new RedisExecutor<Double>(){
			@Override
			public Double execute(Jedis jedis) {
				byte[] genKey = genKey(key);
				byte[] bs = jedis.get(genKey);
				double res = 0;
				if(bs!=null){
					String vv = new String(bs);
					res = Double.parseDouble(vv);
				}
				return res;
			}
		}, "get",key);
	}
	
	public void setFloat(final String key,final float value,final int expire){
		this.setDouble(key, value, expire);
	}

	public float getFloat(final String key){
		return (float)this.getDouble(key);
	}
	
	public void setString(final String key,final String value,final int expire){
		this.redisOp(new RedisExecutor<Void>(){
			@Override
			public Void execute(Jedis jedis) {
				byte[] genKey = genKey(key);
				byte[] valueBytes = new byte[0];
				try {
					valueBytes = value.getBytes("utf-8");
				} catch (UnsupportedEncodingException e) {
					//can't happen
				}
				jedis.set(genKey, valueBytes);
				jedis.expire(genKey, expire);
				return null;
			}
		}, "set",key,value,expire);
	}
	
	public Set<String> sget(final String set){
		return this.redisOp(new RedisExecutor<Set<String>>(){
			@Override
			public Set<String> execute(Jedis jedis) {
				Set<String> values = jedis.smembers(set);
				if(values!=null){
					return values;
				}
				return Collections.emptySet();
			}
		}, "smembers",set);
	}
	
	public void sadd(final String set,final int expire,final String ...values){
		this.redisOp(new RedisExecutor<Void>(){
			@Override
			public Void execute(Jedis jedis) {
				jedis.sadd(set, values);
				jedis.expire(set, expire);
				return null;
			}
		}, "sadd",set,values);
	}
	
	public void sremove(final String set,final int expire,final String ... values){
		this.redisOp(new RedisExecutor<Void>(){
			@Override
			public Void execute(Jedis jedis) {
				jedis.srem(set, values);
				jedis.expire(set, expire);
				return null;
			}
		}, "srem",set,values);
	}
	
	public void expire(final String key,final int seconds){
		this.redisOp(new RedisExecutor<Void>(){
			@Override
			public Void execute(Jedis jedis) {
				jedis.expire(key, seconds);
				return null;
			}
		}, "expire",key,seconds);
	}

	public String getString(final String key){
		return this.redisOp(new RedisExecutor<String>(){
			@Override
			public String execute(Jedis jedis) {
				byte[] genKey = genKey(key);
				byte[] bs = jedis.get(genKey);
				String vv = null;
				if(bs!=null){
					try {
						vv = new String(bs,"utf-8");
					} catch (UnsupportedEncodingException e) {
						//can't happen
					}
				}
				return vv;
			}
		}, "get",key);
	}
	
	/**
	 * 删除缓存
	 * @param key
	 */
	public void del(final String key){
		this.redisOp(new RedisExecutor<Void>(){
			@Override
			public Void execute(Jedis jedis) {
				byte[] genKey = genKey(key);
				jedis.del(genKey);
				return null;
			}
		}, "del",key);
	}
	
	/**
	 * 批量删除缓存
	 * @param keys
	 */
	public void del(final Set<String> keys){
		this.redisOp(new RedisExecutor<Void>(){
			@Override
			public Void execute(Jedis jedis) {
				HashSet<byte[]> set = new HashSet<byte[]>();
				for(String key:keys){
					byte[] genKey = genKey(key);
					set.add(genKey);
				}
				jedis.del(set.toArray(new byte[0][0]));
				return null;
			}
		}, "del",keys);
	}
	
	/**
	 * 多个key get
	 * @param keys
	 * @return
	 */
	public Map<String,Object> mget(final Collection<String> keys){
		return this.redisOp(new RedisExecutor<Map<String,Object>>(){
			@Override
			public Map<String,Object> execute(Jedis jedis) {
				HashMap<String, Object> result = new HashMap<String,Object>();
				HashSet<byte[]> set = new HashSet<byte[]>();
				for(String key:keys){
					byte[] genKey = genKey(key);
					set.add(genKey);
				}
				byte[][] keyArr = set.toArray(new byte[0][0]);
				List<byte[]> values = jedis.mget(keyArr);
				if(values.size()>0&&values.size()==keys.size()){
					for(int i=0;i<values.size();i++){
						String key = byte2String(keyArr[i]);
						Object value = null;
						if(values.get(i)!=null){
							value = SerializeUtils.deserialize(values.get(i));
						}
						result.put(key, value);
					}
				}
				return result;
			}
		}, "mget",keys);
	}
	
	
	public JedisClientService getJedisClientService() {
		return jedisClientService;
	}

	public void setJedisClientService(JedisClientService jedisClientService) {
		this.jedisClientService = jedisClientService;
	}

	public static class NullObject implements Serializable{

		private static final long serialVersionUID = 882419175555440980L;
		
	}
}
