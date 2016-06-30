package org.mybatis.spring.cache;

/**
 * db操作指定
 * @author lindezhi
 * 2015年12月10日 上午1:41:14
 */
public interface UpdateOp {

	public static final int UPDATE_CACHE_AND_DB = 0;
	
	public static final int UPDATE_CACHE = 1;
	
	public static final int UPDATE_DB = 2;
	
	

}
