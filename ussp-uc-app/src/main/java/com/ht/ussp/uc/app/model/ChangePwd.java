package com.ht.ussp.uc.app.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "ChangePwd", description = "修改密码")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePwd {
    
     
	@ApiModelProperty(value = "userId", dataType = "string")
    String userId;
	
    @ApiModelProperty(value = "新密码", dataType = "string", example = "123456.")
    String newPwd;
    
    @ApiModelProperty(value = "旧密码", dataType = "string", example = "123456.")
    String oldPwd;
    
    @ApiModelProperty(value = "token", dataType = "string")
    String token;
    
}
