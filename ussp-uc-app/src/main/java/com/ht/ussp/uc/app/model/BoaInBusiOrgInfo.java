package com.ht.ussp.uc.app.model;

import java.util.Date;

import javax.persistence.Column;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "BoaInOrgInfo", description = "业务组织机构信息")
@Data
@NoArgsConstructor
public class BoaInBusiOrgInfo {

	@ApiModelProperty(value = "业务机构编号", dataType = "string")
	private String businessOrgCode;
	
	@ApiModelProperty(value = "业务机构名称", dataType = "string")
	private String businessOrgName;
	
	@ApiModelProperty(value = "父业务机构编码", dataType = "string")
	private String parentOrgCode;
	
	private String businessGroup;
	
	@ApiModelProperty(value = "分公司编码", dataType = "string")
	private String branchCode;
	
	@ApiModelProperty(value = "片区编码", dataType = "string")
	private String districtCode;
	
	@ApiModelProperty(value = "所属财务中心", dataType = "string")
	private String financeCode;
	
	@ApiModelProperty(value = "所属审批中心", dataType = "string")
	private String approvalCode;
	
	@ApiModelProperty(value = "所属流程中心", dataType = "string")
	private String activityCode;
	
	@ApiModelProperty(value = "信贷编码", dataType = "string")
	private String bmOrgCode;
	
	//层级(20-公司层级  40-片区层级  60-分公司层级  80-部门层级  100-小组层级)
	private Integer orgLevel;
	
	//状态(0正常 1禁用  2删除)
	private Integer status; 
	
	private String province;
	
	private String city;
	
	private String county;
	
	//数据来源(1：用户权限系统；2：钉钉同步,3：信贷系统机构)
	private Integer dataSource;
	
	private String createOperator;
	
	private Integer sequence;

	private Date createdDatetime;

	private Integer jpaVersion;

	private Date updateDatetime;

	private String updateOperator;
	
	private Integer isHeadDept;
	
	private Integer isAppRovalDept;
	
	////HtBoaInCompany
	
	private String deptAddress;
	
	private String deptTel;
	
	private String businessPhone;
	
	private String deptUser;
	
	private String branchNameContract;
	
	private String branchAddressContract;
	
	private String branchPhoneContract;
	
	private String branchPledgeEmailContract;
	
	private String legalPerson;
	
	private String horizontalText; //电子签章横向文
	
	private String uniteCode; //统一社会信用代码
	
	private String cosText; //电子签章下弦文
	
	private String guid; //团贷网对应GUID
	
	private String highestDebtRatio; //分公司所在地标准件房产最高负债率

	public BoaInBusiOrgInfo(String businessOrgCode, String businessOrgName, String parentOrgCode, String businessGroup,
			String branchCode, String districtCode, String financeCode, String approvalCode, String activityCode,
			String bmOrgCode, Integer orgLevel, Integer status, String province, String city, String county,
			Integer dataSource, //String createOperator, Integer sequence, Date createdDatetime, Integer jpaVersion, Date updateDatetime, String updateOperator, 
			Integer isHeadDept, Integer isAppRovalDept, String deptAddress,
			String deptTel, String deptUser, String businessPhone, String branchNameContract,
			String branchAddressContract, String branchPhoneContract, String branchPledgeEmailContract,
			String legalPerson, String horizontalText, String uniteCode, String cosText,String guid,String highestDebtRatio ) {
		this.businessOrgCode = businessOrgCode;
		this.businessOrgName = businessOrgName;
		this.parentOrgCode = parentOrgCode;
		this.businessGroup = businessGroup;
		this.branchCode = branchCode;
		this.districtCode = districtCode;
		this.financeCode = financeCode;
		this.approvalCode = approvalCode;
		this.activityCode = activityCode;
		this.bmOrgCode = bmOrgCode;
		this.orgLevel = orgLevel;
		this.status = status;
		this.province = province;
		this.city = city;
		this.county = county;
		this.dataSource = dataSource;
		/*this.createOperator = createOperator;
		this.sequence = sequence;
		this.createdDatetime = createdDatetime;
		this.jpaVersion = jpaVersion;
		this.updateDatetime = updateDatetime;
		this.updateOperator = updateOperator;*/
		this.isHeadDept = isHeadDept;
		this.isAppRovalDept = isAppRovalDept;
		this.deptAddress = deptAddress;
		this.deptTel = deptTel;
		this.businessPhone = businessPhone;
		this.deptUser = deptUser;
		this.branchNameContract = branchNameContract;
		this.branchAddressContract = branchAddressContract;
		this.branchPhoneContract = branchPhoneContract;
		this.branchPledgeEmailContract = branchPledgeEmailContract;
		this.legalPerson = legalPerson;
		this.horizontalText = horizontalText;
		this.uniteCode = uniteCode;
		this.cosText = cosText;
		this.guid = guid;
		this.highestDebtRatio = highestDebtRatio;
	}
	
	
	
  
}
