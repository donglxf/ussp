package com.ht.ussp.uc.app.model;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "BoaInOrgInfo", description = "组织机构信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoaInOrgInfo {

	@ApiModelProperty(value = "id", dataType = "long")
	long id;
	
    @ApiModelProperty(value = "机构编号", dataType = "string")
    String orgCode;

    @ApiModelProperty(value = "机构名称", dataType = "string")
    String orgName;
    
    @ApiModelProperty(value = "业务机构编号", dataType = "string")
    String businessOrgCode;

    @ApiModelProperty(value = "业务机构名称", dataType = "string")
    String businessOrgName;

    @ApiModelProperty(value = "机构名称中文", dataType = "string")
    String orgNameCn;

    @ApiModelProperty(value = "父机构编号", dataType = "string")
    String parentOrgCode;
    
    @ApiModelProperty(value = "根机构编号", dataType = "string")
    String rootOrgCode;
    
    @ApiModelProperty(value = "机构路径", dataType = "string")
    String orgPath;
    
    @ApiModelProperty(value = "机构类型", dataType = "string")
    String orgType;

    @ApiModelProperty(value = "顺序号", dataType = "int")
    Integer sequence;
    
    @ApiModelProperty(value = "创建人", dataType = "string")
    String createOperator;

    @ApiModelProperty(value = "创建时间", dataType = "date")
    Date createdDatetime;

    @ApiModelProperty(value = "更新人", dataType = "date")
    String updateOperator;

    @ApiModelProperty(value = "最后修改时间", dataType = "string")
    Date lastModifiedDatetime;
    
    @ApiModelProperty(value = "状态", dataType = "int")
    Integer delFlag;
    
    @ApiModelProperty(value = "所属分公司", dataType = "string")
    String branchCode;

    @ApiModelProperty(value = "所属片区", dataType = "string")
    String districtCode;
    
    @ApiModelProperty(value = "机构层级 (20-公司层级  40-片区层级  60-分公司层级  80-部门层级  100-小组层级)", dataType = "int")
    Integer orgLevel;
    
    @ApiModelProperty(value = "所属省", dataType = "string")
    String province;
    
    @ApiModelProperty(value = "所属市", dataType = "string")
    String city;
    
	public BoaInOrgInfo(String orgCode, String orgName, String orgNameCn, String parentOrgCode,
			String rootOrgCode, String orgPath, String orgType, Integer sequence,  String createOperator,
			Date createdDatetime, String updateOperator, Date lastModifiedDatetime,Integer delFlag,long id) {
		this.orgCode = orgCode;
		this.orgName = orgName;
		this.orgNameCn = orgNameCn;
		this.parentOrgCode = parentOrgCode;
		this.rootOrgCode = rootOrgCode;
		this.orgPath = orgPath;
		this.orgType = orgType;
		this.sequence = sequence;
		this.createOperator = createOperator;
		this.createdDatetime = createdDatetime;
		this.updateOperator = updateOperator;
		this.lastModifiedDatetime = lastModifiedDatetime;
		this.delFlag = delFlag;
		this.id = id;
	}

	public BoaInOrgInfo(String orgCode, String orgName, String orgNameCn, String parentOrgCode,
			String rootOrgCode, String orgPath, String orgType, Integer sequence,String createOperator) {
		this.orgCode = orgCode;
		this.orgName = orgName;
		this.orgNameCn = orgNameCn;
		this.parentOrgCode = parentOrgCode;
		this.rootOrgCode = rootOrgCode;
		this.orgPath = orgPath;
		this.orgType = orgType;
		this.sequence = sequence;
		this.createOperator = createOperator;
	}
    
	public BoaInOrgInfo(String orgCode, String orgName, String parentOrgCode, Integer sequence, Integer delFlag,
			String branchCode, String districtCode, Integer orgLevel, String province, String city,long id) {
		this.orgCode = orgCode;
		this.orgName = orgName;
		this.parentOrgCode = parentOrgCode;
		this.sequence = sequence;
		this.delFlag = delFlag;
		this.branchCode = branchCode;
		this.districtCode = districtCode;
		this.orgLevel = orgLevel;
		this.province = province;
		this.city = city;
		this.id = id;
	}
}
