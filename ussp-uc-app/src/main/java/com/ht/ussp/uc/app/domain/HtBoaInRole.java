package com.ht.ussp.uc.app.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * 
* @ClassName: HtBoaInRole
* @Description: 角色表
* @author wim qiuwenwu@hongte.info
* @date 2018年1月5日 下午2:52:57
 */
@Entity
@Table(name="HT_BOA_IN_ROLE")
@NamedQuery(name="HtBoaInRole.findAll", query="SELECT h FROM HtBoaInRole h")
public class HtBoaInRole implements Serializable {
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

	@Column(name="ROLE_CODE")
	private String roleCode;

	@Column(name="ROLE_NAME")
	private String roleName;

	@Column(name="ROLE_NAME_CN")
	private String roleNameCn;

	@Column(name="ROOT_ORG_CODE")
	private String rootOrgCode;

	@Column(name="STATUS")
	private String status;

	@Column(name="UPDATE_OPERATOR")
	private String updateOperator;

	public HtBoaInRole() {
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

	public String getRoleCode() {
		return this.roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleNameCn() {
		return this.roleNameCn;
	}

	public void setRoleNameCn(String roleNameCn) {
		this.roleNameCn = roleNameCn;
	}

	public String getRootOrgCode() {
		return this.rootOrgCode;
	}

	public void setRootOrgCode(String rootOrgCode) {
		this.rootOrgCode = rootOrgCode;
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

}