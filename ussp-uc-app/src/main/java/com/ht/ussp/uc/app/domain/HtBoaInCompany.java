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
@Table(name="HT_BOA_IN_COMPANY")
@NamedQuery(name="HtBoaInCompany.findAll", query="SELECT h FROM HtBoaInCompany h")
public class HtBoaInCompany implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="BRANCH_CODE")
	private String branchCode;
	
	@Column(name="BRANCH_NAME_CONTRACT") // 分公司名称(合同)
	private String branchNameContract;
	
	@Column(name="BRANCH_ADDRESS_CONTRACT") //分公司地址(合同)
	private String branchAddressContract;
	
	@Column(name="BRANCH_PHONE_CONTRACT") // 电话(合同)
	private String branchPhoneContract;
	
	@Column(name="BRANCH_PLEDGE_EMAIL_CONTRACT") //[分公司抵押权人电子邮件地址(合同)]
	private String branchPledgeEmailContract;
	
	@Column(name="LEGAL_PERSON") //法人代表
	private String legalPerson;
	
	/*@Column(name="DEPT_TEL") //联系电话
	private String deptTel;
	
	@Column(name="DEPT_USER")
	private String deptUser;
	
	@Column(name="DEPT_ADDRESS")
	private String deptAddress;
	
	@Column(name="BUSINESS_PHONE") //业务专用客服电话
	private String businessPhone;*/
	
	@Column(name="HORIZONTAL_TEXT")
	private String horizontalText; //电子签章横向文
	
	@Column(name="UNITE_CODE")
	private String uniteCode; //统一社会信用代码
	
	@Column(name="COS_TEXT")
	private String cosText; //电子签章下弦文
	
	@Column(name="STATUS")
	private String status; //状态(0正常 1禁用  2删除)
	
	@Column(name="GUID")
	private String guid; //团贷网对应GUID
	
	@Column(name="HIGHEST_DEBT_RATIO")
	private String highestDebtRatio; //分公司所在地标准件房产最高负债率
	
	@Column(name="CREATE_OPERATOR")
	private String createOperator;

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
	
	 
	
}