package com.ht.ussp.uaa.app.resource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.ussp.common.SysStatus;
import com.ht.ussp.uaa.app.config.JwtSettings;
import com.ht.ussp.uaa.app.config.WebSecurityConfig;
import com.ht.ussp.uaa.app.exception.InvalidJwtToken;
import com.ht.ussp.uaa.app.exception.JwtExpiredTokenException;
import com.ht.ussp.uaa.app.jwt.AccessJwtToken;
import com.ht.ussp.uaa.app.jwt.JwtToken;
import com.ht.ussp.uaa.app.jwt.RawAccessJwtToken;
import com.ht.ussp.uaa.app.jwt.RefreshToken;
import com.ht.ussp.uaa.app.jwt.TokenExtractor;
import com.ht.ussp.uaa.app.jwt.TokenVerifier;
import com.ht.ussp.uaa.app.model.ResponseModal;
import com.ht.ussp.uaa.app.vo.UserVo;

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
	@Autowired
	private TokenVerifier tokenVerifier;

	/**
	 * 
	 * @Title: createToken 
	 * @Description: 创建系统级token 
	 * @return AccessJwtToken
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年4月10日 下午1:14:14
	 */
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

	/**
	 * 
	 * @Title: validateAppJwt 
	 * @Description: 验证系统级token 
	 * @return ResponseModal
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年4月10日 下午1:14:32
	 */
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
		} catch (Exception ex) {
			log.info("validateAppjwt exception....." + ex);
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
	public ResponseModal createUCToken(String userId, String bmUserId, Integer tokenTime, Integer refreshTime) {
		ResponseModal rm = new ResponseModal();
		if (StringUtils.isBlank(userId)) {
			throw new IllegalArgumentException("Cannot create JWT Token without userId");

		} else if (StringUtils.isBlank(bmUserId)) {
			throw new IllegalArgumentException("Cannot create JWT Token without bmUserId");

		} else if (tokenTime.intValue() <= 0) {
			throw new IllegalArgumentException("tokenTime must greater than 0");

		} else if (refreshTime.intValue() <= 0) {
			throw new IllegalArgumentException("refreshtime must greater than 0");

		}

		Map<String, String> tokenMap = new HashMap<String, String>();
		LocalDateTime currentTime = LocalDateTime.now();

		Claims claims = Jwts.claims().setSubject("UC jwt token");
		claims.put("userId", userId);
		claims.put("bmUserId", bmUserId);
//		claims.put("refreshtime", refreshTime);
		String token = Jwts.builder().setClaims(claims).setIssuer(jwtSettings.getTokenIssuer())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime.plusMinutes(tokenTime).atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS512, jwtSettings.getTokenSigningKey()).compact();

		String refreshToken = Jwts.builder().setClaims(claims).setIssuer(jwtSettings.getTokenIssuer())
				.setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(
						Date.from(currentTime.plusMinutes(refreshTime).atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS512, jwtSettings.getTokenSigningKey()).compact();
		log.info("uc's token and refreshToken has created sucessful");
		tokenMap.put("token", token);
		tokenMap.put("refreshToken", refreshToken);
		rm.setSysStatus(SysStatus.SUCCESS);
		rm.setResult(tokenMap);
		return rm;
	}

	/**
	 * 
	 * @Title: refreshToken 
	 * @Description: 给UC使用，与信贷系统有关，使用token获取refreshToken  
	 * @return JwtToken
	 * @throws  String userId,String bmUserId,Integer tokenTime,Integer refreshTime
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年4月10日 下午1:16:30
	 */
	@RequestMapping(value = "/auth/UCToken", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody ResponseModal refreshToken(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		ResponseModal rm = new ResponseModal();
		Map<String, String> tokenMap = new HashMap<String, String>();
		RefreshToken refreshToken = null;
		Integer tokenTime=Integer.valueOf(request.getHeader("tokenTime"));
		try {
			String tokenPayload = tokenExtractor
					.extract(request.getHeader(WebSecurityConfig.AUTHENTICATION_HEADER_NAME));

			RawAccessJwtToken rawToken = new RawAccessJwtToken(tokenPayload);

			refreshToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey())
					.orElseThrow(() -> new InvalidJwtToken());
		} catch (BadCredentialsException ex) {
			mapper.writeValue(response.getWriter(), new ResponseModal(SysStatus.TOKEN_IS_VALID));
			return null;
		} catch (JwtExpiredTokenException expiredEx) {
			mapper.writeValue(response.getWriter(), new ResponseModal(SysStatus.TOKEN_IS_EXPIRED));
			return null;
		} catch (AuthenticationServiceException ex) {
			mapper.writeValue(response.getWriter(), new ResponseModal(SysStatus.HEADER_CANNOT_NULL));
			return null;
		}

		String jti = refreshToken.getJti();
		if (!tokenVerifier.verify(jti)) {
			throw new InvalidJwtToken();
		}

		String userId = refreshToken.getUserId();
		String bmUserId = refreshToken.getBmUserId();

		Claims claims = Jwts.claims().setSubject("UC jwt token");
		claims.put("userId", userId);
		claims.put("bmUserId", bmUserId);
		LocalDateTime currentTime = LocalDateTime.now();
		String token = Jwts.builder().setClaims(claims).setIssuer(jwtSettings.getTokenIssuer())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime.plusMinutes(tokenTime).atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS512, jwtSettings.getTokenSigningKey()).compact();

		tokenMap.put("token", token);

		rm.setSysStatus(SysStatus.SUCCESS);
		rm.setResult(tokenMap);
		return rm;
	}

}
