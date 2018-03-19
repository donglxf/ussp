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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import com.ht.ussp.client.UCClient;
import com.ht.ussp.core.Result;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * 菜单管理
 * @author tangxs
 *
 */
@Log4j2
@ControllerAdvice
public class MenuInfoHelper {
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
	 * 获取菜单 resType view菜单   菜单组group
	 * @return
	 */
	public Result getMenuList(String resType,String apps) {
		if (ucClient == null) {
			log.warn("无法获取菜单，可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
		}
		try {
			apps = StringUtils.isEmpty(apps)?app:apps;
			return ucClient.getMenus(app,resType);
		} catch (Exception ex) {
			log.error("获取菜单发生异常。", ex);
			return Result.buildFail(ex.toString(), ex.getMessage());
		}
	}
	
	/**
	 * 添加菜单  (菜单格式：系统编码_M_父菜单序号_子菜单序号   ex:父菜单-UC_M02   子菜单-UC_M0205)
	 * @param resNameCn
	 * @param resContent
	 * @param fontIcon
	 * @param resParent
	 * @param resParentName
	 * @param roles
	 *  @param apps 
	 * @return 菜单编码
	 */
	public Result<String> addMenu(String resNameCn, String resContent, String fontIcon, String resParent, String resParentName,
			String[] roles,String apps) {
		if (ucClient == null) {
			log.warn("无法添加菜单，可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
		}
		try {
			apps = StringUtils.isEmpty(apps)?app:apps;
			userId = StringUtils.isEmpty(userId)?"":userId;
			return ucClient.addMenu(resNameCn, resContent, fontIcon, resParent, resParentName, roles, userId,apps);
		} catch (Exception ex) {
			log.error("添加菜单发生异常。", ex);
			return Result.buildFail(ex.toString(), ex.getMessage());
		}
	}
    
	/**
	 * 更新菜单
	 * （根据菜单编码更新菜单信息，菜单编码不可修改）
	 * @param resNameCn
	 * @param resContent
	 * @param fontIcon
	 * @param roles
	 * @return  returnCode:0000  更新成功
	 */
	public Result updateMenu(String resCode,String resNameCn, String resContent, String fontIcon, String[] roles,String apps) {
		if (ucClient == null) {
			log.warn("无法更新菜单，可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
		}
		try {
			apps = StringUtils.isEmpty(apps)?app:apps;
			userId = StringUtils.isEmpty(userId)?"":userId;
			return ucClient.updateMenu(resCode, resNameCn, resContent, fontIcon, roles, userId,   apps);
		} catch (Exception ex) {
			log.error("更新菜单发生异常。", ex);
			return Result.buildFail(ex.toString(), ex.getMessage());
		}
	}

	/**
	 * 禁用/启用菜单
	 * @param resCode
	 * @param status   1，禁用，0，启用
	 * @param apps
	 * @return returnCode:0000  状态更改成功
	 */
	public Result changeApiState(String resCode, String status,String apps) {
		if (ucClient == null) {
			log.warn("无法禁用/启用菜单，可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
		}
		try {
			apps = StringUtils.isEmpty(apps)?app:apps;
			userId = StringUtils.isEmpty(userId)?"":userId;
			ucClient.changeApiState(resCode, status, userId, apps);
			return Result.buildSuccess();
		} catch (Exception ex) {
			log.error("禁用/启用菜单发生异常。", ex);
			return Result.buildFail(ex.toString(), ex.getMessage());
		}
	}
     
}
