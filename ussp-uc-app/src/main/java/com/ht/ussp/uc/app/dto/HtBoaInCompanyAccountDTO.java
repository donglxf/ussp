package com.ht.ussp.uc.app.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 * @ClassName: HtBoaInCompanyAccountDTO
 * @Description: 公司账户DTO
 * @author wim qiuwenwu@hongte.info
 * @date 2018年7月20日 上午11:02:36
 */
@Data
public class HtBoaInCompanyAccountDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// 账户编号
	private String accountCode;
	
	// 账户名
	private String accountName;
	
	// 银行卡号
	private String bankCard;
	
	// 银行名称
	private String bankName;
	
	// 分公司编码
	private String companyCode;
	
	// 是否居间账户
	private Boolean isIntermediaryAccount;
	
	// 是否还款账户
	private Boolean isRetuenAccount;	

	// 开户行名
	private String openName;

}
