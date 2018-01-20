package com.ht.ussp.gateway.app.feignClients;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ht.ussp.gateway.app.model.ResponseModal;
import com.ht.ussp.gateway.app.vo.UserVo;

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
	 * @Title: saveResources 
	 * @Description: 调用该接口，查找角色对应资源，并将其保存到REDIS  
	 * @return ResponseModal
	 * @throws
	 */
	@RequestMapping(value = "/auth/saveResources")
	public ResponseModal saveResources(UserVo userVo, @RequestParam("roleCodes") List<String> roleCodes);

	/**
	 * 
	 * @Title: IsHasAuth 
	 * @Description: 验证资源权限，TRUE：通过    FALSE：不通过 
	 * @return Boolean
	 * @throws
	 */
	@RequestMapping(value = "/auth/IsHasAuth")
	public Boolean IsHasAuth(@RequestParam("key") String key, @RequestParam("url") String url);
}
