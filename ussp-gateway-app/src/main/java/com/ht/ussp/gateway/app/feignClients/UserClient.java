package com.ht.ussp.gateway.app.feignClients;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.ht.ussp.gateway.app.model.ResponseModal;

/**
 * 
 * @ClassName: UserClient
 * @Description: feign调用权限相关接口
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月8日 下午3:50:01
 */
@FeignClient(value = "ussp-uc-app")
public interface UserClient {

	/**
	 * 
	 * @Title: validateUser 
	 * @Description: 验证用户 
	 * @return ResponseModal
	 * @throws
	 */
	@RequestMapping(value = "/member/validateUser")
	public ResponseModal validateUser(String app,String userName);
	
	/**
	 * 
	 * @Title: getLoginInfo 
	 * @Description: 获取登录信息 
	 * @return ResponseModal
	 * @throws
	 */
	@RequestMapping(value = "/getLoginInfo")
	public ResponseModal getLoginInfo(String password);
	/**
	 * 
	 * @Title: getResources 
	 * @Description: 获取资源 
	 * @return ResponseModal
	 * @throws
	 */
	@RequestMapping(value = "/getResources")
	public ResponseModal getResources(JSONObject json);
	
	
	
}

