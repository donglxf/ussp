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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import com.ht.ussp.client.UCClient;
import com.ht.ussp.client.dto.BoaInRoleInfoDto;
import com.ht.ussp.client.dto.HtBoaInOrgDto;
import com.ht.ussp.client.dto.LoginInfoDto;
import com.ht.ussp.client.dto.ResDto;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

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
    @Getter
    private String userId;
    
    @Getter
    private String app;

    @Autowired(required = false)
    private UCClient ucClient;

    @ModelAttribute
    public void intLogin(@RequestHeader(value = "app", required = false) String app) {
        this.app = app;
    }
 
    @ModelAttribute
    public void intLogin2(@RequestHeader(value = "userId", required = false) String userId) {
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
        if (ucClient == null) {
            log.warn("无法获取用户登录信息，可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        try {
            return ucClient.getLoginUserInfo(userId,app);
        } catch (Exception ex) {
            log.error("获取登录信息发生异常。", ex);
            return null;
        }
    }
    
    /**
     * 获取当前用户所有权限信息
     * 资源类型枚举值：ResTypeEnum.RES_TYPE_API.getReturnCode()
     * @return
     */
    public List<ResDto> getUserResouce(String resType) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        if (ucClient == null) {
            log.warn("无法获取当前用户所有权限信息，可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        try {
            return ucClient.getUserResouce(userId, resType, app);
        } catch (Exception ex) {
            log.error("获取当前用户所有权限信息发生异常。", ex);
            return null;
        }
    }
    
    /**
     * 获取当前用户所有角色信息
     * @return
     */
    public List<BoaInRoleInfoDto>  getUserRole() {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        if (ucClient == null) {
            log.warn("无法获取当前用户所有角色信息，可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        try {
            return ucClient.getUserRole(userId);
        } catch (Exception ex) {
            log.error(" 获取当前用户所有角色信息发生异常。", ex);
            return null;
        }
    }
    

    /**
     * 根据userId获取用户信息
     * @param userId 若为空则根据bmUserId查询
     * @param bmUserId 若userId不为空则按userId查询,userId为空则根据bmUserId查询,
     * @return
     */
	public LoginInfoDto getUserInfoByUserId(String userId, String bmUserId) {
		if (ucClient == null) {
			log.warn("无法获取用户信息，可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
		}
		try {
			userId = userId == null ? "" : userId;
			bmUserId = bmUserId == null ? "" : bmUserId;
			app = app == null ? "" : app;
			return ucClient.getUserInfoByUserId(userId, bmUserId,app);
		} catch (Exception ex) {
			log.error("获取登录信息发生异常。", ex);
			return null;
		}
	}
    
	
	/**
     * 获取用户所有权限信息
     * 资源类型枚举值：ResTypeEnum.RES_TYPE_API.getReturnCode()
     * @return
     */
    public List<ResDto> getUserResouces(String resType,String userIds) {
        if (ucClient == null) {
            log.warn("无法获取当前用户所有权限信息，可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        userIds = (StringUtils.isEmpty(userIds))?userId:userIds;
        try {
            return ucClient.getUserResouce(userIds, resType, app);
        } catch (Exception ex) {
            log.error("获取当前用户所有权限信息发生异常。", ex);
            return null;
        }
    }
    
    /**
     * 获取用户所有角色信息
     * @return
     */
    public List<BoaInRoleInfoDto> getUserRoles(String userIds) {
        if (ucClient == null) {
            log.warn("无法获取当前用户所有角色信息，可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        userIds = (StringUtils.isEmpty(userIds))?userId:userIds;
        try {
            return ucClient.getUserRole(userId);
        } catch (Exception ex) {
            log.error(" 获取当前用户所有角色信息发生异常。", ex);
            return null;
        }
    }
	
    /**
     * 获取指定时间范围内更新的用户信息<br>
     * startTime YYYY-MM-dd HH:mm:ss
     * endTime YYYY-MM-dd HH:mm:ss
     */
    public List<LoginInfoDto> getUserListByTime(String startTime,String endTime) {
        if (ucClient == null) {
            log.warn("无法获取指定时间范围内更新的用户信息，可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        try {
            return ucClient.getUserListByTime(startTime,endTime);
        } catch (Exception ex) {
            log.error("通过机构编码获取下级机构信息发生异常。", ex);
            return null;
        }
    }
}         
