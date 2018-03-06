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

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
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
public class UserMessageVo implements Serializable {
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
    // 身份证号
    private String idNo;
    // 使用状态  0正常  1删除
    private Integer delFlag;
    //创建人
    private String createOperator;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date createdDatetime;
    //修改人
    private String updateOperator;
    // 最后修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date lastModifiedDatetime;
    // 用户状态 0正常 1禁用 2密码初始化 4冻结 5锁定
    private String status;
    //密码错误次数
    private Integer failedCount;
    //失效时间
    private Date pwdExpDate;
    //生效时间
    private Date effectiveDate;
    
    private String loginId;
    
    private String orgPath;
    
    private String rootOrgCode;
    
    


}
