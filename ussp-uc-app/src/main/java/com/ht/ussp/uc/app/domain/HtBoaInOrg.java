package com.ht.ussp.uc.app.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * 
* @ClassName: HtBoaInOrg
* @Description: 组织机构表
* @author wim qiuwenwu@hongte.info
* @date 2018年1月5日 下午2:49:49
 */
@Entity
@Table(name="HT_BOA_IN_ORG")
@NamedQuery(name="HtBoaInOrg.findAll", query="SELECT h FROM HtBoaInOrg h")
public class HtBoaInOrg implements Serializable {
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

	@Column(name="JPA_VERSION")
	private int jpaVersion;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_MODIFIED_DATETIME")
	private Date lastModifiedDatetime;

	@Column(name="ORG_CODE")
	private String orgCode;

	@Column(name="ORG_NAME")
	private String orgName;

	@Column(name="ORG_NAME_CN")
	private String orgNameCn;

	@Column(name="ORG_PATH")
	private String orgPath;

	@Column(name="ORG_TYPE")
	private String orgType;

	@Column(name="PARENT_ORG_CODE")
	private String parentOrgCode;
	
	@Column(name="REMARK")
	private String remark;

	@Column(name="ROOT_ORG_CODE")
	private String rootOrgCode;

	@Column(name="SEQUENCE")
	private int sequence;

	@Column(name="UPDATE_OPERATOR")
	private String updateOperator;

	public HtBoaInOrg() {
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

	public String getOrgCode() {
		return this.orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgName() {
		return this.orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgNameCn() {
		return this.orgNameCn;
	}

	public void setOrgNameCn(String orgNameCn) {
		this.orgNameCn = orgNameCn;
	}

	public String getOrgPath() {
		return this.orgPath;
	}

	public void setOrgPath(String orgPath) {
		this.orgPath = orgPath;
	}

	public String getOrgType() {
		return this.orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getParentOrgCode() {
		return this.parentOrgCode;
	}

	public void setParentOrgCode(String parentOrgCode) {
		this.parentOrgCode = parentOrgCode;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRootOrgCode() {
		return this.rootOrgCode;
	}

	public void setRootOrgCode(String rootOrgCode) {
		this.rootOrgCode = rootOrgCode;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getUpdateOperator() {
		return this.updateOperator;
	}

	public void setUpdateOperator(String updateOperator) {
		this.updateOperator = updateOperator;
	}

}