package com.ht.ussp.uaa.app.security.point;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
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
import com.ht.ussp.uaa.app.jwt.JwtToken;
import com.ht.ussp.uaa.app.jwt.JwtTokenFactory;
import com.ht.ussp.uaa.app.jwt.RawAccessJwtToken;
import com.ht.ussp.uaa.app.jwt.RefreshToken;
import com.ht.ussp.uaa.app.jwt.TokenExtractor;
import com.ht.ussp.uaa.app.jwt.TokenVerifier;
import com.ht.ussp.uaa.app.model.ResponseModal;
import com.ht.ussp.uaa.app.vo.UserVo;
import com.ht.ussp.uaa.app.vo.ValidateJwtVo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.log4j.Log4j2;

/**
 * 
* @ClassName: RefreshTokenEndpoint
* @Description: TODO
* @author wim qiuwenwu@hongte.info
* @date 2018年1月8日 上午10:06:02
 */
@RestController
@Log4j2
public class RefreshTokenEndpoint {
	@Autowired
	private JwtTokenFactory tokenFactory;
	@Autowired
	private JwtSettings jwtSettings;
	@Autowired
	private TokenVerifier tokenVerifier;
	@Autowired
	@Qualifier("jwtHeaderTokenExtractor")
	private TokenExtractor tokenExtractor;
	@Autowired
	private ObjectMapper mapper;

	@RequestMapping(value = "/auth/token", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody JwtToken refreshToken(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setCharacterEncoding("UTF-8");
		RefreshToken refreshToken = null;
		try {
		String tokenPayload = tokenExtractor.extract(request.getHeader(WebSecurityConfig.AUTHENTICATION_HEADER_NAME));

		RawAccessJwtToken rawToken = new RawAccessJwtToken(tokenPayload);
		
			refreshToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey())
					.orElseThrow(() -> new InvalidJwtToken());
		} catch (BadCredentialsException ex) {
			mapper.writeValue(response.getWriter(), new ResponseModal(SysStatus.TOKEN_IS_VALID));
			return null;
		} catch (JwtExpiredTokenException expiredEx) {
			mapper.writeValue(response.getWriter(), new ResponseModal(SysStatus.TOKEN_IS_EXPIRED));
			return null;
		}catch(AuthenticationServiceException ex) {
        	mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.HEADER_CANNOT_NULL));
        	return null;
        }
		
		String jti = refreshToken.getJti();
		if (!tokenVerifier.verify(jti)) {
			throw new InvalidJwtToken();
		}

		UserVo userVo = new UserVo();
		userVo.setUserId(refreshToken.getUserId());
		userVo.setOrgCode(refreshToken.getOrgCode());
		return tokenFactory.createAccessJwtToken(userVo);
	}

	@RequestMapping(value = "/validateJwt")
	public ResponseModal validateJwt(@RequestParam("tokenPayload") String tokenPayload,HttpServletResponse response){
		response.setCharacterEncoding("UTF-8");
		ResponseModal rm=new ResponseModal();
		Jws<Claims> jwsClaims;
		String userId;
		String orgCode;
		ValidateJwtVo vdj = new ValidateJwtVo();
		
		try {
		RawAccessJwtToken accessToken = new RawAccessJwtToken(tokenExtractor.extract(tokenPayload));
		
		jwsClaims = accessToken.parseClaims(jwtSettings.getTokenSigningKey());
		 userId = jwsClaims.getBody().get("userId").toString();
		 orgCode = jwsClaims.getBody().get("orgCode").toString();
		 vdj.setUserId(userId);
		 vdj.setOrgCode(orgCode);
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

	@RequestMapping(value = "/hello")
	public String hello() {
		System.out.println("----hello");
		return "hello";

	}
}
