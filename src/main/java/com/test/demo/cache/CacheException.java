package com.test.demo.cache;

/**
 * 缓存异常
 * @author Winter Lau
 */
public class CacheException extends RuntimeException {

	/**
	 * v
	 */
	private static final long serialVersionUID = 810696685182096507L;

	public CacheException(String s) {
		super(s);
	}

	public CacheException(String s, Throwable e) {
		super(s, e);
	}

	public CacheException(Throwable e) {
		super(e);
	}
	
}
