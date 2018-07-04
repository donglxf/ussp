package com.ht.ussp.uaa.app.feignClient;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ht.ussp.core.Result;

/**
 * 
 * @ClassName: OutUserClient
 * @Description: 调ouc服务接口
 * @author wim qiuwenwu@hongte.info
 * @date 2018年5月11日 下午2:33:22
 */

@FeignClient(value = "ussp-ouc-app")
public interface OutUserClient {
	@RequestMapping(value = "/user/validateUser")
	public Result validateUser(@RequestParam("app")String app,@RequestParam("userName")String userName,@RequestParam("password")String password,@RequestParam("type")String type);
	
	@RequestMapping(value = "/client/getClientInfo")
	public Result getClientInfo(@RequestParam("app")String app);
	
	@RequestMapping(value = "/user/saveTokenToRedis")
	public Boolean saveTokenToRedis(@RequestParam("userId") String userId, @RequestParam("token") String token);
	
	@RequestMapping(value = "/user/validateToken")
	public Result validateToken(@RequestParam("userId") String userId, @RequestParam("token") String token);
	
	
}
