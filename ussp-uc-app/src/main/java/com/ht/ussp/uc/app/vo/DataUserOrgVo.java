package com.ht.ussp.uc.app.vo;

import java.util.List;

import com.ht.ussp.uc.app.domain.HtBoaInOrg;

import lombok.Data;

@Data
public class DataUserOrgVo {

	//用户信息
	 // 用户ID
    private String userId; //userId
    // 邮箱
    private String email;
    // 身份证号
    private String idNo;
    // 工号
    private String jobNumber;
    // 用户名
    private String userName;
    //所属信贷机构名称
    private String orgCode;
    // 手机号
    private String mobile;
    // 用户状态 0 正常  1禁用 2离职  4冻结 5锁定
    private String status;
	//所属机构信息
    HtBoaInOrg htBoaInOrg;
    //所属父机构信息
    HtBoaInOrg parentHtBoaInOrg;
	//子机构信息
    List<HtBoaInOrg> subHtBoaInOrgList;

}
