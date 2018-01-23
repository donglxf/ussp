package com.ht.ussp.uc.app.vo;

import java.util.Date;

/**
 * 
 * @ClassName: UserInfoVo
 * @Description: 供各客户端查询用户信息用
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月22日 下午10:04:17
 */
public class LoginInfoVo {


	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	// 用户ID
	private String userId;
	// 工号
	private String jobNumber;
	// 用户名
	private String userName;
	// 机构编码
	private String orgCode;
	// 邮箱
	private String email;
	// 身份证号
	private String idNo;
	// 手机号
	private String mobile;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getJobNumber() {
		return jobNumber;
	}
	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}



}
