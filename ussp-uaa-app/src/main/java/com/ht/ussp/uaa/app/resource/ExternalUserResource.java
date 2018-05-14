package com.ht.ussp.uaa.app.resource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.bean.SmsHelper;
import com.ht.ussp.client.dto.MsgReqDtoIn;
import com.ht.ussp.core.Result;
import com.ht.ussp.uaa.app.config.JwtSettings;
import com.ht.ussp.uaa.app.feignClient.OutUserClient;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/out")
public class ExternalUserResource {
	
	@Autowired
	private OutUserClient outUserClient;
	
	@Autowired
	private SmsHelper smsHelper;
	
	@Autowired
	private JwtSettings jwtSettings;
	
	@PostMapping(value = "/ELogin")
	@ApiOperation(value = "创建系统级别token")
	public Result ELogin(String app,String userName,String password,String smsCode,String type,HttpServletResponse response) {
		Map<String,String> map=new HashMap<>();
		if (StringUtils.isBlank(app)) {
			throw new IllegalArgumentException("Cannot create JWT Token without app");

		}
		
		if (StringUtils.isBlank(userName)) {
			throw new IllegalArgumentException("Cannot create JWT Token without userName");

		}
		
		if (StringUtils.isBlank(type)) {
			throw new IllegalArgumentException("Cannot create JWT Token without type");

		}
		
		Result result=outUserClient.validateUser(app, userName, password, type);
		if("0000".equals(result.getReturnCode())){
			String userId=result.getData().toString();
			LocalDateTime currentTime = LocalDateTime.now();
			Claims claims = Jwts.claims().setSubject(app+" jwt token");
			claims.put("userId", userId);
			String token = Jwts.builder().setClaims(claims).setIssuer(jwtSettings.getTokenIssuer())
					.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
					.setExpiration(Date.from(currentTime.plusMinutes(jwtSettings.getOutUserTokenTime()).atZone(ZoneId.systemDefault()).toInstant()))
					.signWith(SignatureAlgorithm.HS512, jwtSettings.getTokenSigningKey()).compact();
			map.put("token", token);
			
		}else {
			map.put("code", result.getReturnCode());
			map.put("CodeDesc", result.getCodeDesc());
		}
		return Result.buildSuccess(map);
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
	
	@ApiOperation(value = "测试验证短信")
	@PostMapping("/validateCode")
	public Boolean TestValidateSms(@RequestParam("telephone") String telephone,@RequestParam("code") String code, @RequestParam("app") String app) {
		
		Boolean flag=smsHelper.validateSmsCode(telephone, code, app);
		return flag;
	}
	
}
