package com.ht.ussp.ouc.app.resource;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.bean.SmsHelper;
import com.ht.ussp.common.SysStatus;
import com.ht.ussp.core.Result;
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
	public Result validateSms(@RequestParam("telephone") String telephone, @RequestParam("code") String code,
			@RequestParam("app") String app) {
		try {
			String result = redis.opsForValue().get(app + ":" + telephone);
			if(StringUtils.isBlank(result)) {
				return Result.buildFail(SysStatus.SMS_CODE_VALID.getStatus(), SysStatus.SMS_CODE_VALID.getMsg());				
			}else {
				Map<String, Object> map = JsonUtil.json2Map(result);
				String redisCode = map.get("code").toString();
				if(redisCode.equals(code)) {
					return Result.buildSuccess();
				}else {
				return Result.buildFail(SysStatus.SMS_CODE_FAIL.getStatus(), SysStatus.SMS_CODE_FAIL.getMsg());}
			} 
		} catch (Exception e) {
			log.error("连接----redis异常-----：" + e);
			return Result.buildFail();	
		}
		
	}

}
