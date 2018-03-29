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
 * 用户关联的用户信息
 * @author tangxs
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContrastVo implements Serializable {
    //主键
    private Long id;
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
    // 手机号
    private String mobile;
    // 邮箱
    private String email;
    
    //信贷用户信息
 // 用户ID
    private String bmUserId;
    // 工号
    private String bmJobNumber;
    // 用户名
    private String bmUserName;
    // 机构编码
    private String bmOrgCode;
    // 机构名称
    private String bmOrgName;
    // 手机号
    private String bmMobile;
    // 邮箱
    private String bmEmail;

}
