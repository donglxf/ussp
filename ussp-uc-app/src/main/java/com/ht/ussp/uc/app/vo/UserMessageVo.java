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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
public class UserMessageVo {
    //主键
    private String id;
    // 用户ID
    private String userId;
    // 工号
    private String jobNumber;
    // 用户名
    private String userName;
    // 机构编码
    private String orgCode;
    // 机构名称
    private String orgCodeName;
    // 手机号
    private String mobile;
    // 邮箱
    private String email;
    // 身份证号
    private String idNo;
    // 使用状态  0正常  1删除
    private int delFlag;
    //创建人
    private String createOperator;
    //创建时间
    private Date createdDatetime;
    //修改人
    private String updateOperator;
    // 最后修改时间
    private Date lastModifiedDatetime;
    // 用户状态 0正常 1禁用 2密码初始化 4冻结 5锁定
    private String status;
    //密码错误次数
    private int failedCount;
    //失效时间
    private Date pwdExpDate;
    //生效时间
    private Date effectiveDate;
}
