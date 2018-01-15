package com.ht.ussp.gateway.app.jwt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.ht.ussp.gateway.app.config.JwtSettings;
import com.ht.ussp.gateway.app.vo.UserVo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 
 * @ClassName: JwtTokenFactory
 * @Description: 分离TOKEN创建逻辑
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月8日 上午10:05:04
 */
@Component
public class JwtTokenFactory {
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenFactory.class);
	private final JwtSettings settings;

	@Autowired
	public JwtTokenFactory(JwtSettings settings) {
		this.settings = settings;
	}

	/**
	 * 
	 * @Title: createAccessJwtToken 
	 * @Description: 生成TOKEN 
	 * @return AccessJwtToken
	 * @throws
	 */
	public AccessJwtToken createAccessJwtToken(UserVo userVo) {
		if (StringUtils.isBlank(userVo.getUserId())) {
			throw new IllegalArgumentException("Cannot create JWT Token without userId");
		}
		Claims claims = Jwts.claims().setSubject("User Authorize");
		claims.put("userId", userVo.getUserId());
		claims.put("controller", userVo.getController());
//		if(("N").equals(userVo.getController())&&list.size()>0) {
//			claims.put("roles", list);
//		}
		
		LocalDateTime currentTime = LocalDateTime.now();

		String token = Jwts.builder().setClaims(claims).setIssuer(settings.getTokenIssuer())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime.plusMinutes(settings.getTokenExpirationTime())
						.atZone(ZoneId.systemDefault()).toInstant()))
				.setAudience(userVo.getUserName()).signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
				.compact();
		logger.info("jwt has created:" + token);
		return new AccessJwtToken(token, claims);
	}

	/**
	 * 
	  * @Title: createRefreshToken 
	  * @Description: 生成REFRESH TOKEN 
	  * @return JwtToken
	  * @throws
	 */
	public JwtToken createRefreshToken(UserVo userVo) {
		if (StringUtils.isBlank(userVo.getUserId())) {
			throw new IllegalArgumentException("Cannot create refreshToken without userId");
		}

		LocalDateTime currentTime = LocalDateTime.now();

		Claims claims = Jwts.claims().setSubject("User refresh token");
		claims.put("userId", userVo.getUserId());
		claims.put("controller", userVo.getController());
		String token = Jwts.builder().setClaims(claims).setIssuer(settings.getTokenIssuer())
				.setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime.plusMinutes(settings.getRefreshTokenExpTime())
						.atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey()).compact();
		logger.info("refreshToken has created:" + token);
		return new AccessJwtToken(token, claims);
	}
}
