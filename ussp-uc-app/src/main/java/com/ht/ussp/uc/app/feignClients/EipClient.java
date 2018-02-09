package com.ht.ussp.uc.app.feignClients;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.vo.EmailVo;

/**
 * 外联平台
 */
@FeignClient(value = "eip-out")
public interface EipClient {

	/**
	 * 调用外联平台发送邮件
	 * 
	 * @param emailVo
	 * @return
	 */
	@PostMapping(value = "/eip/common/sendEmail", headers = { "app=UC", "content-type=application/json" })
	public Result<EmailVo> sendEmail(@RequestBody EmailVo emailVo);

}
