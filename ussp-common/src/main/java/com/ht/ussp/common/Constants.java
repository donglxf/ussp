package com.ht.ussp.common;


/**
 * 
 * @ClassName: Constants
 * @Description: 常量类
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月9日 下午6:00:55
 */
public class Constants {

	public static final int ZERO=0;
	public static final String EMPTY_STRING="";
	
	/**
	 * 渠道编码
	 */
	public static final String CHANNEL_UC = "uc";  		//用户中心
	public static final String CHANNEL_RISK = "risk";	//风控系统
	public static final String CHANNEL_CISP = "cisp";   //车全系统

	public static final String RES_TYPE_MODULE="module";//模块
	public static final String RES_TYPE_GROUP="group";//分组
	public static final String RES_TYPE_VIEW="view";//页面
	public static final String RES_TYPE_BUTTON="btn";//按钮
	public static final String RES_TYPE_TAB="tab";//标签页
	public static final String RES_TYPE_API="api";// API
	
	
	public static final String STATUS_0="0";// 系统 用户 角色 机构 禁用标记： 0：正常  1禁用
	public static final String STATUS_1="1";// 系统 用户 角色 机构 禁用标记： 0：正常  1禁用
	
	public static final int DEL_0=0;// 系统 用户 角色 机构 删除标记： 0：正常  1:已删除
	public static final int DEL_1=1;// 系统 用户 角色 机构 删除标记： 0：正常  1:已删除
	
	
	public static final String USER_STATUS_0 ="0";// 系统 用户 0 正常  1禁用 2离职  4冻结 5锁定
	public static final String USER_STATUS_1 ="1";// 系统 用户 0 正常  1禁用 2离职  4冻结 5锁定
	public static final String USER_STATUS_2 ="2";// 系统 用户 0 正常  1禁用 2离职  4冻结 5锁定
	public static final String USER_STATUS_4 ="4";// 系统 用户 0 正常  1禁用 2离职  4冻结 5锁定
	public static final String USER_STATUS_5 ="5";// 系统 用户 0 正常  1禁用 2离职  4冻结 5锁定
	
	// 数据来源(1：用户权限系统；2：钉钉同步;3:信贷系统)
	public static final int USER_DATASOURCE_1 =1;// 1：用户权限系统 
	public static final int USER_DATASOURCE_2 =2;// 2：钉钉同步 
	public static final int USER_DATASOURCE_3 =3;// 3:信贷系统
	
}
