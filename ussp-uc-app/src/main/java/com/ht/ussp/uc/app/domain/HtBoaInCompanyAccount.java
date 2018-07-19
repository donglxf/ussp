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
 * @ClassName: HtBoaInCompanyAccount
 * @Description: 公司账户表
 * @author wim qiuwenwu@hongte.info
 * @date 2018年7月19日 下午2:10:20
 */
@Data
@Entity
@Table(name = "HT_BOA_IN_COMPANY_ACCOUNT")
@NamedQuery(name = "HtBoaInCompanyAccount.findAll", query = "SELECT h FROM HtBoaInCompanyAccount h")
public class HtBoaInCompanyAccount implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;
	// 账户编号
	@Column(name = "ACCOUNT_CODE")
	private String accountCode;
	// 账户名
	@Column(name = "ACCOUNT_NAME")
	private String accountName;
	// 银行卡号
	@Column(name = "BANK_CARD")
	private String bankCard;
	// 银行名称
	@Column(name = "BANK_NAME")
	private String bankName;
	// 分公司编码
	@Column(name = "COMPANY_CODE")
	private String companyCode;
	//创建人
	@Column(name = "CREATE_OPERATOR")
	private String createOperator;
	//创建时间
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATETIME")
	private Date createdDatetime;
	//删除标识
	@Column(name = "DEL_FLAG")
	private int delFlag;
	// 是否居间账户
	@Column(name = "IS_INTERMEDIARY_ACCOUNT")
	private byte isIntermediaryAccount;
	// 是否还款账户
	@Column(name = "IS_RETUEN_ACCOUNT")
	private byte isRetuenAccount;
	
	@Column(name = "JPA_VERSION")
	private int jpaVersion;
	//最后修改时间
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATETIME")
	private Date lastModifiedDatetime;
	// 开户行名
	@Column(name = "OPEN_NAME")
	private String openName;
	//更新人
	@Column(name = "UPDATE_OPERATOR")
	private String updateOperator;
}