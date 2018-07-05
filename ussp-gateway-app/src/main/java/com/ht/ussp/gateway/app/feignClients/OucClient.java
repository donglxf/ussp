package com.ht.ussp.gateway.app.feignClients;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ht.ussp.core.Result;

@FeignClient("ussp-ouc-app")
public interface OucClient {
	
	/**
	 * 
	 * @Title: validateToken 
	 * @Description: 验证token,供鸿特金服APP使用 
	 * @return Result
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年7月2日 下午8:38:21
	 */
	@GetMapping(value = "/user/validateToken")
	Result validateToken(@RequestParam("app") String app,@RequestParam("userId") String userId, @RequestParam("token") String token);
}
