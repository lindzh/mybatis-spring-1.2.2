package org.mybatis.spring.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * redis dao cache
 * @author lindezhi
 * 2015年12月4日 下午5:11:23
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {
	
	
	/**
	 * 操作类型
	 * @return
	 */
	OperateType operate();
	
	/**
	 * 
	 * @return
	 */
	String key() default "";
	
	/**
	 * cache前缀
	 * @return
	 */
	String prefix() default "";
	
	/**
	 * 关联cache
	 * @return
	 */
	String refPrefix() default "";
	
	/**
	 * 关联key
	 * @return
	 */
	String refKey() default "";
	
	int ttl() default RedisCacheService.EXPIRE_DAY;

}
