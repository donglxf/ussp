package com.ht.ussp.uc.app.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ht.ussp.uc.app.domain.HtBoaOutUser;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "BoaOutRoleInfo", description = "角色信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoaOutRoleInfo {

    @ApiModelProperty(value = "角色编号", dataType = "string")
    String roleCode;

    @ApiModelProperty(value = "角色名称", dataType = "string")
    String roleName;

    @ApiModelProperty(value = "角色名称中文", dataType = "string")
    String roleNameCn;

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

    @ApiModelProperty(value = "用户集")
    List<HtBoaOutUser> users = new ArrayList<HtBoaOutUser>(0);

    public BoaOutRoleInfo(String roleCode, String roleName, String roleNameCn,
            String status, String createOperator, Date createdDatetime,
            String updateOperator, Date lastModifiedDatetime) {
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.roleNameCn = roleNameCn;
        this.status = status;
        this.createOperator = createOperator;
        this.createdDatetime = createdDatetime;
        this.updateOperator = updateOperator;
        this.lastModifiedDatetime = lastModifiedDatetime;
    }

}
