package org.mybatis.spring.cache;

import java.lang.reflect.Method;

/**
 * 
 * @author lindezhi
 * 2015年12月5日 下午6:04:59
 */
public class MethodCache {
	
	private RedisCache cacheDef;
	
	private Method method;
	
	public OperateType operate() {
		return cacheDef.operate();
	}

	public String key() {
		return cacheDef.key();
	}

	public String prefix() {
		return cacheDef.prefix();
	}

	public String refPrefix() {
		return cacheDef.refPrefix();
	}

	public String refKey() {
		return cacheDef.refKey();
	}

	public RedisCache getCacheDef() {
		return cacheDef;
	}

	public void setCacheDef(RedisCache cacheDef) {
		this.cacheDef = cacheDef;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
	
}
