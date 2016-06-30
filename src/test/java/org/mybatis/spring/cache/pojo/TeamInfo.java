package org.mybatis.spring.cache.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import lombok.Data;

import org.mybatis.spring.cache.StringUtils;

import com.linda.common.mybatis.generator.annotation.Column;
import com.linda.common.mybatis.generator.annotation.Index;
import com.linda.common.mybatis.generator.annotation.PrimaryKey;
import com.linda.common.mybatis.generator.annotation.Table;
/**
 * 
 * 商品
 * 2015年11月16日 下午11:08:07
 */
@Data
@Table(name="team",autoGeneratePrimaryKey=true)
public class TeamInfo implements Comparable<TeamInfo>, Serializable{

	private static final long serialVersionUID = -2367842916896358216L;

	@PrimaryKey
	private long id;
	
	@Column
	private long user_id;
	
	@Column
	private String title;
	
	@Column
	private String need_book;
		
	/**
	 * 商品简介
	 */
	@Column
	private String summary;
	
	@Column
	private int city_id;
	
	@Column
	@Index(name="FbiAndTypeAndGrpupAndBeginExpire",count=true,limitOffset=true)
	private long group_id;
	
	/**
	 * type>0的都是活动商品
	 */
	@Column
	@Index(name="PartnerIdAndType",count=true,limitOffset=true,selectOne=true)
	private int type;
	
	@Column
	@Index(name="PartnerIdAndType",count=true,limitOffset=true,selectOne=true)
	private long partner_id;
	
	/**
	 * 销售价
	 */
	@Column
	private double team_price;
	
	/**
	 * 市场价，原价
	 */
	@Column
	private double market_price;
	
	/**
	 * 商户结算价格
	 */
	@Column
	private double partner_price;
	
	@Deprecated
	@Column
	private int per_number;
	
	/**
	 * now_number+库存数量=max_number
	 */
	@Column
	private int max_number;
	
	/**
	 * 当前销售数量
	 */
	@Column
	private int now_number;
	
	/**
	 * 用户使用，已售数量，已废弃
	 */
	@Deprecated
	@Column
	private int pre_number;
	
	@Deprecated
	@Column
	private String allowrefund;	
	
	@Deprecated
	@Column
	private String image;
	
	/**
	 * 商品小图，封面
	 */
	@Column
	private String image0;
	
	//image 1-5废弃   7-10废弃
	@Column
	private String image1;
	
	@Column
	private String image2;
	
	@Column
	private String image3;
	
	@Column
	private String image4;
	
	@Column
	private String image5;
	
	/**
	 * fbi商品列表商品使用图片
	 */
	@Column
	private String image6;
	
	@Column
	private int credit;
	
	/**
	 * 是否是限购商品
	 */
	@Column
	private int tag;
	
	/**
	 * 使用规则
	 */
	@Column
	private String use_rule;
	
	@Column
	private String buyonce;
	
	@Column
	private int sort_order;
	
	/**
	 * 结束售卖时间
	 */
	@Column
	@Index(name="PartnerIdAndExpire",count=true,limitOffset=true)
	private int expire_time;
	
	/**
	 * 开始售卖时间
	 */
	@Column
	@Index(name="FbiAndTypeAndGrpupAndBeginExpire",count=true,limitOffset=true)
	private int begin_time;
	
	/**
	 * 核销码失效时间,fbi表示配送时间
	 */
	@Column
	private int end_time;
	
	@Column
	private int close_time;
	
	@Column
	private String tohome;
	
	/**
	 * 是否是特惠到家商品，到家的需要送货上门
	 */
	@Column
	@Index(name="FbiAndTypeAndGrpupAndBeginExpire",count=true,limitOffset=true)
	private int fbi;
	
	@Column
	private String perunit;
	
	/**
	 * 商品码或者条形码
	 */
	@Column
	private String code;
	
	@Column
	private int onsale;
	
	@Column
	private String url;
	
	@Column
	private String title2;
	
	@Column
	private int new_user_only;
	
	@Column
	private int create_time;
	
	@Column
	private int gift;
	
	/**
	 * 限购组
	 */
	@Index(name="TagGroup",count=true,limitOffset=true)
	@Column
	private int taggroup;
	
	@Column
	private String expired;
	
	@Column
	private int baokuan;
	
	@Column
	private String image7;
	
	@Column
	private String image8;
	
	@Column
	private String image9;
	
	@Column
	private String image10;
	
	@Column
	private int display_number;
	
	@Column
	private String peisong_shijian;
	
	@Column
	private int presale;
	
	/**
	 * 除了商品小图	其他的都放在里面
	 * 目前逗号分割图片数组
	 */
	@Column
	private String images;
	
	@Column
	private String teamname;
	
	@Column
	private int day_begin_time;
	
	@Column
	private int day_end_time;
	
	@Column
	private int expire_days;
	
	@Column
	private int weight;
	
	@Column
	private int taguser;
	
	@Column
	private String images_top;
	
	@Column
	private int auto_unshelve;
	
