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
import com.ht.ussp.client.dto.LoginInfoDto;
import com.ht.ussp.core.Result;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户权限中心服务接口<br>
 * 1、API资源同步<br>
 *
 * @author 谭荣巧
 * @Date 2018/1/18 14:22
 */
@FeignClient("ussp-uc-app")
public interface UCClient {

    /**
     * 同步API资源到用户权限中心<br>
     *
     * @param apiResourceDto API资源信息
     * @author 谭荣巧
     * @Date 2018/1/18 14:28
     */
    @PostMapping("/auth/api/aynch")
    void resourceApiAynch(@RequestBody ApiResourceDto apiResourceDto);

    /**
     * 获取登录信息<br>
     *
     * @param userId 用户编码
     * @return 用户登录信息
     * @author 谭荣巧
     * @Date 2018/1/31 10:26
     */
    @GetMapping(value = "/user/getLoginUserInfo")
    LoginInfoDto getLoginUserInfo(@RequestParam("userId") String userId);
    
    /**
     * 
     * @Title: getCustomResouce 
     * @Description: 获取用户自定权限
     * @return Result
     * @throws
     * @author wim qiuwenwu@hongte.info 
     * @date 2018年2月10日 下午2:40:21
     */
    @GetMapping(value = "/auth/getCustomResouce")
    Result getCustomResouce(@RequestParam("userId") String userId,@RequestParam("app") String app);
    
    /**
     * 
     * @Title: IsHasCustomResouce 
     * @Description: 判断用户是否有自定义权限  
     * @return Boolean
     * @throws
     * @author wim qiuwenwu@hongte.info 
     * @date 2018年2月10日 下午2:40:42
     */
    @GetMapping(value = "/auth/IsHasCustomResouce")
    Boolean IsHasCustomResouce(@RequestParam("userId") String userId,@RequestParam("rescode") String rescode,@RequestParam("app") String app);
}

