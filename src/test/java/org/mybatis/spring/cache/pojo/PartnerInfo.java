package org.mybatis.spring.cache.pojo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

import com.linda.common.mybatis.generator.annotation.Column;
import com.linda.common.mybatis.generator.annotation.PrimaryKey;
import com.linda.common.mybatis.generator.annotation.Table;

/**
 * 商户店铺
 * @author lindezhi
 * 2015年12月18日 上午10:45:27
 */
@Data
@Table(name="partner",autoGeneratePrimaryKey=true)
public class PartnerInfo implements Comparable<PartnerInfo> , Serializable{
	
	private static final long serialVersionUID = 2920864779744340478L;

	@PrimaryKey
	private long id;
	
	/**
	 * 店铺名称
	 */
	@Column
	private String title;
	@Column
	private String jieshao;
	@Column
	private String shijian;
	@Column
	private String shijian_begin;
	@Column
	private String shijian_end;
	
	/**
	 * 门店地址
	 */
	@Column
	private String address;
	@Column
	private String mobile;
	@Column
	private String username;
	@Column
	private String password;
	
	/**
	 * 店铺背景
	 */
	@Column
	private String image;
	@Column
	private int distance;
	@Column
	private int city_id;
	@Column
	private long group_id;
	
	/**
	 * 银行
	 */
	@Column
	private String bank_name;
	/**
	 * 支行
	 */
	@Column
	private String sub_bank_name;
	/**
	 * 账号
	 */
	@Column
	private String bank_no;
	
	/**
	 * 银行卡开户省份
	 */
	@Column
	private String bank_province;
	
	/**
	 * 银行卡开户地市
	 */
	@Column
	private String bank_city;
	/**
	 * 姓名
	 */
	@Column
	private String bank_user;
	/**
	 * 银行头像
	 */
	@Column
	private String bank_img;
	
	@Column
	private String enable;
	@Column
	private long user_id;
	@Column
	private int create_time;
	@Column
	private String longlat;
	@Column
	private double longi;
	@Column
	private double lati;
	@Column
	private int sort_order;
	
	/**
	 * 标记是否是fbi
	 */
	@Column
	private int fbi;
	@Column
	private int qc_id;
	
	/**
	 * 头像
	 */
	@Column
	private String avatar;
	
	/**
	 * 配送费
	 */
	@Column
	private double delivery_fee;
	
	/**
	 * 起送费
	 */
	@Column
	private double delivery_begin_money;
	
	/**
	 * 满多少免配送费
	 */
	@Column
	private double delivery_end_money;
	@Column
	private int delivery_days;
	@Column
	private int delivery_hours;
	
	/**
	 * 营业时间
	 */
	@Column
	private int sale_time_begin;
	
	@Column
	private int sale_time_end;
	
	/**
	 * 服务类型，到家还是到店
	 */
	@Column
	private String tohome;
	@Column
	private int clear_time;
	@Column
	private int sale;
	@Column
	private int pv;
	@Column
	private int uv;
	/**
	 * 所属商圈
	 */
	@Column
	private int circle_id;
	@Column
	private int area_id;
	@Column
	private int service_distance;
	@Column
	private int cartable;
	
	
	/**
	 * 保证金金额
	 */
	@Column
	private double guarantee_fee;
	
	/**
	 * 保证金是否已交
	 */
	@Column
	private int guarantee_flag;
	/**
	 * 商户余额
	 */
	@Column
	private double balance;
	
	/**
	 * 商户状态（0表示营业,1表示打烊）
	 */
	@Column
	private int partner_status;
	/**
	 * 店铺打烊总时长,单位秒
	 */
	@Column
	private int close_total_time;
	/**
	 * 店铺二维码URL
	 */
	@Column
	private String qr_code_url;
	/**
	 * 店铺服务电话
	 */
	@Column
	private String service_mobile;
	
	/**
	 * 提现限制金额
	 */
	@Column
	private Double withdraw_limit_fee;
	
	/**
	 * 是否支持货架，1：支持
	 */
	@Column
	private int support_shelf;
	
