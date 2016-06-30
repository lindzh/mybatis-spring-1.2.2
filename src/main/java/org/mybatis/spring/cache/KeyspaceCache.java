package org.mybatis.spring.cache;

import java.util.List;


/**
 * 
 * @author lindezhi
 * 2015年12月5日 下午6:05:59
 */
public class KeyspaceCache {
	
	/**
	 * cache字段
	 */
	private String key;
	
	/**
	 * cache前缀
	 */
	private String prefix;
	
	/**
	 * refcache前缀 affected
	 */
	private String refPrefix;
	
	/**
	 * refkey
	 */
	private String refKey;
	
	/**
	 * 添加
	 */
	private MethodCache insert;
	
	/**
	 * 更新
	 */
	private MethodCache update;
	
	/**
	 * 删除
	 */
	private MethodCache delete;
	
	/**
	 * 查询
	 */
	private MethodCache select;
	
	/**
	 * 批量查询
	 */
	private MethodCache multiSelect;
	
	/**
	 * 更新关联
	 */
	private List<MethodCache> refSelects;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getRefPrefix() {
		return refPrefix;
	}

	public void setRefPrefix(String refPrefix) {
		this.refPrefix = refPrefix;
	}

	public String getRefKey() {
		return refKey;
	}

	public void setRefKey(String refKey) {
		this.refKey = refKey;
	}

	public MethodCache getInsert() {
		return insert;
	}

	public void setInsert(MethodCache insert) {
		this.insert = insert;
	}

	public MethodCache getUpdate() {
		return update;
	}

	public void setUpdate(MethodCache update) {
		this.update = update;
	}

	public MethodCache getDelete() {
		return delete;
	}

	public void setDelete(MethodCache delete) {
		this.delete = delete;
	}

	public MethodCache getSelect() {
		return select;
	}

	public void setSelect(MethodCache select) {
		this.select = select;
	}

	public MethodCache getMultiSelect() {
		return multiSelect;
	}

	public void setMultiSelect(MethodCache multiSelect) {
		this.multiSelect = multiSelect;
	}

	public List<MethodCache> getRefSelects() {
		return refSelects;
	}

	public void setRefSelects(List<MethodCache> refSelects) {
		this.refSelects = refSelects;
	}
}
