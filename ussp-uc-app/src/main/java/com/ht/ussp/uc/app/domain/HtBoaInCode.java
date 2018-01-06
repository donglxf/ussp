package com.ht.ussp.uc.app.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * 
* @ClassName: HtBoaInCode
* @Description: 码表
* @author wim qiuwenwu@hongte.info
* @date 2018年1月5日 下午2:48:09
 */
@Entity
@Table(name="HT_BOA_IN_CODE")
@NamedQuery(name="HtBoaInCode.findAll", query="SELECT h FROM HtBoaInCode h")
public class HtBoaInCode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	private Long id;

	@Column(name="BIZ_CODE")
	private String bizCode;

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
	
	@Column(name="NAME")
	private String name;

	@Column(name="NAME_CN")
	private String nameCn;

	@Column(name="ROOT_ORG_CODE")
	private String rootOrgCode;
	
	@Column(name="SEQUENCE")
	private int sequence;
	
	@Column(name="STATUS")
	private String status;

	@Column(name="TYPE_CODE")
	private String typeCode;

	@Column(name="UPDATE_OPERATOR")
	private String updateOperator;

	public HtBoaInCode() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBizCode() {
		return this.bizCode;
	}

	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameCn() {
		return this.nameCn;
	}

	public void setNameCn(String nameCn) {
		this.nameCn = nameCn;
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

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTypeCode() {
		return this.typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getUpdateOperator() {
		return this.updateOperator;
	}

	public void setUpdateOperator(String updateOperator) {
		this.updateOperator = updateOperator;
	}

}