	@Column
	private int delete_time;
	
	@Column
	private int update_time;
	
	@Column
	private long activity_id;
	
	@Column
	private long third_team_id;
	
	//今日已售
	@Column
	private int today_sale;
	
	@Column
	private int fixed_amount;
	
	@Column
	private int increase_inventory;
	
	//商品标签
	@Column
	private int team_tag;
	
	//商品标签内容
	@Column
	private String team_tag_value;
	
	//顶级类目ID
	@Column
	private long top_group_id;
	
	//是否是快递(1是,0否)
	@Column
	private int support_express;
	
	//----------------以下为非数据库字段------------------
	/**
	 * 商家
	 */
	private PartnerInfo partner;
	
	/**
	 * 访问统计量
	 */
	private int pv;
	
	/**
	 * 商品所在商家的商品数
	 */
	private int count;
	//images转化的数组
	private String[] imagesArr;
	
	private String[] topImagesArr;
	
	private long column_id;
	
	private String column_title;
	
	//下单购买商品数量
	private int quantity;
	
	//可能喜欢的商品
	private List<TeamInfo> favTeams;

	@Override
	public int compareTo(TeamInfo o) {
		return o.getSort_order() - this.sort_order;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TeamInfo other = (TeamInfo) obj;
		if (allowrefund == null) {
			if (other.allowrefund != null)
				return false;
		} else if (!allowrefund.equals(other.allowrefund))
			return false;
		if (baokuan != other.baokuan)
			return false;
		if (begin_time != other.begin_time)
			return false;
		if (buyonce == null) {
			if (other.buyonce != null)
				return false;
		} else if (!buyonce.equals(other.buyonce))
			return false;
		if (city_id != other.city_id)
			return false;
		if (close_time != other.close_time)
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (column_id != other.column_id)
			return false;
		if (column_title == null) {
			if (other.column_title != null)
				return false;
		} else if (!column_title.equals(other.column_title))
			return false;
		if (count != other.count)
			return false;
		if (create_time != other.create_time)
			return false;
		if (credit != other.credit)
			return false;
		if (day_begin_time != other.day_begin_time)
			return false;
		if (day_end_time != other.day_end_time)
			return false;
		if (display_number != other.display_number)
			return false;
		if (end_time != other.end_time)
			return false;
		if (expire_days != other.expire_days)
			return false;
		if (expire_time != other.expire_time)
			return false;
		if (expired == null) {
			if (other.expired != null)
				return false;
		} else if (!expired.equals(other.expired))
			return false;
		if (fbi != other.fbi)
			return false;
		if (gift != other.gift)
			return false;
		if (group_id != other.group_id)
			return false;
		if (id != other.id)
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (image0 == null) {
			if (other.image0 != null)
				return false;
		} else if (!image0.equals(other.image0))
			return false;
		if (image1 == null) {
			if (other.image1 != null)
				return false;
		} else if (!image1.equals(other.image1))
			return false;
		if (image10 == null) {
			if (other.image10 != null)
				return false;
		} else if (!image10.equals(other.image10))
			return false;
		if (image2 == null) {
			if (other.image2 != null)
				return false;
		} else if (!image2.equals(other.image2))
			return false;
		if (image3 == null) {
			if (other.image3 != null)
				return false;
		} else if (!image3.equals(other.image3))
			return false;
		if (image4 == null) {
			if (other.image4 != null)
				return false;
		} else if (!image4.equals(other.image4))
			return false;
		if (image5 == null) {
			if (other.image5 != null)
				return false;
		} else if (!image5.equals(other.image5))
			return false;
		if (image6 == null) {
			if (other.image6 != null)
				return false;
		} else if (!image6.equals(other.image6))
			return false;
		if (image7 == null) {
			if (other.image7 != null)
				return false;
		} else if (!image7.equals(other.image7))
			return false;
		if (image8 == null) {
			if (other.image8 != null)
				return false;
		} else if (!image8.equals(other.image8))
			return false;
		if (image9 == null) {
			if (other.image9 != null)
				return false;
		} else if (!image9.equals(other.image9))
			return false;
		if (images == null) {
			if (other.images != null)
				return false;
		} else if (!images.equals(other.images))
			return false;
		if (!Arrays.equals(imagesArr, other.imagesArr))
			return false;
		if (images_top == null) {
			if (other.images_top != null)
				return false;
		} else if (!images_top.equals(other.images_top))
			return false;
		if (Double.doubleToLongBits(market_price) != Double.doubleToLongBits(other.market_price))
			return false;
		if (max_number != other.max_number)
			return false;
		if (need_book == null) {
			if (other.need_book != null)
				return false;
		} else if (!need_book.equals(other.need_book))
			return false;
		if (new_user_only != other.new_user_only)
			return false;
		if (now_number != other.now_number)
			return false;
		if (onsale != other.onsale)
			return false;
		if (partner_id != other.partner_id)
			return false;
		if (Double.doubleToLongBits(partner_price) != Double.doubleToLongBits(other.partner_price))
			return false;
		if (peisong_shijian == null) {
			if (other.peisong_shijian != null)
				return false;
		} else if (!peisong_shijian.equals(other.peisong_shijian))
			return false;
		if (per_number != other.per_number)
			return false;
		if (perunit == null) {
			if (other.perunit != null)
				return false;
		} else if (!perunit.equals(other.perunit))
			return false;
		if (pre_number != other.pre_number)
			return false;
		if (presale != other.presale)
			return false;
		if (pv != other.pv)
			return false;
		if (quantity != other.quantity)
			return false;
		if (sort_order != other.sort_order)
			return false;
		if (summary == null) {
			if (other.summary != null)
				return false;
		} else if (!summary.equals(other.summary))
			return false;
		if (tag != other.tag)
			return false;
		if (taggroup != other.taggroup)
			return false;
		if (taguser != other.taguser)
			return false;
		if (Double.doubleToLongBits(team_price) != Double.doubleToLongBits(other.team_price))
			return false;
		if (teamname == null) {
			if (other.teamname != null)
				return false;
		} else if (!teamname.equals(other.teamname))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (title2 == null) {
			if (other.title2 != null)
				return false;
		} else if (!title2.equals(other.title2))
			return false;
		if (tohome == null) {
			if (other.tohome != null)
				return false;
		} else if (!tohome.equals(other.tohome))
			return false;
		if (type != other.type)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (use_rule == null) {
			if (other.use_rule != null)
				return false;
		} else if (!use_rule.equals(other.use_rule))
			return false;
		if (user_id != other.user_id)
			return false;
		if (weight != other.weight)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allowrefund == null) ? 0 : allowrefund.hashCode());
		result = prime * result + baokuan;
		result = prime * result + begin_time;
		result = prime * result + ((buyonce == null) ? 0 : buyonce.hashCode());
		result = prime * result + city_id;
		result = prime * result + close_time;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + (int) (column_id ^ (column_id >>> 32));
		result = prime * result + ((column_title == null) ? 0 : column_title.hashCode());
		result = prime * result + count;
		result = prime * result + create_time;
		result = prime * result + credit;
		result = prime * result + day_begin_time;
		result = prime * result + day_end_time;
		result = prime * result + display_number;
		result = prime * result + end_time;
		result = prime * result + expire_days;
		result = prime * result + expire_time;
		result = prime * result + ((expired == null) ? 0 : expired.hashCode());
		result = prime * result + fbi;
		result = prime * result + gift;
		result = prime * result + (int) (group_id ^ (group_id >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((image0 == null) ? 0 : image0.hashCode());
		result = prime * result + ((image1 == null) ? 0 : image1.hashCode());
		result = prime * result + ((image10 == null) ? 0 : image10.hashCode());
		result = prime * result + ((image2 == null) ? 0 : image2.hashCode());
		result = prime * result + ((image3 == null) ? 0 : image3.hashCode());
		result = prime * result + ((image4 == null) ? 0 : image4.hashCode());
		result = prime * result + ((image5 == null) ? 0 : image5.hashCode());
		result = prime * result + ((image6 == null) ? 0 : image6.hashCode());
		result = prime * result + ((image7 == null) ? 0 : image7.hashCode());
		result = prime * result + ((image8 == null) ? 0 : image8.hashCode());
		result = prime * result + ((image9 == null) ? 0 : image9.hashCode());
		result = prime * result + ((images == null) ? 0 : images.hashCode());
		result = prime * result + Arrays.hashCode(imagesArr);
		result = prime * result + ((images_top == null) ? 0 : images_top.hashCode());
		long temp;
		temp = Double.doubleToLongBits(market_price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + max_number;
		result = prime * result + ((need_book == null) ? 0 : need_book.hashCode());
		result = prime * result + new_user_only;
		result = prime * result + now_number;
		result = prime * result + onsale;
		result = prime * result + (int) (partner_id ^ (partner_id >>> 32));
		temp = Double.doubleToLongBits(partner_price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((peisong_shijian == null) ? 0 : peisong_shijian.hashCode());
		result = prime * result + per_number;
		result = prime * result + ((perunit == null) ? 0 : perunit.hashCode());
		result = prime * result + pre_number;
		result = prime * result + presale;
		result = prime * result + pv;
		result = prime * result + quantity;
		result = prime * result + sort_order;
		result = prime * result + ((summary == null) ? 0 : summary.hashCode());
		result = prime * result + tag;
		result = prime * result + taggroup;
		result = prime * result + taguser;
		temp = Double.doubleToLongBits(team_price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((teamname == null) ? 0 : teamname.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((title2 == null) ? 0 : title2.hashCode());
		result = prime * result + ((tohome == null) ? 0 : tohome.hashCode());
		result = prime * result + type;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((use_rule == null) ? 0 : use_rule.hashCode());
		result = prime * result + (int) (user_id ^ (user_id >>> 32));
		result = prime * result + weight;
		return result;
	}

}
