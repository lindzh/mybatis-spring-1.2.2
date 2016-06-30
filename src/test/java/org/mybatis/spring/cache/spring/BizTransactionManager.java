package org.mybatis.spring.cache.spring;

import java.util.Set;

import org.mybatis.spring.cache.CacheContext;
import org.mybatis.spring.cache.RedisCacheService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.util.CollectionUtils;

/**
 * 
 * @author lindezhi
 * 2015年11月18日 下午11:14:30
 */
public class BizTransactionManager extends DataSourceTransactionManager implements ApplicationContextAware{

	private static final long serialVersionUID = -3177438254181907166L;
	
	private RedisCacheService redisCacheService;
	
	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) {
		super.doBegin(transaction, definition);
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) {
		super.doCommit(status);
		this.clearKeys();
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status) {
		super.doRollback(status);
		this.clearKeys();
	}
	
	/**
	 * 清除缓存
	 */
	private void clearKeys(){
		Set<String> keys = CacheContext.getKeys();
		if(!CollectionUtils.isEmpty(keys)){
			redisCacheService.del(keys);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
	}

	public RedisCacheService getRedisCacheService() {
		return redisCacheService;
	}

	public void setRedisCacheService(RedisCacheService redisCacheService) {
		this.redisCacheService = redisCacheService;
	}
	
}
