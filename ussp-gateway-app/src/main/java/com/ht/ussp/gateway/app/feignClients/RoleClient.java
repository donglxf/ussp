package com.ht.ussp.gateway.app.feignClients;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * @ClassName: RoleClient
 * @Description: 调用角色与权限相关资口
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月15日 上午9:43:38
 */

@FeignClient(value = "ussp-uc-app")
public interface RoleClient {


	/**
	 * 
	 * @Title: IsHasAuth 
	 * @Description: 验证资源权限，TRUE：通过    FALSE：不通过 
	 * @return Boolean
	 * @throws
	 */
	@RequestMapping(value = "/auth/IsHasAuth")
	public Boolean isHasAuth(@RequestParam("key") String key, @RequestParam("url") String url);
	
	/**
	 * 
	 * @Title: isOS 
	 * @Description: 判断是否为外部系统 
	 * @return Boolean
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年5月15日 上午11:15:20
	 */
	@RequestMapping(value = "/apps/isOS")
	public Boolean isOS(@RequestParam("app") String app);
	
}
