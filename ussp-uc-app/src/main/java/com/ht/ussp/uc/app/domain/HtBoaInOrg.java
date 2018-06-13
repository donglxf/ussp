package com.ht.ussp.uc.app.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;


/**
 * 
* @ClassName: HtBoaInOrg
* @Description: 组织机构表
* @author wim qiuwenwu@hongte.info
* @date 2018年1月5日 下午2:49:49
 */
@Data
@Entity
@Table(name="HT_BOA_IN_ORG")
@NamedQuery(name="HtBoaInOrg.findAll", query="SELECT h FROM HtBoaInOrg h")
public class HtBoaInOrg implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="CREATE_OPERATOR")
	private String createOperator;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATETIME", insertable = false, updatable = false)
	private Date createdDatetime;

	@Column(name="DEL_FLAG")
	private int delFlag;

	@Column(name="JPA_VERSION")
	private int jpaVersion;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_MODIFIED_DATETIME", insertable = false, updatable = false)
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

	@Column(name="DATA_SOURCE")
	private int dataSource;

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

}