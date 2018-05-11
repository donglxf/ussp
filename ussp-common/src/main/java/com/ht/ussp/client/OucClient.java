package com.ht.ussp.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * @ClassName: OucClient
 * @Description: 调外部用户服务接口
 * @author wim qiuwenwu@hongte.info
 * @date 2018年5月11日 下午5:42:29
 */
@FeignClient("ussp-ouc-app")
public interface OucClient {
	@GetMapping(value = "/sms/saveSmsToRedis")
	Boolean saveSmsToRedis(@RequestParam("telephone") String telephone, @RequestParam("msgBody") String msgBody,@RequestParam("app") String app);

}
