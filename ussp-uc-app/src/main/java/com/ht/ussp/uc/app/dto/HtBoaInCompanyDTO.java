package com.ht.ussp.uc.app.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @ClassName: HtBoaInCompanyDTO
 * @Description: 公司信息
 * @author wim qiuwenwu@hongte.info
 * @date 2018年7月19日 下午3:20:17
 */
@Data
public class HtBoaInCompanyDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String businessPhone;
	private String companyAddress;
	private String companyAddressContract;
	private String companyCode;
	private String companyName;
	private String companyNameContract;
	private String companyPhone;
	private String companyPhoneContract;
	private String companyPledgeEmailContract;
	private BigDecimal highestDebtRatio;
	private String horizontalText;
	private String legalPerson;
	private String thirdQuarterText;
	private String tuandaiGuid;
	private String unifiedSocialCreditCode;
}
