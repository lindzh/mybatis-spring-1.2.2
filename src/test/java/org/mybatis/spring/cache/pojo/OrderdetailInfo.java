package org.mybatis.spring.cache.pojo;

import java.io.Serializable;

import lombok.Data;

import org.springframework.util.StopWatch.TaskInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.linda.common.mybatis.generator.annotation.Column;
import com.linda.common.mybatis.generator.annotation.Index;
import com.linda.common.mybatis.generator.annotation.PrimaryKey;
import com.linda.common.mybatis.generator.annotation.Table;

@Data
@Table(name="orderdetail",autoGeneratePrimaryKey=true)
@JsonIgnoreProperties(ignoreUnknown=true)
public class OrderdetailInfo  implements Serializable{

	private static final long serialVersionUID = -2764028222269714798L;

	@PrimaryKey
	private long id;
	
	@Column
	private long team_id;
	
	@Column
	private int quantity;
	
	@Column
	private double partner_price;
	
	@Column
	private double team_price;
	
	@Column
	@Index(name="partnerIdAndState")
	private long partner_id;
	
	@Column
	private long distribution_id;
	
	@Column
	private int replenish;
	
	@Column
	@Index(name="orderId")
	private long order_id;
	
	@Column
	@Index(name="partnerIdAndState")
	private String state="";
	
	@Column
	private long create_time;
	
	@Column
	private long fbi;
	
	@Column
	private long user_id;
	
	@Column
	private String udid;
	
	@Column
	private String split_number = "";
	
	@Column
	private long activity_id;
	
	@Column
	private String team_title = "";

	@Column
	private String buyer_id;
	
	@Column
	private String openid;

	@Column
	private long community_id;
	
	@Column
	private int clear_id;
	
	@Column
	private int presale;
	
//	@Column
//	private int presale;
	//明细对应商品信息
	private TeamInfo team;
	
	private OrderInfo orderInfo;
	
	private TaskInfo taskInfo;
	//订单
	private OrderInfo order;
	
	//用于定时任务中同一个小区，同一种商品，同一个配送时间的count
	private int count;
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderdetailInfo other = (OrderdetailInfo) obj;
		if (buyer_id == null) {
			if (other.buyer_id != null)
				return false;
		} else if (!buyer_id.equals(other.buyer_id))
			return false;
		if (clear_id != other.clear_id)
			return false;
		if (community_id != other.community_id)
			return false;
		if (count != other.count)
			return false;
		if (create_time != other.create_time)
			return false;
		if (distribution_id != other.distribution_id)
			return false;
		if (fbi != other.fbi)
			return false;
		if (id != other.id)
			return false;
		if (openid == null) {
			if (other.openid != null)
				return false;
		} else if (!openid.equals(other.openid))
			return false;
		if (order_id != other.order_id)
			return false;
		if (partner_id != other.partner_id)
			return false;
		if (Double.doubleToLongBits(partner_price) != Double.doubleToLongBits(other.partner_price))
			return false;
		if (quantity != other.quantity)
			return false;
		if (replenish != other.replenish)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (team_id != other.team_id)
			return false;
		if (Double.doubleToLongBits(team_price) != Double.doubleToLongBits(other.team_price))
			return false;
		if (udid == null) {
			if (other.udid != null)
				return false;
		} else if (!udid.equals(other.udid))
			return false;
		if (user_id != other.user_id)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buyer_id == null) ? 0 : buyer_id.hashCode());
		result = prime * result + clear_id;
		result = prime * result + (int) (community_id ^ (community_id >>> 32));
		result = prime * result + count;
		result = prime * result + (int) (create_time ^ (create_time >>> 32));
		result = prime * result + (int) (distribution_id ^ (distribution_id >>> 32));
		result = prime * result + (int) (fbi ^ (fbi >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((openid == null) ? 0 : openid.hashCode());
		result = prime * result + (int) (order_id ^ (order_id >>> 32));
		result = prime * result + (int) (partner_id ^ (partner_id >>> 32));
		long temp;
		temp = Double.doubleToLongBits(partner_price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + quantity;
		result = prime * result + replenish;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + (int) (team_id ^ (team_id >>> 32));
		temp = Double.doubleToLongBits(team_price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((udid == null) ? 0 : udid.hashCode());
		result = prime * result + (int) (user_id ^ (user_id >>> 32));
		return result;
	}
}
