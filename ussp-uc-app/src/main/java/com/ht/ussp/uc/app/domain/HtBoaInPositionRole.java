package com.ht.ussp.uc.app.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * 
* @ClassName: HtBoaInPositionRole
* @Description: 岗位角色关联表
* @author wim qiuwenwu@hongte.info
* @date 2018年1月5日 下午2:51:01
 */
@Entity
@Table(name="HT_BOA_IN_POSITION_ROLE")
@NamedQuery(name="HtBoaInPositionRole.findAll", query="SELECT h FROM HtBoaInPositionRole h")
public class HtBoaInPositionRole implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
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

	@Column(name="POSITION_CODE")
	private String positionCode;

	@Column(name="ROLE_CODE")
	private String roleCode;

	@Column(name="ROOT_ORG_CODE")
	private String rootOrgCode;

	@Column(name="UPDATE_OPERATOR")
	private String updateOperator;

	public HtBoaInPositionRole() {
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

	public String getPositionCode() {
		return this.positionCode;
	}

	public void setPositionCode(String positionCode) {
		this.positionCode = positionCode;
	}

	public String getRoleCode() {
		return this.roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRootOrgCode() {
		return this.rootOrgCode;
	}

	public void setRootOrgCode(String rootOrgCode) {
		this.rootOrgCode = rootOrgCode;
	}

	public String getUpdateOperator() {
		return this.updateOperator;
	}

	public void setUpdateOperator(String updateOperator) {
		this.updateOperator = updateOperator;
	}

}