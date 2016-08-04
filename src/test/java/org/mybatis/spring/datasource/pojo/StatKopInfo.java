package org.mybatis.spring.datasource.pojo;

import lombok.Data;

import com.linda.common.mybatis.generator.annotation.Column;
import com.linda.common.mybatis.generator.annotation.PrimaryKey;
import com.linda.common.mybatis.generator.annotation.Table;

/**
 * api访问统计 example
 * @author lindezhi
 * 2016年8月4日 上午10:40:05
 */
@Data
@Table(name="koa_req_stat",autoGeneratePrimaryKey=true)
public class StatKopInfo {
	
	@PrimaryKey
	private long id;
	
	@Column
	private String api_name;
	
	@Column
	private String visit_time;
	
	@Column
	private int max_times;
	
	@Column
	private int min_times;
	
	@Column
	private double avg_times;
	
	@Column
	private int request_count;
	
	@Column
	private int success_count;
	
	@Column
	private int fail_count;
	
	@Column
	private String unique_flag;

}
