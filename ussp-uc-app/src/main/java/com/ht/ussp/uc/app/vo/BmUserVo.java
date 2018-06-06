/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: UserMessageVo.java
 * Author:   谭荣巧
 * Date:     2018/1/15 8:38
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户管理数据对象<br>
 * 用于用户管理界面的用户列表数据<br>
 *
 * @author 谭荣巧
 * @Date 2018/1/15 8:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BmUserVo implements Serializable {
    // 用户ID
    private String bmUserId; //userId
    // 邮箱
    private String email;
    // 身份证号
    private String idNo;
    // 工号
    private String jobNumber;
    // 用户名
    private String userName;
    // 所属信贷机构编码
    private String bmOrgCode;
    //所属信贷机构名称
    private String orgCode;
    // 机构名称
    private String bmOrgName;
    // 手机号
    private String mobile;
    // 用户状态 0 正常  1禁用 2离职  4冻结 5锁定
    private String status;
    //系统编码
    private String app;
     

  
}
