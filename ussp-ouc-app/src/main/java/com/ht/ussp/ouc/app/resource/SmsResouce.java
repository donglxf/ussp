package com.ht.ussp.ouc.app.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.bean.SmsHelper;
import com.ht.ussp.client.dto.MsgReqDtoIn;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping(value = "/sms")
public class SmsResouce {
	
	@Autowired
	protected RedisTemplate<String, String> redis;
	
	@Autowired
	private SmsHelper smsHelper;
	
	@Value("${smsTime}")
	private int smsTime;
	
	@ApiOperation(value = "保存短信至REDIS")
	@GetMapping("/saveSmsToRedis")
	public Boolean saveSmsToRedis(@RequestParam("telephone") String telephone, @RequestParam("msgBody") String msgBody,
			@RequestParam("app") String app) {
		Boolean flag = false;
		try {
			redis.opsForValue().set(app + ":" + telephone, msgBody, smsTime, TimeUnit.SECONDS);
		} catch (Exception e) {
			log.error("----保存短信至redis异常：" + e);
			flag = false;
		}
		flag = true;
		return flag;
	}

	@ApiOperation(value = "验证短信验证码")
	@GetMapping("/validateSmsCode")
	public Boolean validateSms(@RequestParam("telephone") String telephone, @RequestParam("smsCode") String smsCode,
			@RequestParam("app") String app) {
		Boolean flag = false;

		try {
			String code = redis.opsForValue().get(app + ":" + telephone);
			if (smsCode.equals(code)) {
				flag = true;
			} else {
				flag = false;
				log.error("-----验证码不正确----");
			}
		} catch (Exception e) {
			log.error("----验证短信失败：" + e);
			flag = false;
		}
		flag = true;
		return flag;
	}

	@ApiOperation(value = "测试发送短信")
	@PostMapping("/TestSendSms")
	public Boolean TestSendSms(@RequestParam("telephone") String telephone, @RequestParam("app") String app) {
		Map<String, String> map = new HashMap<String, String>();
		MsgReqDtoIn msgReqDtoIn = new MsgReqDtoIn();
		String RandomStr = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
		map.put("code", RandomStr);
		msgReqDtoIn.setMsgBody(map);
		msgReqDtoIn.setApp(app);
		msgReqDtoIn.setMsgTo(telephone);
		msgReqDtoIn.setMsgModelId(993748227645890562l);
		smsHelper.sendMsg(msgReqDtoIn);
		return true;
	}
}
