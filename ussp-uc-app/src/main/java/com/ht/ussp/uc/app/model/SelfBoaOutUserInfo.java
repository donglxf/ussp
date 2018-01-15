package com.ht.ussp.uc.app.model;

import java.util.HashSet;
import java.util.Set;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "SelfBoaOutUserInfo", description = "用户个人信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelfBoaOutUserInfo {

    @ApiModelProperty(value = "用户ID", dataType = "string")
    String userId;

    @ApiModelProperty(value = "用户名", dataType = "string")
    String userName;

    @ApiModelProperty(value = "电子邮箱", dataType = "string")
    String email;

    @ApiModelProperty(value = "身份证号", dataType = "string")
    String idNo;

    @ApiModelProperty(value = "手机号", dataType = "string")
    String mobile;

    @ApiModelProperty(value = "角色编码", dataType = "string")
    Set<String> roleCodes = new HashSet<String>(0);

    @ApiModelProperty(value = "角色英文名", dataType = "string")
    Set<String> roleNames = new HashSet<String>(0);

    @ApiModelProperty(value = "角色中文名", dataType = "string")
    Set<String> roleNameChs = new HashSet<String>(0);

    public SelfBoaOutUserInfo(String userId, String userName, String email,
            String idNo, String mobile) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.idNo = idNo;
        this.mobile = mobile;
    }

}
