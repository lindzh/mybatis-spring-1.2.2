package org.mybatis.spring.cache;

/**
 * 缓存更新上下文，指定需要更新的地方，方便php java缓存同步
 * @author lindezhi
 * 2015年12月10日 上午1:40:36
 */
public class RedisCacheContext {
	
	private static ThreadLocal<Integer> update = new ThreadLocal<Integer>();
	
	private static ThreadLocal<Integer> select = new ThreadLocal<Integer>();
	
	public static int getOp(){
		Integer op = update.get();
		if(op!=null){
			return op;
		}else{
			op = UpdateOp.UPDATE_CACHE_AND_DB;
			update.set(op);
		}
		return op;
	}
	
	public static void setOp(int op){
		update.set(op);
	}
	
	public static void clear(){
		update.remove();
		select.remove();
	}
	
	public static void setSelectOp(int op){
		select.set(op);
	}
	
	public static int getSelectOp(){
		Integer op = select.get();
		if(op!=null){
			return op;
		}else{
			op = SelectOp.SELECT_CACHE_OR_DB;
			select.set(op);
		}
		return op;
	}

}
