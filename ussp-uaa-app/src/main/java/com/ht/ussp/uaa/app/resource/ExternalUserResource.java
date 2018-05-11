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
import org.springframework.web.bind.annotation.RestController;

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
}
