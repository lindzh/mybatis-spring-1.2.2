package org.mybatis.spring.cache.pojo;


import java.io.Serializable;

import lombok.Data;

import com.linda.common.mybatis.generator.annotation.Column;
import com.linda.common.mybatis.generator.annotation.Index;
import com.linda.common.mybatis.generator.annotation.PrimaryKey;
import com.linda.common.mybatis.generator.annotation.Table;
import com.linda.common.mybatis.generator.annotation.UniqueKey;

/**
 * user表model
 * @author heyue
 *
 */
@Data
@Table(name="user",autoGeneratePrimaryKey=true)
public class UserInfo  implements Serializable{
	
	private static final long serialVersionUID = 4177307756610524775L;

	@PrimaryKey
	private long id;
	
	@Column
	private String email;
	
	@Column
	private String username;
	
	/**
	 * 爱到家名字
	 */
	@Column
	private String realname;
	
	/**
	 * 微信名字
	 */
	@Column
	private String nickname;
	
	/**
	 * 微信头像
	 */
	@Column
	private String headimgurl;
	
	@Column
	private String alipay_id;
	
	@Column
	private String password;
	/**
	 * 爱到家头像
	 */
	@Column
	private String avatar;
	
	//取值 :'M'、'F'
	@Column
	private String gender;
	
	@UniqueKey(name="Mobile",select=true,update=false,delete=false)
	@Column
	private String mobile;
	
	@Column
	private double score;
	
	@Column
	private String manager;
	
	@Column
	private int login_time;
	
	@Column
	private int create_time;
	
	@Column
	private String openid;
	
	@Column
	private int jihuo_time;
	
	@Column
	private String type;
	
	@Column
	private String huhao;
	
	@Index(name="CommunityAndPartAndBuildingAndUnit")
	@Column
	private long community_id;
	
	@UniqueKey(name="Token",select=true,update=false,delete=false)
	@Column
	private String token;
	
	@Column
	private int expire_time;
	
	@Index(name="CommunityAndPartAndBuildingAndUnit")
	@Column
	private String part;
	
	@Index(name="CommunityAndPartAndBuildingAndUnit")
	@Column
	private int building;
	
	@Index(name="CommunityAndPartAndBuildingAndUnit")
	@Column
	private int unit;
	
	@Index(name="CommunityAndPartAndBuildingAndUnit")
	@Column
	private int room;
	
	@Column
	private String state;
	
	@Column
	private int maxreadmessageid;
	
	//通过auth_user查询
//    on user.community_id = authuser.community_id 
//    and user.part = authuser.part and user.building = authuser.building 
//    and user.unit = authuser.unit and user.room = authuser.room
	@Column
	private String propertyfee;
	
	@Column
	private String model;
	
	@Column
	private String ip;
	
	@Column
	private int has_pay;
	
	@Column
	private String openid_app = "";
	
	@Column
	private String unionid = "";
	
	@Column
	private long province_id;
	
	@Column
	private long city_id;
	
	@Column
	private String poi="";
	
	@Column
	private int risk_rank;
	
	//1代表验证码登录注册
	private int verifyCodeLogin;
	//是否有密码，1是无密码
	private int noPassword;
	//是否为该邻里团的组织人
	private int is_organizer;
	
	private int loginType;

	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}
