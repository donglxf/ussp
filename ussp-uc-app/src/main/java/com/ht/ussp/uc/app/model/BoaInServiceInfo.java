package com.ht.ussp.uc.app.model;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "BoaInService", description = "微服务信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoaInServiceInfo {

	@ApiModelProperty(value = "id", dataType = "long")
	long id;

	@ApiModelProperty(value = "微服务code", dataType = "string")
	String serviceCode;
	
	@ApiModelProperty(value = "微服务", dataType = "string")
	String applicationService;

	@ApiModelProperty(value = "微服务名称", dataType = "string")
	String applicationServiceName;

	@ApiModelProperty(value = "所属系统", dataType = "string")
	String app;

	// 状态（0正常 1禁用，也将禁用所有调用的权限 2删除）
	@ApiModelProperty(value = "状态", dataType = "string")
	String status;

	@ApiModelProperty(value = "创建人", dataType = "string")
	String createOperator;

	@ApiModelProperty(value = "创建时间", dataType = "date")
	Date createdDatetime;

	@ApiModelProperty(value = "更新人", dataType = "date")
	String updateOperator;

	@ApiModelProperty(value = "最后修改时间", dataType = "string")
	Date updateDatetime;

	@ApiModelProperty(value = "授权编码", dataType = "string")
	String authServiceCode;

	@ApiModelProperty(value = "可调用的微服务code", dataType = "string")
	String callServiceCode;
	
	@ApiModelProperty(value = "可调用的微服务微服务", dataType = "string")
	String callApplicationService;

	@ApiModelProperty(value = "可调用的微服务名称", dataType = "string")
	String callApplicationServiceName;
	
	@ApiModelProperty(value = "可调用的微服务所属系统", dataType = "string")
	String callApp;

	@ApiModelProperty(value = "可调用的微服务api", dataType = "string")
	String apiContent;

	@ApiModelProperty(value = "可调用的微服务api描述", dataType = "string")
	String apiDesc;

	// 微服务授权信息
	public BoaInServiceInfo(long id, String app, String serviceCode, String applicationService, String applicationServiceName, String authServiceCode,String callServiceCode,String callApplicationService,String callApplicationServiceName,
			 String status, String createOperator, Date createdDatetime, String updateOperator, Date updateDatetime,String callApp) {
		this.id = id;
		this.serviceCode = serviceCode;
		this.applicationService = applicationService;
		this.app = app;
		this.status = status;
		this.createOperator = createOperator;
		this.createdDatetime = createdDatetime;
		this.updateOperator = updateOperator;
		this.updateDatetime = updateDatetime;
		this.authServiceCode = authServiceCode;
		this.callServiceCode = callServiceCode;
		this.applicationServiceName = applicationServiceName;
		this.callApplicationService = callApplicationService;
		this.callApplicationServiceName = callApplicationServiceName;
		this.callApp = callApp;
	}

	// 微服务API授权信息
	public BoaInServiceInfo(long id,  String app, String serviceCode, String applicationService, String applicationServiceName, String authServiceCode,String callService, String callApplicationService,String callApplicationServiceName, 
			String status, String createOperator, Date createdDatetime, String updateOperator, Date updateDatetime,  String apiContent, String apiDesc) {
		this.id = id;
		this.serviceCode = serviceCode;
		this.applicationService = applicationService;
		this.app = app;
		this.status = status;
		this.createOperator = createOperator;
		this.createdDatetime = createdDatetime;
		this.updateOperator = updateOperator;
		this.updateDatetime = updateDatetime;
		this.authServiceCode = authServiceCode;
		this.applicationServiceName = applicationServiceName;
		this.apiContent = apiContent;
		this.apiDesc = apiDesc;
		this.callApplicationService = callApplicationService;
		this.callApplicationServiceName = callApplicationServiceName;
	}
	
}
