package com.ht.ussp.ouc.app.vo;
import java.util.Date;

/**
 * 
 * @ClassName: UserVo
 * @Description: 系统内部使用VO
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月22日 下午10:04:51
 */
public class UserVo {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	// 用户ID
	private String userId;
	// 工号
	private String jobNumber;
	// 用户名
	private String userName;
	//密码
	private String password;
	//密码错误次数
	private int failedCount;
	// 机构编码
	private String orgCode;
	// 使用状态  0正常  1删除
	private int delFlag;
	// 邮箱
	private String email;
	// 身份证号
	private String idNo;
	// 最后修改时间
	private Date lastModifiedDatetime;
	// 手机号
	private String mobile;
	//系统编码
	private String app;
	//是否管理员  Y是  N不是
	private String controller;

	public int getDelFlag() {
		return this.delFlag;
	}

	public void setDelFlag(int delFlag) {
		this.delFlag = delFlag;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIdNo() {
		return this.idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public Date getLastModifiedDatetime() {
		return this.lastModifiedDatetime;
	}

	public void setLastModifiedDatetime(Date lastModifiedDatetime) {
		this.lastModifiedDatetime = lastModifiedDatetime;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getOrgCode() {
		return this.orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getJobNumber() {
		return jobNumber;
	}

	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getController() {
		return controller;
	}

	public void setController(String controller) {
		this.controller = controller;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getFailedCount() {
		return failedCount;
	}

	public void setFailedCount(int failedCount) {
		this.failedCount = failedCount;
	}


}
