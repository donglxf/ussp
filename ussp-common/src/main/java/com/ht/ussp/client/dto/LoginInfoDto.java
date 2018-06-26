package com.ht.ussp.client.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: UserInfoVo
 * @Description: 供各客户端查询用户信息用
 * @date 2018年1月22日 下午10:04:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginInfoDto {
    // 用户ID
    private String userId;
    // 工号
    private String jobNumber;
    // 用户名
    private String userName;
    // 机构编码
    private String orgCode;
    // 机构名称
    private String orgName;
    // 邮箱
    private String email;
    // 身份证号
    private String idNo;
    // 手机号
    private String mobile;

 // 钉钉userID
    private String ddUserId;
 // 信贷userID
    private String bmUserId;
 // 钉钉机构
    private String ddOrgCode;
 // 信贷机构
    private String bmOrgCode;
    
    //是否管理员  Y是  N不是
  	private String controller;
  	
 // 所属业务机构
 	private String bussinesOrgCode;
  	
 // 所属业务机构
 	private String bussinesOrgName;
 	
 	private String branchName;
  	//所属分公司
  	private String branchCode;

	//所属片区
  	private String districtCode;

	//所属省
  	private String province;

	//所属市
  	private String city;
	
	//是否是总部
  	private String isHeadDept;
	
	//是否是审批中心
  	private String isAppRovalDept;
}
