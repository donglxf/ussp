package com.ht.ussp.uc.app.vo;

import io.swagger.annotations.ApiModelProperty;
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
public class LoginInfoVo {
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

	// 是否管理员 Y是 N不是
	private String controller;

	// 所属业务机构
	private String bussinesOrgCode;

	@ApiModelProperty(value = "所属分公司", dataType = "string")
	String branchCode;

	@ApiModelProperty(value = "所属片区", dataType = "string")
	String districtCode;

	@ApiModelProperty(value = "所属省", dataType = "string")
	String province;

	@ApiModelProperty(value = "所属市", dataType = "string")
	String city;

	public LoginInfoVo(String userId, String jobNumber, String userName, String orgCode, String orgName, String email,
			String idNo, String mobile, String ddUserId, String bmUserId, String ddOrgCode, String bmOrgCode,
			String controller) {
		super();
		this.userId = userId;
		this.jobNumber = jobNumber;
		this.userName = userName;
		this.orgCode = orgCode;
		this.orgName = orgName;
		this.email = email;
		this.idNo = idNo;
		this.mobile = mobile;
		this.ddUserId = ddUserId;
		this.bmUserId = bmUserId;
		this.ddOrgCode = ddOrgCode;
		this.bmOrgCode = bmOrgCode;
		this.controller = controller;
	}

}
