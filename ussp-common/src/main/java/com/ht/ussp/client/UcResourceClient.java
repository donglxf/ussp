/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: UcClient.java
 * Author:   谭荣巧
 * Date:     2018/1/18 14:22
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.client;

import com.ht.ussp.client.dto.ApiResourceDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户权限中心服务接口<br>
 * 1、API资源同步<br>
 *
 * @author 谭荣巧
 * @Date 2018/1/18 14:22
 */
@FeignClient("ussp-uc-app")
public interface UcResourceClient {

    /**
     * 同步API资源到用户权限中心<br>
     *
     * @param apiResourceDto API资源信息
     * @author 谭荣巧
     * @Date 2018/1/18 14:28
     */
    @PostMapping("/res/api/aynch")
    void resourceApiAynch(@RequestBody ApiResourceDto apiResourceDto);
}
