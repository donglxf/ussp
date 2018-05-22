/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: AppAndAuthVo.java
 * Author:   谭荣巧
 * Date:     2018/1/17 14:21
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统和微服务组合树<br>
 * <br>
 *
 */
@Data
@NoArgsConstructor
public class AppAndServiceVo {
    
	private String serviceCode;
    private String applicationServiceName;
    private String applicationService;
    private String parentCode;
    private String app;
    
	public AppAndServiceVo(Object serviceCode, Object applicationServiceName, Object applicationService, Object parentApp, Object app) {
		this.serviceCode = serviceCode == null ? null : serviceCode.toString();
		this.applicationServiceName = applicationServiceName == null ? null : applicationServiceName.toString();
		this.applicationService = applicationService == null ? null : applicationService.toString();
		this.parentCode = parentApp == null ? null : parentApp.toString();
		this.app = app == null ? null : app.toString();
	}
}
