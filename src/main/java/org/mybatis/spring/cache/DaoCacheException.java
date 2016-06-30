package org.mybatis.spring.cache;

/**
 * 
 * @author lindezhi
 * 2015年11月12日 下午4:22:23
 */
public class DaoCacheException extends RuntimeException{

	private static final long serialVersionUID = -6539577349912221921L;

	public DaoCacheException() {
		super();
	}

	public DaoCacheException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DaoCacheException(String message, Throwable cause) {
		super(message, cause);
	}

	public DaoCacheException(String message) {
		super(message);
	}

	public DaoCacheException(Throwable cause) {
		super(cause);
	}
}
