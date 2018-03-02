package org.ussp.uaa.app.feignClient;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.ussp.uaa.app.model.ResponseModal;

/**
 * 
 * @ClassName: UserClient
 * @Description: feign调用用户相关接口
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
	@RequestMapping(value = "/user/validateUser")
	public ResponseModal validateUser(@RequestParam("app")String app,@RequestParam("userName")String userName);
	
}

