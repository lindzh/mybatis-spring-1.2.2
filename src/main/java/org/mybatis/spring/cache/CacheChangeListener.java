package org.mybatis.spring.cache;

import java.util.Map;

/**
 * 
 * @author lindezhi
 * 2015年12月14日 下午8:49:27
 */
public interface CacheChangeListener {
	
	/**
	 * table改变消息
	 * @param id
	 * @param params
	 */
	public void onChange(String table,String id,Map<String,Object> olddata);

}
