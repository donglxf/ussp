package com.ht.ussp.gateway.app.feignClients;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ht.ussp.gateway.app.model.ResponseModal;

/**
 * 
 * @ClassName: RoleClient
 * @Description: 调用角色与权限相关资口
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月15日 上午9:43:38
 */
/**
 * 
 * @ClassName: UaaClient
 * @Description: 调用UAA相关接口
 * @author wim qiuwenwu@hongte.info
 * @date 2018年3月12日 下午3:46:03
 */

@FeignClient(value = "ussp-uaa-app")
public interface UaaClient {

	/**
	 * 
	 * @Title: saveResources 
	 * @Description: 内部API jwt验证 
	 * @return ResponseModal  userId & orgCode
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年3月12日 下午3:50:31
	 */
	@RequestMapping(value = "/validateJwt")
	public ResponseModal validateJwt(@RequestParam("tokenPayload") String tokenPayload);
	
	/**
	 * 
	 * @Title: validateAppJwt 
	 * @Description: 系统级JWT验证 
	 * @return ResponseModal
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年3月21日 下午1:38:58
	 */
	@RequestMapping(value = "/external/validateAppJwt")
	public ResponseModal validateAppJwt(@RequestParam("tokenPayload") String tokenPayload,@RequestParam("app") String app);
	
}
