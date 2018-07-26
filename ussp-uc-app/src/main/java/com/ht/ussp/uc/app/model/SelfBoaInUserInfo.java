package com.ht.ussp.uc.app.model;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "SelfBoaInUserInfo", description = "用户个人信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelfBoaInUserInfo {

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

    @ApiModelProperty(value = "机构编码", dataType = "string")
    String orgCode;

    @ApiModelProperty(value = "机构名称", dataType = "string")
    String orgName;

    @ApiModelProperty(value = "机构中文名称", dataType = "string")
    String orgNameCh;

    @ApiModelProperty(value = "根机构编码", dataType = "string")
    String rootOrgCode;

    @ApiModelProperty(value = "根机构名称", dataType = "string")
    String rootOrgName;

    @ApiModelProperty(value = "根机构中文名称", dataType = "string")
    String rootOrgNameCh;

    @ApiModelProperty(value = "机构路径", dataType = "string")
    String orgPath;

    @ApiModelProperty(value = "机构类型", dataType = "string")
    String orgType;

    @ApiModelProperty(value = "岗位编码", dataType = "string")
    List  positionCodes = new ArrayList();

    @ApiModelProperty(value = "角色编码", dataType = "string")
    List  roleCodes = new ArrayList();

    @ApiModelProperty(value = "工号", dataType = "string")
    String jobNumber;
    
    @ApiModelProperty(value = "系统编码", dataType = "string")
    String appCode;
    
    @ApiModelProperty(value = "系统名称", dataType = "string")
    String appName;
    
  //状态: 0 正常  2离职
    @ApiModelProperty(value = "用户状态", dataType = "string")
    String status;
    public SelfBoaInUserInfo(String userId, String userName, String email,
            String idNo, String mobile, String orgCode, String orgName, String orgNameCh,
            String rootOrgCode, String rootOrgName, String rootOrgNameCh, String orgPath,
            String orgType,String jobNumber) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.idNo = idNo;
        this.mobile = mobile;
        this.orgCode = orgCode;
        this.orgName = orgName;
        this.orgNameCh = orgNameCh;
        this.rootOrgCode = rootOrgCode;
        this.rootOrgName = rootOrgName;
        this.rootOrgNameCh = rootOrgNameCh;
        this.orgPath = orgPath;
        this.orgType = orgType;
        this.jobNumber = jobNumber;
    }
    
    public SelfBoaInUserInfo(String userId, String userName, String email,
            String idNo, String mobile, String orgCode, String jobNumber,String appCode,String status ) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.idNo = idNo;
        this.mobile = mobile;
        this.orgCode = orgCode;
        this.jobNumber = jobNumber;
        this.appCode = appCode;
        this.status = status;
    }

}
