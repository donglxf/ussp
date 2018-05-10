package com.ht.ussp.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ht.ussp.client.dto.MsgReqDtoIn;
import com.ht.ussp.client.dto.MsgResDtoOut;
import com.ht.ussp.core.Result;

/**
 * 
 * @ClassName: SmsClient
 * @Description: 发送短信接口
 * @author wim qiuwenwu@hongte.info
 * @date 2018年5月8日 下午3:03:30
 */

@FeignClient("msg-send-app")
public interface SmsClient {
	/**
	 * 
	 * @Title: resourceApiAynch 
	 * @Description: 发送短信验证码接口 
	 * @return Map<String,Integer>
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年5月8日 下午3:08:35
	 */
	@PostMapping("/sendRequest/sendRequest")
	public Result<MsgResDtoOut> sendMsg(@RequestBody MsgReqDtoIn msgReqDtoIn);
}
