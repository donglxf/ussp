/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: BaseController.java
 * Author:   谭荣巧
 * Date:     2018/1/31 10:27
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.base;

import com.ht.ussp.client.UCClient;
import com.ht.ussp.client.dto.LoginInfoDto;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.annotation.Resource;

/**
 * 公共控制类<br>
 * 1、有获取登录信息的方法<br>
 *
 * @author 谭荣巧
 * @Date 2018/1/31 10:27
 */
public class UsspController {

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
        return ucClient.getLoginUserInfo(userId);
    }
}
