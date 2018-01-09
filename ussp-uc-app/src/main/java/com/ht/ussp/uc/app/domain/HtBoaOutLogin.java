package com.ht.ussp.uc.app.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * 
* @ClassName: HtBoaOutLogin
* @Description: 登录信息表
* @author wim qiuwenwu@hongte.info
* @date 2018年1月5日 下午2:59:24
 */
@Entity
@Table(name="HT_BOA_OUT_LOGIN")
@NamedQuery(name="HtBoaOutLogin.findAll", query="SELECT h FROM HtBoaOutLogin h")
public class HtBoaOutLogin implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	private Long id;

	@Column(name="CREATE_OPERATOR")
	private String createOperator;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATETIME")
	private Date createdDatetime;

	@Column(name="DEL_FLAG")
	private int delFlag;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE")
	private Date effectiveDate;

	@Column(name="FAILED_COUNT")
	private int failedCount;

	@Column(name="JPA_VERSION")
	private int jpaVersion;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_MODIFIED_DATETIME")
	private Date lastModifiedDatetime;

	@Column(name="LOGIN_ID")
	private String loginId;

	@Column(name="PASSWORD")
	private String password;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="PWD_EXP_DATE")
	private Date pwdExpDate;
	
	@Column(name="STATUS")
	private String status;

	@Column(name="UPDATE_OPERATOR")
	private String updateOperator;

	@Column(name="USER_ID")
	private String userId;

	public HtBoaOutLogin() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCreateOperator() {
		return this.createOperator;
	}

	public void setCreateOperator(String createOperator) {
		this.createOperator = createOperator;
	}

	public Date getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public int getDelFlag() {
		return this.delFlag;
	}

	public void setDelFlag(int delFlag) {
		this.delFlag = delFlag;
	}

	public Date getEffectiveDate() {
		return this.effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public int getFailedCount() {
		return this.failedCount;
	}

	public void setFailedCount(int failedCount) {
		this.failedCount = failedCount;
	}

	public int getJpaVersion() {
		return this.jpaVersion;
	}

	public void setJpaVersion(int jpaVersion) {
		this.jpaVersion = jpaVersion;
	}

	public Date getLastModifiedDatetime() {
		return this.lastModifiedDatetime;
	}

	public void setLastModifiedDatetime(Date lastModifiedDatetime) {
		this.lastModifiedDatetime = lastModifiedDatetime;
	}

	public String getLoginId() {
		return this.loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getPwdExpDate() {
		return this.pwdExpDate;
	}

	public void setPwdExpDate(Date pwdExpDate) {
		this.pwdExpDate = pwdExpDate;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUpdateOperator() {
		return this.updateOperator;
	}

	public void setUpdateOperator(String updateOperator) {
		this.updateOperator = updateOperator;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}