package org.mybatis.spring.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 
 * @author lindezhi
 * 2016年7月20日 上午10:33:42
 */
public class SimpleDataSource extends AbstractRoutingDataSource{

	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceContextHolder.getContextType();
	}

}
