package org.mybatis.spring.datasource;


/**
 * 数据源类型
 * @author lindezhi
 * 2016年7月20日 上午10:13:42
 */
public class DataSourceContextHolder {
	
	/**
	 * 主要的
	 */
	public static final String DATA_SOURCE_MAIN = "main";
	
	/**
	 * 统计
	 */
	public static final String DATA_SOURCE_STAT = "stat";
	
	private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>(){
		@Override
		protected String initialValue() {
			return DATA_SOURCE_MAIN;
		}
	};  
    
    public static void setContextType(String contextType) {  
        contextHolder.set(contextType);  
    }  
      
    public static String getContextType() {  
        return contextHolder.get();  
    }  
      
    public static void clearContextType() {  
        contextHolder.remove();  
    }

}
