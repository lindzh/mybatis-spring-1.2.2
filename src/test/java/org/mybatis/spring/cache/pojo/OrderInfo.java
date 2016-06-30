package org.mybatis.spring.cache.pojo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

import com.linda.common.mybatis.generator.annotation.Column;
import com.linda.common.mybatis.generator.annotation.Index;
import com.linda.common.mybatis.generator.annotation.PrimaryKey;
import com.linda.common.mybatis.generator.annotation.Table;
import com.linda.common.mybatis.generator.annotation.UniqueKey;

/**
 * order表model
 * @author heyue
 *
 */
@Data
@Table(name="order",autoGeneratePrimaryKey=true)
public class OrderInfo  implements Comparable<OrderInfo>, Serializable{
	
	private static final long serialVersionUID = 697453816035661189L;
	@Index(name="IdAndUserId")
	@PrimaryKey
	private long id;
	@UniqueKey(name="Ordermd5",select=true,update=false,delete=false)
	@Column
	private String order_md5;
	
	/**
	 * 支付方式 wx
	 */
	@Column
	private String service;
	@Index(name="IdAndUserId")
	@Column
	private long user_id;
	@Column
	private long admin_id;
	@Column
	private long team_id;
	@Column
	private String state;
	
	/**
	 * 老的购买数量
	 */
	@Column
	private int quantity;
	@Column
	private double price;
	@Column
	private double partner_price;
	@Column
	private int create_time;
	@Column
	private int cancel_time;
	@Column
	private int pay_time;
	@Column
	private int consume_time;
	@Column
	private int expire_time;
	@Column
	private int clear_time;
	@Index(name="PartnerIdAndCoupon")
	@Column
	private long partner_id;
	@Column
	private double score;
	@Column
	private int scorereturn;
	
	@Index(name="PartnerIdAndCoupon")
	@Column
	private String coupon;
	@Column
	private String tohome;
	@Column
	private String trade_no;
	@Column
	private String buyer_id;
	@Column
	private String buyer_email;
	@Column
	private String openid;
	@Column
	private String wx_transaction_id;
	@Column
	private String wx_prepay_id;
	@Column
	private int fbi;
	@Column
	private long express_id;
	@Column
	private long distribution_id;
	@Column
	private long community_id;
	@Column
	private int delivered_time;
	@Column
	private double delivery_fee;
	@Column
	private int expect_time;
	@Column
	private String udid;
	
	/**
	 * 商户发送时间
	 */
	@Column
	private int delivering_time;
	
	/**
	 * 商户确认送达时间
	 */
	@Column
	private int delivering2_time;
	@Column
	private int refundreq_time;
	@Column
	private int refunddeny_time;
	@Column
	private int refunding_time;
	@Column
	private int refunded_time;
	@Column
	private int arbitrating_time;
	@Column
	private String refund_reason;
	@Column
	private int clear_id;
	
	/**
	 * 客服备注
	 */
	@Column
	private String comment;
	@Column
	private int autodelivering_time;
	@Column
	private int problem;
	@Column
	private int replenish;
	@Column
	private long hongbao_id;
	@Column
	private double hongbao_amount;
	@Column
	private int taskinterval;
	@Column
	private long replenish_order_id;
	@Column
	private double refund_amount;
	@Column
	private String replenish_responser;
	@Column
	private int expect_end_time;
	@Column
	private double total_partner_price;
	
	@Column
	private String order_message;
	
	@Column
	private int user_complain_time;
	
	@Column
	private int is_new_order;

	@Column
	private long groupbuy_id;
	
	@Column
	private long city_id;
	
	@Column
	private long origin_order_id;
	
	//是否是快递(1是,2否)
	@Column
	private int is_express;
	
	//来源订单类型(0订单,1邻里团,2欢乐颂)
	@Column
	private int origin_order_type;

	@Column
	private double partner_hongbao_amount;
	
	//----------------------------------
	
	//商家信息
	private PartnerInfo partner;
	//补货订单对象
	private OrderInfo replenish_order;
	//购物车商品
	private List<OrderdetailInfo> orderdetails;
	//下单返回数据
	private String mch_id;
	
	//order接口，是否可以分享红包 0：不可以  1：可以
	private int shareHongBao;
	
	//下单用户
	private UserInfo user;
	
	//任务是否分配
	private boolean assigned;
	
