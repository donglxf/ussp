package com.ht.ussp.bean;

import lombok.Data;

@Data
public class EloginParam {
	
	/**
	 * 系统编码
	 */
	private String app;
	
	/**
	 * 可以是手机号，邮箱，普通用户名
	 */
	private String userName;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 手机验证码或图形验证码等
	 */
	private String validateCode;
	
	/**
	 * 类型：normal,sms,email
	 */
	private String type;
	
	/**
	 * 手机序列号，PC端传MAC地址
	 */
	private String ieme;
	

}
