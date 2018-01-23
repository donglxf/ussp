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
    int sequence;
    
    @ApiModelProperty(value = "创建人", dataType = "string")
    String createOperator;

    @ApiModelProperty(value = "创建时间", dataType = "date")
    Date createdDatetime;

    @ApiModelProperty(value = "更新人", dataType = "date")
    String updateOperator;

    @ApiModelProperty(value = "最后修改时间", dataType = "string")
    Date lastModifiedDatetime;
    
    @ApiModelProperty(value = "状态", dataType = "int")
    int delFlag;

	public BoaInOrgInfo(String orgCode, String orgName, String orgNameCn, String parentOrgCode,
			String rootOrgCode, String orgPath, String orgType, int sequence,  String createOperator,
			Date createdDatetime, String updateOperator, Date lastModifiedDatetime,int delFlag,long id) {
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
			String rootOrgCode, String orgPath, String orgType, int sequence,String createOperator) {
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
    

}