	private String service_mobile = "4000";
	//团购的团配置Id
	private long setting_id;
	//管家是否已配送 1:已配送
	private int hasdistribution;
	
	@Override
	public int compareTo(OrderInfo o) {
		return (int)(o.getId() - this.getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderInfo other = (OrderInfo) obj;
		if (admin_id != other.admin_id)
			return false;
		if (arbitrating_time != other.arbitrating_time)
			return false;
		if (assigned != other.assigned)
			return false;
		if (autodelivering_time != other.autodelivering_time)
			return false;
		if (buyer_email == null) {
			if (other.buyer_email != null)
				return false;
		} else if (!buyer_email.equals(other.buyer_email))
			return false;
		if (buyer_id == null) {
			if (other.buyer_id != null)
				return false;
		} else if (!buyer_id.equals(other.buyer_id))
			return false;
		if (cancel_time != other.cancel_time)
			return false;
		if (clear_id != other.clear_id)
			return false;
		if (clear_time != other.clear_time)
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (community_id != other.community_id)
			return false;
		if (consume_time != other.consume_time)
			return false;
		if (coupon == null) {
			if (other.coupon != null)
				return false;
		} else if (!coupon.equals(other.coupon))
			return false;
		if (create_time != other.create_time)
			return false;
		if (delivered_time != other.delivered_time)
			return false;
		if (delivering2_time != other.delivering2_time)
			return false;
		if (delivering_time != other.delivering_time)
			return false;
		if (Double.doubleToLongBits(delivery_fee) != Double.doubleToLongBits(other.delivery_fee))
			return false;
		if (distribution_id != other.distribution_id)
			return false;
		if (expect_end_time != other.expect_end_time)
			return false;
		if (expect_time != other.expect_time)
			return false;
		if (expire_time != other.expire_time)
			return false;
		if (express_id != other.express_id)
			return false;
		if (fbi != other.fbi)
			return false;
		if (groupbuy_id != other.groupbuy_id)
			return false;
		if (Double.doubleToLongBits(hongbao_amount) != Double.doubleToLongBits(other.hongbao_amount))
			return false;
		if (hongbao_id != other.hongbao_id)
			return false;
		if (id != other.id)
			return false;
		if (is_new_order != other.is_new_order)
			return false;
		if (mch_id == null) {
			if (other.mch_id != null)
				return false;
		} else if (!mch_id.equals(other.mch_id))
			return false;
		if (openid == null) {
			if (other.openid != null)
				return false;
		} else if (!openid.equals(other.openid))
			return false;
		if (order_md5 == null) {
			if (other.order_md5 != null)
				return false;
		} else if (!order_md5.equals(other.order_md5))
			return false;
		if (order_message == null) {
			if (other.order_message != null)
				return false;
		} else if (!order_message.equals(other.order_message))
			return false;
		if (partner_id != other.partner_id)
			return false;
		if (Double.doubleToLongBits(partner_price) != Double.doubleToLongBits(other.partner_price))
			return false;
		if (pay_time != other.pay_time)
			return false;
		if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
			return false;
		if (problem != other.problem)
			return false;
		if (quantity != other.quantity)
			return false;
		if (Double.doubleToLongBits(refund_amount) != Double.doubleToLongBits(other.refund_amount))
			return false;
		if (refund_reason == null) {
			if (other.refund_reason != null)
				return false;
		} else if (!refund_reason.equals(other.refund_reason))
			return false;
		if (refunddeny_time != other.refunddeny_time)
			return false;
		if (refunded_time != other.refunded_time)
			return false;
		if (refunding_time != other.refunding_time)
			return false;
		if (refundreq_time != other.refundreq_time)
			return false;
		if (replenish != other.replenish)
			return false;
		if (replenish_order_id != other.replenish_order_id)
			return false;
		if (replenish_responser == null) {
			if (other.replenish_responser != null)
				return false;
		} else if (!replenish_responser.equals(other.replenish_responser))
			return false;
		if (Double.doubleToLongBits(score) != Double.doubleToLongBits(other.score))
			return false;
		if (scorereturn != other.scorereturn)
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		if (service_mobile == null) {
			if (other.service_mobile != null)
				return false;
		} else if (!service_mobile.equals(other.service_mobile))
			return false;
		if (setting_id != other.setting_id)
			return false;
		if (shareHongBao != other.shareHongBao)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (taskinterval != other.taskinterval)
			return false;
		if (team_id != other.team_id)
			return false;
		if (tohome == null) {
			if (other.tohome != null)
				return false;
		} else if (!tohome.equals(other.tohome))
			return false;
		if (Double.doubleToLongBits(total_partner_price) != Double.doubleToLongBits(other.total_partner_price))
			return false;
		if (trade_no == null) {
			if (other.trade_no != null)
				return false;
		} else if (!trade_no.equals(other.trade_no))
			return false;
		if (udid == null) {
			if (other.udid != null)
				return false;
		} else if (!udid.equals(other.udid))
			return false;
		if (user_complain_time != other.user_complain_time)
			return false;
		if (user_id != other.user_id)
			return false;
		if (wx_prepay_id == null) {
			if (other.wx_prepay_id != null)
				return false;
		} else if (!wx_prepay_id.equals(other.wx_prepay_id))
			return false;
		if (wx_transaction_id == null) {
			if (other.wx_transaction_id != null)
				return false;
		} else if (!wx_transaction_id.equals(other.wx_transaction_id))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (admin_id ^ (admin_id >>> 32));
		result = prime * result + arbitrating_time;
		result = prime * result + (assigned ? 1231 : 1237);
		result = prime * result + autodelivering_time;
		result = prime * result + ((buyer_email == null) ? 0 : buyer_email.hashCode());
		result = prime * result + ((buyer_id == null) ? 0 : buyer_id.hashCode());
		result = prime * result + cancel_time;
		result = prime * result + clear_id;
		result = prime * result + clear_time;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + (int) (community_id ^ (community_id >>> 32));
		result = prime * result + consume_time;
		result = prime * result + ((coupon == null) ? 0 : coupon.hashCode());
		result = prime * result + create_time;
		result = prime * result + delivered_time;
		result = prime * result + delivering2_time;
		result = prime * result + delivering_time;
		long temp;
		temp = Double.doubleToLongBits(delivery_fee);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (distribution_id ^ (distribution_id >>> 32));
		result = prime * result + expect_end_time;
		result = prime * result + expect_time;
		result = prime * result + expire_time;
		result = prime * result + (int) (express_id ^ (express_id >>> 32));
		result = prime * result + fbi;
		result = prime * result + (int) (groupbuy_id ^ (groupbuy_id >>> 32));
		temp = Double.doubleToLongBits(hongbao_amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (hongbao_id ^ (hongbao_id >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + is_new_order;
		result = prime * result + ((mch_id == null) ? 0 : mch_id.hashCode());
		result = prime * result + ((openid == null) ? 0 : openid.hashCode());
		result = prime * result + ((order_md5 == null) ? 0 : order_md5.hashCode());
		result = prime * result + ((order_message == null) ? 0 : order_message.hashCode());
		result = prime * result + (int) (partner_id ^ (partner_id >>> 32));
		temp = Double.doubleToLongBits(partner_price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + pay_time;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + problem;
		result = prime * result + quantity;
		temp = Double.doubleToLongBits(refund_amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((refund_reason == null) ? 0 : refund_reason.hashCode());
		result = prime * result + refunddeny_time;
		result = prime * result + refunded_time;
		result = prime * result + refunding_time;
		result = prime * result + refundreq_time;
		result = prime * result + replenish;
		result = prime * result + (int) (replenish_order_id ^ (replenish_order_id >>> 32));
		result = prime * result + ((replenish_responser == null) ? 0 : replenish_responser.hashCode());
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + scorereturn;
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		result = prime * result + ((service_mobile == null) ? 0 : service_mobile.hashCode());
		result = prime * result + (int) (setting_id ^ (setting_id >>> 32));
		result = prime * result + shareHongBao;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + taskinterval;
		result = prime * result + (int) (team_id ^ (team_id >>> 32));
		result = prime * result + ((tohome == null) ? 0 : tohome.hashCode());
		temp = Double.doubleToLongBits(total_partner_price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((trade_no == null) ? 0 : trade_no.hashCode());
		result = prime * result + ((udid == null) ? 0 : udid.hashCode());
		result = prime * result + user_complain_time;
		result = prime * result + (int) (user_id ^ (user_id >>> 32));
		result = prime * result + ((wx_prepay_id == null) ? 0 : wx_prepay_id.hashCode());
		result = prime * result + ((wx_transaction_id == null) ? 0 : wx_transaction_id.hashCode());
		return result;
	}
	
}
