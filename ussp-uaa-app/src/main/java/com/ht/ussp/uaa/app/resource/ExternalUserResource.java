package com.ht.ussp.uaa.app.resource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.bean.EloginParam;
import com.ht.ussp.bean.SmsHelper;
import com.ht.ussp.client.dto.MsgReqDtoIn;
import com.ht.ussp.common.SysStatus;
import com.ht.ussp.core.Result;
import com.ht.ussp.uaa.app.config.JwtSettings;
import com.ht.ussp.uaa.app.exception.InvalidJwtToken;
import com.ht.ussp.uaa.app.exception.JwtExpiredTokenException;
import com.ht.ussp.uaa.app.feignClient.OutUserClient;
import com.ht.ussp.uaa.app.jwt.AccessJwtToken;
import com.ht.ussp.uaa.app.jwt.JwtToken;
import com.ht.ussp.uaa.app.jwt.RawAccessJwtToken;
import com.ht.ussp.uaa.app.jwt.RefreshToken;
import com.ht.ussp.uaa.app.jwt.TokenExtractor;
import com.ht.ussp.uaa.app.jwt.TokenVerifier;
import com.ht.ussp.uaa.app.vo.HtBoaOutClient;
import com.ht.ussp.util.FastJsonUtil;

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

	/**
	 * 
	 * @Title: ELogin 
	 * @Description: 外部用户认证，ieme是手机序列号，如果是pc端，传mac地址 
	 * @return Result
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年5月16日 下午2:16:20
	 */
	@PostMapping(value = "/ELogin")
	@ApiOperation(value = "外部用户认证")
	public Result ELogin(@RequestBody EloginParam eloginParam, HttpServletResponse response) {
		String app = eloginParam.getApp();
		String ieme = eloginParam.getIeme();
		String userName = eloginParam.getUserName();
		String type = eloginParam.getType();
		String password = eloginParam.getPassword();
		Map<String, String> map = new HashMap<>();
		
		if (StringUtils.isBlank(app)|| StringUtils.isBlank(ieme) || StringUtils.isBlank(userName)
				||StringUtils.isBlank(type)||StringUtils.isBlank(password)) {
			return Result.buildFail(SysStatus.ERROR_PARAM);
		}

		Result result = outUserClient.validateUser(app, userName, password, type);
		if ("0000".equals(result.getReturnCode())) {
			String userId = result.getData().toString();
			JwtToken accessToken = createAccessJwtToken(userId, ieme);
			if(outUserClient.saveTokenToRedis(userId, accessToken.getToken())) {
				map.put("accessToken", accessToken.getToken());
			}
			
		} else {
			return Result.buildFailConvert(result.getCodeDesc(), result.getMsg());
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
	public AccessJwtToken createAccessJwtToken(String userId, String ieme) {
		if (StringUtils.isBlank(userId)) {
			throw new IllegalArgumentException("Cannot create JWT Token without userId");
		}
		if (StringUtils.isBlank(ieme)) {
			throw new IllegalArgumentException("Cannot create JWT Token without ieme");
		}
		LocalDateTime currentTime = LocalDateTime.now();
		Claims claims = Jwts.claims().setSubject("out user jwt token");
		claims.put("userId", userId);
		claims.put("ieme", ieme);
		String accessToken = Jwts.builder().setClaims(claims).setIssuer(jwtSettings.getTokenIssuer())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime.plusYears(jwtSettings.getOutUserTokenTime())
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
	@PostMapping(value = "/getOutRefreshToken")
	@ApiOperation(value = "获取外部用户刷新令牌")
	public Result OutRefreshToken(String app,String appId, String appSecret) {
		if (StringUtils.isBlank(app)||StringUtils.isBlank(appId)||StringUtils.isBlank(appSecret)) {
			return Result.buildFail(SysStatus.ERROR_PARAM);
		}
		Map<String, String> map = new HashMap<>();
		Result result=outUserClient.getClientInfo(app);
		if("9996".equals(result.getCodeDesc())) {
			return Result.buildFail(SysStatus.CLIENT_NOT_REGISTERED);
		}else if("0000".equals(result.getReturnCode())) {
			HtBoaOutClient htBoaOutClient=FastJsonUtil.jsonToPojo(FastJsonUtil.objectToJson(result.getData()), HtBoaOutClient.class);
			
			if(appId.equals(htBoaOutClient.getAppId())&&appSecret.equals(htBoaOutClient.getAppSecret())) {
				LocalDateTime currentTime = LocalDateTime.now();

				Claims claims = Jwts.claims().setSubject("Out User refresh token");
				claims.put("app", app);
				claims.put("clientId", appId);
				claims.put("secrect", appSecret);
				String refreshToken = Jwts.builder().setClaims(claims).setIssuer(jwtSettings.getTokenIssuer())
						.setId(UUID.randomUUID().toString())
						.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
						.setExpiration(Date.from(currentTime.plusMinutes(jwtSettings.getOutRefreshTokenExpTime())
								.atZone(ZoneId.systemDefault()).toInstant()))
						.signWith(SignatureAlgorithm.HS512, jwtSettings.getTokenSigningKey()).compact();
				log.info("Out user refreshToken has created:" + refreshToken);
				map.put("refreshToken", refreshToken);
				
			}else {
				
				return Result.buildFail(SysStatus.CLIENT_IS_VALID);
			}
			
		}
		
		return Result.buildSuccess(map);
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
	@GetMapping(value = "/getAccessToken", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Result getAccessToken (String refreshToken,String userId,String ieme, HttpServletRequest request) {
		RefreshToken token=null;
		Map<String, String> map = new HashMap<>();
		try {
//		String tokenPayload = tokenExtractor
//				.extract(request.getHeader(WebSecurityConfig.AUTHENTICATION_HEADER_NAME));

		RawAccessJwtToken rawToken = new RawAccessJwtToken(refreshToken);

		 token = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey())
				.orElseThrow(() -> new InvalidJwtToken());
		}catch(BadCredentialsException ex) {
			return Result.buildFail(SysStatus.TOKEN_IS_VALID);
		}catch (JwtExpiredTokenException expiredEx) {
			return Result.buildFail(SysStatus.TOKEN_IS_EXPIRED);
			
		}catch(AuthenticationServiceException ex) {
			return Result.buildFail(SysStatus.ERROR_PARAM);
		}
		
		String jti = token.getJti();
		if (!tokenVerifier.verify(jti)) {
			throw new InvalidJwtToken();
		}
		JwtToken accessToken = createAccessJwtToken(userId, ieme);
		map.put("accessToken", accessToken.getToken());
		return Result.buildSuccess(map);
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
	public Result validateJwt(@RequestParam("tokenPayload") String tokenPayload) {
		Jws<Claims> jwsClaims;
		String userId;
		String ieme;
		Map<String,String> map=new HashMap<String,String>();
		try {
			RawAccessJwtToken accessToken = new RawAccessJwtToken(tokenExtractor.extract(tokenPayload));

			jwsClaims = accessToken.parseClaims(jwtSettings.getTokenSigningKey());
			userId = jwsClaims.getBody().get("userId").toString();
			ieme = jwsClaims.getBody().get("ieme").toString();
			map.put("userId", userId);
			map.put("ieme", ieme);
		} catch (BadCredentialsException ex) {
			return Result.buildFail(SysStatus.TOKEN_IS_VALID);
		} catch (JwtExpiredTokenException expiredEx) {
			return Result.buildFail(SysStatus.TOKEN_IS_EXPIRED);
		} catch (AuthenticationServiceException asEx) {
			return Result.buildFail(SysStatus.ERROR_PARAM);
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
	public Result TestValidateSms(@RequestParam("telephone") String telephone, @RequestParam("code") String code,
			@RequestParam("app") String app) {

		Result result = smsHelper.validateSmsCode(telephone, code, app);
		return result;
		
	}

}
