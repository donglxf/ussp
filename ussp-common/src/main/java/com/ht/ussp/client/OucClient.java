package com.ht.ussp.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ht.ussp.core.Result;

/**
 * 
 * @ClassName: OucClient
 * @Description: 调外部用户服务接口
 * @author wim qiuwenwu@hongte.info
 * @date 2018年5月11日 下午5:42:29
 */
@FeignClient("ussp-ouc-app")
public interface OucClient {
	/**
	 * 
	 * @Title: saveSmsToRedis 
	 * @Description: 发送短信 
	 * @return Boolean
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年5月14日 上午11:10:52
	 */
	@GetMapping(value = "/sms/saveSmsToRedis")
	Boolean saveSmsToRedis(@RequestParam("telephone") String telephone, @RequestParam("msgBody") Object msgBody,@RequestParam("app") String app);
	
	/**
	 * 
	 * @Title: validateSmsCode 
	 * @Description: 验证短信 
	 * @return Boolean
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年5月14日 上午11:11:10
	 */
	@GetMapping(value = "/sms/validateSmsCode")
	Result validateSmsCode(@RequestParam("telephone") String telephone, @RequestParam("code") String code,@RequestParam("app") String app);

}
