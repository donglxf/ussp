package com.ht.ussp.uaa.app.resource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.common.SysStatus;
import com.ht.ussp.uaa.app.config.JwtSettings;
import com.ht.ussp.uaa.app.exception.JwtExpiredTokenException;
import com.ht.ussp.uaa.app.jwt.AccessJwtToken;
import com.ht.ussp.uaa.app.jwt.JwtTokenFactory;
import com.ht.ussp.uaa.app.jwt.RawAccessJwtToken;
import com.ht.ussp.uaa.app.jwt.TokenExtractor;
import com.ht.ussp.uaa.app.model.ResponseModal;
import com.ht.ussp.uaa.app.vo.ValidateJwtVo;

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
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenFactory.class);
	@Autowired
	@Qualifier("jwtHeaderTokenExtractor")
	private TokenExtractor tokenExtractor;
	@Autowired
	private JwtSettings jwtSettings;

//	@Autowired
//	public ExternalTokenManager(JwtSettings settings) {
//		this.settings = settings;
//	}

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
		logger.info(app + "'s token has created:" + token);
		return new AccessJwtToken(token, claims);

	}
	
	

	@RequestMapping(value = "/validateAppJwt")
	public ResponseModal validateAppJwt(@RequestParam("tokenPayload") String tokenPayload,@RequestParam("app") String app,HttpServletResponse response){
		ResponseModal rm=new ResponseModal();
		Jws<Claims> jwsClaims;
		
		try {
		RawAccessJwtToken AccessToken = new RawAccessJwtToken(tokenExtractor.extract(tokenPayload));
		
		jwsClaims = AccessToken.parseClaims(jwtSettings.getTokenSigningKey());
		String parseApp = jwsClaims.getBody().get("app").toString();
		if(!app.equals(parseApp)) {
			rm.setSysStatus(SysStatus.TOKEN_IS_VALID);
			return rm;
		}
		 rm.setSysStatus(SysStatus.SUCCESS);
		}catch(BadCredentialsException ex) {
			rm.setSysStatus(SysStatus.TOKEN_IS_VALID);
			log.info("----token invalid----");
		}catch (JwtExpiredTokenException expiredEx) {
			rm.setSysStatus(SysStatus.TOKEN_IS_EXPIRED);
			log.info("----token expired----");
        }
		return rm;
	}
}
