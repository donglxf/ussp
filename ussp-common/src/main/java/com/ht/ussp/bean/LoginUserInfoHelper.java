/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: LoginUserInfoHelper.java
 * Author:   谭荣巧
 * Date:     2018/2/6 17:32
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.bean;

import com.ht.ussp.client.UCClient;
import com.ht.ussp.client.dto.LoginInfoDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.annotation.Resource;

/**
 * 登录用户助手<br>
 * 获取登录用户信息<br>
 *
 * @author 谭荣巧
 * @Date 2018/2/6 17:32
 */
@Log4j2
@ControllerAdvice
public class LoginUserInfoHelper {
    private String userId;

    @Resource
    private UCClient ucClient;

    @ModelAttribute
    public void intLogin(@RequestHeader(value = "userId", required = false) String userId) {
        this.userId = userId;
    }

    /**
     * 获取用户登录信息<br>
     *
     * @author 谭荣巧
     * @Date 2018/1/31 10:41
     */
    public LoginInfoDto getLoginInfo() {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        try {
            return ucClient.getLoginUserInfo(userId);
        } catch (Exception ex) {
            log.error("获取登录信息发生异常。", ex);
            return null;
        }
    }
}
