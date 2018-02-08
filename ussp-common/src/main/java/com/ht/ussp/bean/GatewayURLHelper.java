/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: GatewayURLHelper.java
 * Author:   谭荣巧
 * Date:     2018/2/8 9:12
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 获取网关地址的控制类<br>
 * 为了实现通过docker镜像发布前端应用，网关地址需要通过application.yml动态配置<br>
 *
 * @author 谭荣巧
 * @Date 2018/2/8 9:12
 */
@RestController
@RequestMapping("/common")
public class GatewayURLHelper {

    @Value("${ht.config.ui.gatewayUrl:htt://localhost:30111}")
    private String gatewayUrl;

    @GetMapping(value = "/config.js", produces = "application/javascript")
    public String config() {
        return String.format("var gatewayUrl='%s';", gatewayUrl);
    }
}
