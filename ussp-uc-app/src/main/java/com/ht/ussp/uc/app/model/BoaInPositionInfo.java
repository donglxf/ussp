package com.ht.ussp.uc.app.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ht.ussp.uc.app.domain.HtBoaInRole;
import com.ht.ussp.uc.app.domain.HtBoaInUser;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "BoaInPositionInfo", description = "岗位信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoaInPositionInfo {

	@ApiModelProperty(value = "id", dataType = "long")
	long id;
	
    @ApiModelProperty(value = "岗位编号", dataType = "string")
    String positionCode;

    @ApiModelProperty(value = "岗位名称", dataType = "string")
    String positionName;

    @ApiModelProperty(value = "岗位名称中文", dataType = "string")
    String positionNameCn;

    @ApiModelProperty(value = "父机构编号", dataType = "string")
    String pOrgCode;

    @ApiModelProperty(value = "父机构名称", dataType = "string")
    String pOrgName;

    @ApiModelProperty(value = "父机构名称中文", dataType = "string")
    String pOrgNameCn;

    @ApiModelProperty(value = "父机构类型", dataType = "string")
    String pOrgType;

    @ApiModelProperty(value = "根机构编码", dataType = "string")
    String rOrgCode;

    @ApiModelProperty(value = "根机构名称", dataType = "string")
    String rOrgName;

    @ApiModelProperty(value = "根机构名称中文", dataType = "string")
    String rOrgNameCn;

    @ApiModelProperty(value = "根机构类型", dataType = "string")
    String rOrgType;

    @ApiModelProperty(value = "机构路径", dataType = "string")
    String orgPath;

    @ApiModelProperty(value = "顺序号", dataType = "int")
    Integer sequence;

    @ApiModelProperty(value = "创建人", dataType = "string")
    String createOperator;

    @ApiModelProperty(value = "创建时间", dataType = "date")
    Date createdDatetime;

    @ApiModelProperty(value = "更新人", dataType = "date")
    String updateOperator;

    @ApiModelProperty(value = "状态", dataType = "int")
    int delFlag;
    
    @ApiModelProperty(value = "最后修改时间", dataType = "string")
    Date lastModifiedDatetime;
    
    @ApiModelProperty(value = "状态", dataType = "string")
    String status;

    @ApiModelProperty(value = "用户集")
    List<HtBoaInUser> users = new ArrayList<HtBoaInUser>(0);

    @ApiModelProperty(value = "角色集")
    List<HtBoaInRole> roles = new ArrayList<HtBoaInRole>(0);
    
    public BoaInPositionInfo(long id,String positionCode, String positionName,
            String positionNameCn, String pOrgCode, String pOrgName,
            String pOrgNameCn, String pOrgType, String rOrgCode,
            String rOrgName, String rOrgNameCn, String rOrgType, String orgPath,
            int sequence, String createOperator, Date createdDatetime,
            String updateOperator, Date lastModifiedDatetime,int delFlag,String status) {
        this.positionCode = positionCode;
        this.positionName = positionName;
        this.positionNameCn = positionNameCn;
        this.pOrgCode = pOrgCode;
        this.pOrgName = pOrgName;
        this.pOrgNameCn = pOrgNameCn;
        this.pOrgType = pOrgType;
        this.rOrgCode = rOrgCode;
        this.rOrgName = rOrgName;
        this.rOrgNameCn = rOrgNameCn;
        this.rOrgType = rOrgType;
        this.orgPath = orgPath;
        this.sequence = sequence;
        this.createOperator = createOperator;
        this.createdDatetime = createdDatetime;
        this.updateOperator = updateOperator;
        this.lastModifiedDatetime = lastModifiedDatetime;
        this.delFlag = delFlag;
        this.id = id;
        this.status = status;
    }
    

    public BoaInPositionInfo(long id,String positionCode, String positionName,
            String positionNameCn,   String orgPath,
            int sequence, String createOperator, Date createdDatetime,
            String updateOperator, Date lastModifiedDatetime,int delFlag,String status) {
        this.positionCode = positionCode;
        this.positionName = positionName;
        this.positionNameCn = positionNameCn;
        this.orgPath = orgPath;
        this.sequence = sequence;
        this.createOperator = createOperator;
        this.createdDatetime = createdDatetime;
        this.updateOperator = updateOperator;
        this.lastModifiedDatetime = lastModifiedDatetime;
        this.delFlag = delFlag;
        this.id = id;
        this.status = status;
    }

}