	/**
	 * 商家服务间隔时间
	 */
	@Column
	private int interval_time;
	
	@Column
	private int partner_type;
	
	/**
	 * 是否置顶
	 */
	@Column
	private int is_top;
	
	/**
	 * 商家商品
	 */
	private List<TeamInfo> teams;
	
	//当日销量
	private int saleCount;
	@Override
	public int compareTo(PartnerInfo o) {
		return o.getPv() - this.pv;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PartnerInfo other = (PartnerInfo) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (area_id != other.area_id)
			return false;
		if (avatar == null) {
			if (other.avatar != null)
				return false;
		} else if (!avatar.equals(other.avatar))
			return false;
		if (Double.doubleToLongBits(balance) != Double.doubleToLongBits(other.balance))
			return false;
		if (bank_city == null) {
			if (other.bank_city != null)
				return false;
		} else if (!bank_city.equals(other.bank_city))
			return false;
		if (bank_img == null) {
			if (other.bank_img != null)
				return false;
		} else if (!bank_img.equals(other.bank_img))
			return false;
		if (bank_name == null) {
			if (other.bank_name != null)
				return false;
		} else if (!bank_name.equals(other.bank_name))
			return false;
		if (bank_no == null) {
			if (other.bank_no != null)
				return false;
		} else if (!bank_no.equals(other.bank_no))
			return false;
		if (bank_province == null) {
			if (other.bank_province != null)
				return false;
		} else if (!bank_province.equals(other.bank_province))
			return false;
		if (bank_user == null) {
			if (other.bank_user != null)
				return false;
		} else if (!bank_user.equals(other.bank_user))
			return false;
		if (cartable != other.cartable)
			return false;
		if (circle_id != other.circle_id)
			return false;
		if (city_id != other.city_id)
			return false;
		if (clear_time != other.clear_time)
			return false;
		if (close_total_time != other.close_total_time)
			return false;
		if (create_time != other.create_time)
			return false;
		if (Double.doubleToLongBits(delivery_begin_money) != Double.doubleToLongBits(other.delivery_begin_money))
			return false;
		if (delivery_days != other.delivery_days)
			return false;
		if (Double.doubleToLongBits(delivery_end_money) != Double.doubleToLongBits(other.delivery_end_money))
			return false;
		if (Double.doubleToLongBits(delivery_fee) != Double.doubleToLongBits(other.delivery_fee))
			return false;
		if (delivery_hours != other.delivery_hours)
			return false;
		if (distance != other.distance)
			return false;
		if (enable == null) {
			if (other.enable != null)
				return false;
		} else if (!enable.equals(other.enable))
			return false;
		if (fbi != other.fbi)
			return false;
		if (group_id != other.group_id)
			return false;
		if (Double.doubleToLongBits(guarantee_fee) != Double.doubleToLongBits(other.guarantee_fee))
			return false;
		if (guarantee_flag != other.guarantee_flag)
			return false;
		if (id != other.id)
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (jieshao == null) {
			if (other.jieshao != null)
				return false;
		} else if (!jieshao.equals(other.jieshao))
			return false;
		if (Double.doubleToLongBits(lati) != Double.doubleToLongBits(other.lati))
			return false;
		if (Double.doubleToLongBits(longi) != Double.doubleToLongBits(other.longi))
			return false;
		if (longlat == null) {
			if (other.longlat != null)
				return false;
		} else if (!longlat.equals(other.longlat))
			return false;
		if (mobile == null) {
			if (other.mobile != null)
				return false;
		} else if (!mobile.equals(other.mobile))
			return false;
		if (partner_status != other.partner_status)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (pv != other.pv)
			return false;
		if (qc_id != other.qc_id)
			return false;
		if (qr_code_url == null) {
			if (other.qr_code_url != null)
				return false;
		} else if (!qr_code_url.equals(other.qr_code_url))
			return false;
		if (sale != other.sale)
			return false;
		if (sale_time_begin != other.sale_time_begin)
			return false;
		if (sale_time_end != other.sale_time_end)
			return false;
		if (service_distance != other.service_distance)
			return false;
		if (service_mobile == null) {
			if (other.service_mobile != null)
				return false;
		} else if (!service_mobile.equals(other.service_mobile))
			return false;
		if (shijian == null) {
			if (other.shijian != null)
				return false;
		} else if (!shijian.equals(other.shijian))
			return false;
		if (shijian_begin == null) {
			if (other.shijian_begin != null)
				return false;
		} else if (!shijian_begin.equals(other.shijian_begin))
			return false;
		if (shijian_end == null) {
			if (other.shijian_end != null)
				return false;
		} else if (!shijian_end.equals(other.shijian_end))
			return false;
		if (sort_order != other.sort_order)
			return false;
		if (sub_bank_name == null) {
			if (other.sub_bank_name != null)
				return false;
		} else if (!sub_bank_name.equals(other.sub_bank_name))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (tohome == null) {
			if (other.tohome != null)
				return false;
		} else if (!tohome.equals(other.tohome))
			return false;
		if (user_id != other.user_id)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (uv != other.uv)
			return false;
		if (withdraw_limit_fee == null) {
			if (other.withdraw_limit_fee != null)
				return false;
		} else if (!withdraw_limit_fee.equals(other.withdraw_limit_fee))
			return false;
		return true;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + area_id;
		result = prime * result + ((avatar == null) ? 0 : avatar.hashCode());
		long temp;
		temp = Double.doubleToLongBits(balance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((bank_city == null) ? 0 : bank_city.hashCode());
		result = prime * result + ((bank_img == null) ? 0 : bank_img.hashCode());
		result = prime * result + ((bank_name == null) ? 0 : bank_name.hashCode());
		result = prime * result + ((bank_no == null) ? 0 : bank_no.hashCode());
		result = prime * result + ((bank_province == null) ? 0 : bank_province.hashCode());
		result = prime * result + ((bank_user == null) ? 0 : bank_user.hashCode());
		result = prime * result + cartable;
		result = prime * result + circle_id;
		result = prime * result + city_id;
		result = prime * result + clear_time;
		result = prime * result + close_total_time;
		result = prime * result + create_time;
		temp = Double.doubleToLongBits(delivery_begin_money);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + delivery_days;
		temp = Double.doubleToLongBits(delivery_end_money);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(delivery_fee);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + delivery_hours;
		result = prime * result + distance;
		result = prime * result + ((enable == null) ? 0 : enable.hashCode());
		result = prime * result + fbi;
		result = prime * result + (int) (group_id ^ (group_id >>> 32));
		temp = Double.doubleToLongBits(guarantee_fee);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + guarantee_flag;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((jieshao == null) ? 0 : jieshao.hashCode());
		temp = Double.doubleToLongBits(lati);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longi);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((longlat == null) ? 0 : longlat.hashCode());
		result = prime * result + ((mobile == null) ? 0 : mobile.hashCode());
		result = prime * result + partner_status;
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + pv;
		result = prime * result + qc_id;
		result = prime * result + ((qr_code_url == null) ? 0 : qr_code_url.hashCode());
		result = prime * result + sale;
		result = prime * result + sale_time_begin;
		result = prime * result + sale_time_end;
		result = prime * result + service_distance;
		result = prime * result + ((service_mobile == null) ? 0 : service_mobile.hashCode());
		result = prime * result + ((shijian == null) ? 0 : shijian.hashCode());
		result = prime * result + ((shijian_begin == null) ? 0 : shijian_begin.hashCode());
		result = prime * result + ((shijian_end == null) ? 0 : shijian_end.hashCode());
		result = prime * result + sort_order;
		result = prime * result + ((sub_bank_name == null) ? 0 : sub_bank_name.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((tohome == null) ? 0 : tohome.hashCode());
		result = prime * result + (int) (user_id ^ (user_id >>> 32));
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		result = prime * result + uv;
		result = prime * result + ((withdraw_limit_fee == null) ? 0 : withdraw_limit_fee.hashCode());
		return result;
	}
	
}
