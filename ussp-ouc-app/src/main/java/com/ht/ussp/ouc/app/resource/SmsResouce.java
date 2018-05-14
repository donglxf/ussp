package com.ht.ussp.ouc.app.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.ht.ussp.bean.SmsHelper;
import com.ht.ussp.util.JsonUtil;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;

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
	public Boolean saveSmsToRedis(@RequestParam("telephone") String telephone, @RequestParam("msgBody") Object msgBody,
			@RequestParam("app") String app) throws IllegalAccessException {
		JSONObject json = JSONObject.fromObject(msgBody);
		Boolean flag = false;
		try {
			redis.opsForValue().set(app + ":" + telephone, json.toString(), smsTime, TimeUnit.SECONDS);

		} catch (Exception e) {
			log.error("----保存短信至redis异常：" + e);
			flag = false;
		}
		flag = true;
		return flag;
	}

	@ApiOperation(value = "验证短信验证码")
	@GetMapping("/validateSmsCode")
	public Boolean validateSms(@RequestParam("telephone") String telephone, @RequestParam("code") String code,
			@RequestParam("app") String app) {
		Boolean flag = false;

		try {
			String result = redis.opsForValue().get(app + ":" + telephone);
			Map<String, Object> map = JsonUtil.json2Map(result);
			String redisCode = map.get("code").toString();
			if (code.equals(redisCode)) {
				flag = true;
			} else {
				flag = false;
				return flag;
			}
		} catch (Exception e) {
			log.error("----验证短信失败：" + e);
			flag = false;
		}
		return flag;
	}

}
