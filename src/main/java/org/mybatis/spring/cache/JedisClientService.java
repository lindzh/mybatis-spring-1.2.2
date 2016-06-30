package org.mybatis.spring.cache;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

/**
 * 
 * @author lindezhi
 * 2016年6月29日 下午5:35:08
 */
public class JedisClientService {
	
	private JedisPoolConfig poolConfig;
	
	private String host;
	
	private int port;
	
	private String sentinels;
	
	private String masterName;
	
	/**
	 * 启动时生成
	 */
	private JedisSentinelPool sentinelPool;
	
	/**
	 * 启动时生成
	 */
	private JedisPool jedisPool;
	
	private int timeout = 10000;
	
	private Log logger = LogFactory.getLog("REDIS_CACHE");
	
	/**
	 * 开启连接
	 */
	public void start(){
		if(StringUtils.isNotBlank(sentinels)&&StringUtils.isNotBlank(masterName)){
			String[] sentinelServers = sentinels.split(";");
			HashSet<String> set = new HashSet<String>();
			set.addAll(Arrays.asList(sentinelServers));
			sentinelPool = new JedisSentinelPool(masterName,set,poolConfig,timeout);
		}else{
			jedisPool = new JedisPool(poolConfig,host,port);
		}
	}
	
	/**
	 * 结束连接
	 */
	public void stop(){
		if(jedisPool!=null){
			jedisPool.destroy();
		}
		if(sentinelPool!=null){
			sentinelPool.destroy();
		}
	}
	
	
	/**
	 * 资源回收
	 * @return
	 */
    public Jedis getResource() {
		if(jedisPool!=null){
			return jedisPool.getResource();
		}
		if(sentinelPool!=null){
			return sentinelPool.getResource();
		}
		throw new DaoCacheException("can't find any jedis pool");
    }

    /**
     * 资源回收
     * @param resource
     */
    public void returnBrokenResource(final Jedis resource) {
		if(jedisPool!=null){
			jedisPool.returnBrokenResource(resource);
			return;
		}
		if(sentinelPool!=null){
			sentinelPool.returnBrokenResource(resource);
			return;
		}
		throw new DaoCacheException("can't find any jedis pool");
    }

    /**
     * 资源回收
     * @param resource
     */
    public void returnResource(final Jedis resource) {
		if(jedisPool!=null){
			jedisPool.returnResource(resource);
			return;
		}
		if(sentinelPool!=null){
			sentinelPool.returnResource(resource);
			return;
		}
		throw new DaoCacheException("can't find any jedis pool");
    }
}
