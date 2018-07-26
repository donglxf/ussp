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

@Data
@Entity
@Table(name="HT_BOA_IN_BUSINESS_ORG")
@NamedQuery(name="HtBoaInBusinessOrg.findAll", query="SELECT h FROM HtBoaInBusinessOrg h")
public class HtBoaInBusinessOrg implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="BUSINESS_ORG_CODE")
	private String businessOrgCode;
	
	@Column(name="BUSINESS_ORG_NAME")
	private String businessOrgName;
	
	@Column(name="PARENT_ORG_CODE")
	private String parentOrgCode;
	
	@Column(name="BUSINESS_GROUP")
	private String businessGroup;
	
	@Column(name="BRANCH_CODE")
	private String branchCode;
	
	@Column(name="DISTRICT_CODE")
	private String districtCode;
	
	@Column(name="FINANCE_CODE")
	private String financeCode;
	
	@Column(name="APPROVAL_CODE")
	private String approvalCode;
	
	@Column(name="ACTIVITY_CODE")
	private String activityCode;
	
	@Column(name="BM_ORG_CODE")
	private String bmOrgCode;
	
	@Column(name="ORG_LEVEL")
	private Integer orgLevel;
	
	@Column(name="STATUS")
	private Integer status;
	
	@Column(name="PROVINCE")
	private String province;
	
	@Column(name="CITY")
	private String city;
	
	@Column(name="COUNTY")
	private String county;
	
	//数据来源(1：用户权限系统；2：钉钉同步,3：信贷系统机构)
	@Column(name="DATA_SOURCE")
	private Integer dataSource;
	
	@Column(name="CREATE_OPERATOR")
	private String createOperator;
	
	@Column(name="SEQUENCE")
	private Integer sequence;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATETIME", insertable = false, updatable = false)
	private Date createdDatetime;

	@Column(name="JPA_VERSION")
	private Integer jpaVersion;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATETIME", insertable = false, updatable = false)
	private Date updateDatetime;

	@Column(name="UPDATE_OPERATOR")
	private String updateOperator;
	
	@Column(name="IS_HEAD_DEPT")
	private Integer isHeadDept;
	
	@Column(name="IS_APPROVAL_DEPT")
	private Integer isAppRovalDept;
	
	@Column(name="DEPT_ADDRESS")
	private String deptAddress;
	
	@Column(name="DEPT_TEL") //联系电话
	private String deptTel;
	
	@Column(name="BUSINESS_PHONE") //业务专用客服电话
	private String businessPhone;
	
	@Column(name="DEPT_USER")
	private String deptUser;
	
	/*@Column(name="BRANCH_NAME_CONTRACT") // 分公司名称(合同)
	private String branchNameContract;
	
	@Column(name="BRANCH_ADDRESS_CONTRACT") //分公司地址(合同)
	private String branchAddressContract;
	
	@Column(name="BRANCH_PHONE_CONTRACT") // 电话(合同)
	private String branchPhoneContract;
	
	@Column(name="BRANCH_PLEDGE_EMAIL_CONTRACT") //[分公司抵押权人电子邮件地址(合同)]
	private String branchPledgeEmailContract;
	
	@Column(name="LEGAL_PERSON") //法人代表
	private String legalPerson;
	*/
	
}