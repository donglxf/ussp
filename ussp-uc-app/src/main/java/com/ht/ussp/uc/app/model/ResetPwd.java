package com.ht.ussp.uc.app.model;

import java.util.HashSet;
import java.util.Set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "ResetPsw", description = "重置密码验证参数")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPwd {
    
    @ApiModelProperty(value = "手机号", dataType = "string", example = "13681251250")
    String mobile;
    
    @ApiModelProperty(value = "邮箱地址", dataType = "string", example = "boa@hongte.info")
    String email;
    
    @ApiModelProperty(value = "验证码", dataType = "string", example = "0145")
    String code;
    
    @ApiModelProperty(value = "历史密码组")
    Set<String> histPwds = new HashSet<String>(0);
    
    @ApiModelProperty(value = "新密码", dataType = "string", example = "123456.")
    String newPwd;
}
