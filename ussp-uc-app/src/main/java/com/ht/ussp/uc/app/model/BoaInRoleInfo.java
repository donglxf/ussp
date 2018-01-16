package com.ht.ussp.uc.app.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ht.ussp.uc.app.domain.HtBoaInPosition;
import com.ht.ussp.uc.app.domain.HtBoaInUser;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "BoaInRoleInfo", description = "角色信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoaInRoleInfo {

    @ApiModelProperty(value = "角色编号", dataType = "string")
    String roleCode;

    @ApiModelProperty(value = "角色名称", dataType = "string")
    String roleName;

    @ApiModelProperty(value = "角色名称中文", dataType = "string")
    String roleNameCn;

    @ApiModelProperty(value = "根机构编码", dataType = "string")
    String rOrgCode;

    @ApiModelProperty(value = "根机构名称", dataType = "string")
    String rOrgName;

    @ApiModelProperty(value = "根机构名称中文", dataType = "string")
    String rOrgNameCn;

    @ApiModelProperty(value = "根机构类型", dataType = "string")
    String rOrgType;

    @ApiModelProperty(value = "状态", dataType = "string")
    String status;

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

    @ApiModelProperty(value = "用户集")
    List<HtBoaInUser> users = new ArrayList<HtBoaInUser>(0);

    @ApiModelProperty(value = "岗位集")
    List<HtBoaInPosition> positions = new ArrayList<HtBoaInPosition>(0);

    public BoaInRoleInfo(String roleCode, String roleName, String roleNameCn,
            String rOrgCode, String rOrgName, String rOrgNameCn,
            String rOrgType, String status, String createOperator,
            Date createdDatetime, String updateOperator,
            Date lastModifiedDatetime,int delFlag) {
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.roleNameCn = roleNameCn;
        this.rOrgCode = rOrgCode;
        this.rOrgName = rOrgName;
        this.rOrgNameCn = rOrgNameCn;
        this.rOrgType = rOrgType;
        this.status = status;
        this.createOperator = createOperator;
        this.createdDatetime = createdDatetime;
        this.updateOperator = updateOperator;
        this.lastModifiedDatetime = lastModifiedDatetime;
        this.delFlag = delFlag;
    }

}
