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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.ussp.bean.SmsHelper;
import com.ht.ussp.client.dto.MsgReqDtoIn;
import com.ht.ussp.common.SysStatus;
import com.ht.ussp.core.Result;
import com.ht.ussp.uaa.app.config.JwtSettings;
import com.ht.ussp.uaa.app.config.WebSecurityConfig;
import com.ht.ussp.uaa.app.exception.InvalidJwtToken;
import com.ht.ussp.uaa.app.exception.JwtExpiredTokenException;
import com.ht.ussp.uaa.app.feignClient.OutUserClient;
import com.ht.ussp.uaa.app.jwt.AccessJwtToken;
import com.ht.ussp.uaa.app.jwt.JwtToken;
import com.ht.ussp.uaa.app.jwt.RawAccessJwtToken;
import com.ht.ussp.uaa.app.jwt.RefreshToken;
import com.ht.ussp.uaa.app.jwt.TokenExtractor;
import com.ht.ussp.uaa.app.jwt.TokenVerifier;
import com.ht.ussp.uaa.app.model.ResponseModal;
import com.ht.ussp.uaa.app.vo.ValidateJwtVo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping(value = "/out")
public class ExternalUserResource {

	@Autowired
	private OutUserClient outUserClient;

	@Autowired
	private SmsHelper smsHelper;

	@Autowired
	private JwtSettings jwtSettings;

	@Autowired
	@Qualifier("jwtHeaderTokenExtractor")
	private TokenExtractor tokenExtractor;

	@Autowired
	private TokenVerifier tokenVerifier;

	@Autowired
	private ObjectMapper mapper;

	@PostMapping(value = "/ELogin")
	@ApiOperation(value = "外部用户认证")
	public Result ELogin(String app, String userName, String password, String smsCode, String type,
			HttpServletResponse response) {
		Map<String, String> map = new HashMap<>();
		if (StringUtils.isBlank(app)) {
			throw new IllegalArgumentException("Cannot create JWT Token without app");

		}

		if (StringUtils.isBlank(userName)) {
			throw new IllegalArgumentException("Cannot create JWT Token without userName");

		}

		if (StringUtils.isBlank(type)) {
			throw new IllegalArgumentException("Cannot create JWT Token without type");

		}
		// 手机号和验证码登录
		if ("sms".equals(type)) {
			Boolean flag = smsHelper.validateSmsCode(userName, smsCode, app);
			if (flag == false) {
				Result.buildFail("9929", "短信验证码验证失败！");
			}
			log.info("---------flag-------" + flag);
		}

		// 用户名密码方式登录
		if ("normal".equals(type)) {
			if (StringUtils.isBlank(password)) {
				throw new IllegalArgumentException("Cannot create JWT Token without password");
			}

			Boolean flag = smsHelper.validateSmsCode(userName, smsCode, app);
			if (flag == false) {
				Result.buildFail("9929", "短信验证码验证失败！");
			}
			log.info("---------flag-------" + flag);
		}

		Result result = outUserClient.validateUser(app, userName, password, type);
		if ("0000".equals(result.getReturnCode())) {
			String userId = result.getData().toString();

			JwtToken accessToken=createAccessJwtToken(userId);
			JwtToken refreshToken = OutRefreshToken(userId);
			
			map.put("accessToken", accessToken.getToken());
			map.put("refreshToken", refreshToken.getToken());
		} else {
			map.put("code", result.getReturnCode());
			map.put("CodeDesc", result.getCodeDesc());
		}
		return Result.buildSuccess(map);
	}

	/**
	 * 
	 * @Title: createAccessJwtToken 
	 * @Description: 创建accessToken 
	 * @return AccessJwtToken
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年5月15日 下午5:01:10
	 */
	public AccessJwtToken createAccessJwtToken(String userId) {
		if (StringUtils.isBlank(userId)) {
			throw new IllegalArgumentException("Cannot create JWT Token without userId");
		}
		LocalDateTime currentTime = LocalDateTime.now();
		Claims claims = Jwts.claims().setSubject("out user jwt token");
		claims.put("userId", userId);
		String accessToken = Jwts.builder().setClaims(claims).setIssuer(jwtSettings.getTokenIssuer())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime.plusMinutes(jwtSettings.getOutUserTokenTime())
						.atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS512, jwtSettings.getTokenSigningKey()).compact();

