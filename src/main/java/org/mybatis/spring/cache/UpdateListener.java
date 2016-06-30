package org.mybatis.spring.cache;

import java.lang.reflect.Method;

/**
 * 
 * @author lindezhi
 * 2016年6月29日 下午5:26:48
 */
public interface UpdateListener {
	
	public void onUpdate(Method method,String table,String id,Object olddata);

}
