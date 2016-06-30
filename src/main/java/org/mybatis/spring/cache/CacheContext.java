package org.mybatis.spring.cache;

import java.util.HashSet;
import java.util.Set;

import org.springframework.util.CollectionUtils;

/**
 * 修正事物引起的缓存异常
 * @author lindezhi
 * 2016年6月21日 下午3:48:07
 */
public class CacheContext {
	
	private static ThreadLocal<HashSet<String>> cacheKeys = new ThreadLocal<HashSet<String>>(){
		@Override
		protected HashSet<String> initialValue() {
			return new HashSet<String>();
		}
	};
	
	public static Set<String> getKeys(){
		return cacheKeys.get();
	}
	
	public static void addKeys(Set<String> keys){
		if(!CollectionUtils.isEmpty(keys)){
			cacheKeys.get().addAll(keys);
		}
	}
	
	public static void addKey(String key){
		if(StringUtils.isNotBlank(key)){
			cacheKeys.get().add(key);
		}
	}

	public static void clear(){
		cacheKeys.remove();
	}
}