		log.info("out user jwt has created:" + accessToken);
		return new AccessJwtToken(accessToken, claims);
	}

	/**
	 * 
	 * @Title: createRefreshToken 
	 * @Description: 创建外部用户刷新令牌 
	 * @return JwtToken
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年5月15日 上午11:32:21
	 */
	public JwtToken OutRefreshToken(String userId) {
		if (StringUtils.isBlank(userId)) {
			throw new IllegalArgumentException("Cannot create refreshToken without userId");
		}

		LocalDateTime currentTime = LocalDateTime.now();

		Claims claims = Jwts.claims().setSubject("Out User refresh token");
		claims.put("userId", userId);
		String refreshToken = Jwts.builder().setClaims(claims).setIssuer(jwtSettings.getTokenIssuer())
				.setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime.plusMinutes(jwtSettings.getOutRefreshTokenExpTime())
						.atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS512, jwtSettings.getTokenSigningKey()).compact();
		log.info("Out user refreshToken has created:" + refreshToken);
		return new AccessJwtToken(refreshToken, claims);
	}

	/**
	 * 
	 * @Title: refreshToken 
	 * @Description: 通过refreshToken获取accessToken---外部用户,当外部用户登录超时，或者换地址登录时使用refreshToken来获得accessToken
	 * @return JwtToken
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年5月15日 下午2:47:07
	 */
	@ApiOperation(value = "通过refreshToken获取accesstoken")
	@GetMapping(value = "/getAccessToken", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody JwtToken refreshToken(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setCharacterEncoding("UTF-8");
		RefreshToken refreshToken = null;
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

		return createAccessJwtToken(refreshToken.getUserId());
	}

	
	/**
	 * 
	 * @Title: validateOutUserJwt 
	 * @Description: 验证外部用户AccessToken 
	 * @return ResponseModal
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年5月15日 下午5:33:05
	 */
	@ApiOperation(value = "验证外部用户AccessToken")
	@PostMapping(value = "/validateOutUserJwt")
	public ResponseModal validateJwt(@RequestParam("tokenPayload") String tokenPayload,HttpServletResponse response){
		response.setCharacterEncoding("UTF-8");
		ResponseModal rm=new ResponseModal();
		Jws<Claims> jwsClaims;
		String userId;
		ValidateJwtVo vdj = new ValidateJwtVo();
		
		try {
		RawAccessJwtToken accessToken = new RawAccessJwtToken(tokenExtractor.extract(tokenPayload));
		
		jwsClaims = accessToken.parseClaims(jwtSettings.getTokenSigningKey());
		 userId = jwsClaims.getBody().get("userId").toString();
		 vdj.setUserId(userId);
		 rm.setSysStatus(SysStatus.SUCCESS);
		 rm.setResult(vdj);
		}catch(BadCredentialsException ex) {
			rm.setSysStatus(SysStatus.TOKEN_IS_VALID);
			log.info("----token invalid----");
		}catch (JwtExpiredTokenException expiredEx) {
			rm.setSysStatus(SysStatus.TOKEN_IS_EXPIRED);
			log.info("----token expired----");
        }catch (AuthenticationServiceException asEx) {
			rm.setSysStatus(SysStatus.ERROR_PARAM);
			log.info("----invalid token's header----");
        }
		return rm;
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
	public Boolean TestValidateSms(@RequestParam("telephone") String telephone, @RequestParam("code") String code,
			@RequestParam("app") String app) {

		Boolean flag = smsHelper.validateSmsCode(telephone, code, app);
		return flag;
	}

}
