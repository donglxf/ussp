package com.ht.ussp.uc.app.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 
* @ClassName: HtBoaInOrgLogo
* @Description: 机构logo图片表
* @author wim qiuwenwu@hongte.info
* @date 2018年1月5日 下午2:50:19
 */
@Entity
@Table(name="HT_BOA_IN_ORG_LOGO")
@NamedQuery(name="HtBoaInOrgLogo.findAll", query="SELECT h FROM HtBoaInOrgLogo h")
public class HtBoaInOrgLogo implements Serializable {
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

	@Column(name="IMG_NAME")
	private String imgName;

	@Column(name="JPA_VERSION")
	private int jpaVersion;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_MODIFIED_DATETIME")
	private Date lastModifiedDatetime;

	@Lob
	@Column(name="LOGO_IMG")
	private byte[] logoImg;

	@Column(name="ORG_CODE")
	private String orgCode;

	@Column(name="UPDATE_OPERATOR")
	private String updateOperator;

	@Column(name="VERSION")
	private BigDecimal version;

	public HtBoaInOrgLogo() {
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

	public String getImgName() {
		return this.imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
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

	public byte[] getLogoImg() {
		return this.logoImg;
	}

	public void setLogoImg(byte[] logoImg) {
		this.logoImg = logoImg;
	}

	public String getOrgCode() {
		return this.orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getUpdateOperator() {
		return this.updateOperator;
	}

	public void setUpdateOperator(String updateOperator) {
		this.updateOperator = updateOperator;
	}

	public BigDecimal getVersion() {
		return this.version;
	}

	public void setVersion(BigDecimal version) {
		this.version = version;
	}

}