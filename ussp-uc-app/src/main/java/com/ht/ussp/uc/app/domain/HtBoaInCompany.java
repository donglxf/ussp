package com.ht.ussp.uc.app.domain;

import java.io.Serializable;
import java.math.BigDecimal;
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

	@Column(name="BUSINESS_PHONE")
	private String businessPhone;

	@Column(name="COMPANY_ADDRESS")
	private String companyAddress;

	@Column(name="COMPANY_ADDRESS_CONTRACT")
	private String companyAddressContract;

	@Column(name="COMPANY_CODE")
	private String companyCode;

	@Column(name="COMPANY_NAME")
	private String companyName;

	@Column(name="COMPANY_NAME_CONTRACT")
	private String companyNameContract;

	@Column(name="COMPANY_PHONE")
	private String companyPhone;

	@Column(name="COMPANY_PHONE_CONTRACT")
	private String companyPhoneContract;

	@Column(name="COMPANY_PLEDGE_EMAIL_CONTRACT")
	private String companyPledgeEmailContract;

	@Column(name="CREATE_OPERATOR")
	private String createOperator;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATETIME")
	private Date createdDatetime;

	@Column(name="DEL_FLAG")
	private int delFlag;

	@Column(name="HIGHEST_DEBT_RATIO")
	private BigDecimal highestDebtRatio;

	@Column(name="HORIZONTAL_TEXT")
	private String horizontalText;

	@Column(name="JPA_VERSION")
	private int jpaVersion;

	@Column(name="LEGAL_PERSON")
	private String legalPerson;

	@Column(name="THIRD_QUARTER_TEXT")
	private String thirdQuarterText;

	@Column(name="TUANDAI_GUID")
	private String tuandaiGuid;

	@Column(name="UNIFIED_SOCIAL_CREDIT_CODE")
	private String unifiedSocialCreditCode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATETIME")
	private Date updateDatetime;

	@Column(name="UPDATE_OPERATOR")
	private String updateOperator;
	
}