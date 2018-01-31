package com.ht.ussp.uc.app.vo;

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
    // 邮箱
    private String email;
    // 身份证号
    private String idNo;
    // 手机号
    private String mobile;
}
