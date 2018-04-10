package com.ht.ussp.uaa.app.resource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.ussp.common.SysStatus;
import com.ht.ussp.uaa.app.config.JwtSettings;
import com.ht.ussp.uaa.app.exception.JwtExpiredTokenException;
import com.ht.ussp.uaa.app.jwt.AccessJwtToken;
import com.ht.ussp.uaa.app.jwt.RawAccessJwtToken;
import com.ht.ussp.uaa.app.jwt.TokenExtractor;
import com.ht.ussp.uaa.app.model.ResponseModal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

/**
 * 
 * @ClassName: ExternalTokenManager
 * @Description: 外部系统TOKEN管理
 * @author wim qiuwenwu@hongte.info
 * @date 2018年3月21日 上午8:39:05
 */
@RestController
@RequestMapping(value = "/external")
@Log4j2
public class ExternalTokenManager {
	@Autowired
	@Qualifier("jwtHeaderTokenExtractor")
	private TokenExtractor tokenExtractor;
	@Autowired
	private JwtSettings jwtSettings;
	@Autowired
	private ObjectMapper mapper;
	
	@PostMapping(value = "/createAppToken")
	@ApiOperation(value = "创建系统级别token")
	public AccessJwtToken createToken(String app) {

		if (StringUtils.isBlank(app)) {
			throw new IllegalArgumentException("Cannot create JWT Token without app");

		}
		LocalDateTime currentTime = LocalDateTime.now();

		Claims claims = Jwts.claims().setSubject("app jwt token");
		claims.put("app", app);
		String token = Jwts.builder().setClaims(claims).setIssuer(jwtSettings.getTokenIssuer())
				.setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(
						currentTime.plusDays(jwtSettings.getAppTokenTime()).atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS512, jwtSettings.getTokenSigningKey()).compact();
		log.info(app + "'s token has created:" + token);
		return new AccessJwtToken(token, claims);

	}

	@RequestMapping(value = "/validateAppJwt")
	public ResponseModal validateAppJwt(@RequestParam("tokenPayload") String tokenPayload,
			@RequestParam("app") String app, HttpServletResponse response) {
		ResponseModal rm = new ResponseModal();
		Jws<Claims> jwsClaims;
		try {
			RawAccessJwtToken accessToken = new RawAccessJwtToken(tokenExtractor.extract(tokenPayload));
			jwsClaims = accessToken.parseClaims(jwtSettings.getTokenSigningKey());
			String parseApp = jwsClaims.getBody().get("app").toString();
			if (!app.equals(parseApp)) {
				rm.setSysStatus(SysStatus.TOKEN_IS_VALID);
				return rm;
			}
			rm.setSysStatus(SysStatus.SUCCESS);
		} catch (BadCredentialsException | NullPointerException ex) {
			rm.setSysStatus(SysStatus.TOKEN_IS_VALID);
			log.info("----token invalid----");
		} catch (JwtExpiredTokenException expiredEx) {
			rm.setSysStatus(SysStatus.TOKEN_IS_EXPIRED);
			log.info("----token expired----");
		}catch (Exception ex) {
			log.info("validateAppjwt exception....."+ex);
		}
		return rm;
	}
	
	
	/**
	 * 
	 * @Title: createUCToken 
	 * @Description: 给UC使用，与信贷系统有关，创建token和refreshToken 
	 * @return AccessJwtToken
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年4月10日 上午9:47:18
	 */
	@PostMapping(value = "/createUCToken")
	@ApiOperation(value = "创建UCToken")
	public void createUCToken(String userId,String bmUserId,Integer tokenTime,Integer refreshtime,HttpServletResponse response) {

		if (StringUtils.isBlank(userId)) {
			throw new IllegalArgumentException("Cannot create JWT Token without userId");

		}else if(StringUtils.isBlank(bmUserId)) {
			throw new IllegalArgumentException("Cannot create JWT Token without bmUserId");

		}else if (refreshtime.intValue()<=0) {
			throw new IllegalArgumentException("refreshtime must greater than 0");

		}
		
		Map<String, String> tokenMap = new HashMap<String, String>();
		LocalDateTime currentTime = LocalDateTime.now();

		Claims claims = Jwts.claims().setSubject("UC jwt token");
		claims.put("userId", userId);
		claims.put("bmUserId", bmUserId);
		claims.put("refreshtime", refreshtime);
		String token = Jwts.builder().setClaims(claims).setIssuer(jwtSettings.getTokenIssuer())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime.plusMinutes(tokenTime)
						.atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS512, jwtSettings.getTokenSigningKey())
				.compact();
		
		String refreshToken = Jwts.builder().setClaims(claims).setIssuer(jwtSettings.getTokenIssuer())
				.setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime.plusMinutes(refreshtime)
						.atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS512, jwtSettings.getTokenSigningKey()).compact();
		log.info("uc's token and refreshToken has created sucessful");
		tokenMap.put("code", SysStatus.SUCCESS.getStatus());
		tokenMap.put("token", token);
		tokenMap.put("refreshToken", refreshToken);
		
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		try {
			mapper.writeValue(response.getWriter(), tokenMap);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	
}
