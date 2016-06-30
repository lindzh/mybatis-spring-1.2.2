package org.mybatis.spring.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author lindezhi
 * 2015年12月10日 上午10:56:52
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheVersion {
	
	String value() default "v1.0";
	
	boolean open() default true;
	
}
