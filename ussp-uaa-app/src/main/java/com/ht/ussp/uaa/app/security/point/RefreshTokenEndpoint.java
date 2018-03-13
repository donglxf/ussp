package com.ht.ussp.uaa.app.security.point;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

/**
 * 
* @ClassName: RefreshTokenEndpoint
* @Description: TODO
* @author wim qiuwenwu@hongte.info
* @date 2018年1月8日 上午10:06:02
 */
@RestController
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
	
	

	@RequestMapping(value = "/uaa/auth/token", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody JwtToken refreshToken(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setCharacterEncoding("UTF-8");
		String tokenPayload = tokenExtractor.extract(request.getHeader(WebSecurityConfig.AUTHENTICATION_HEADER_NAME));

		RawAccessJwtToken rawToken = new RawAccessJwtToken(tokenPayload);
		RefreshToken refreshToken = null;
		try {
			refreshToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey())
					.orElseThrow(() -> new InvalidJwtToken());
		} catch (BadCredentialsException ex) {
			mapper.writeValue(response.getWriter(), new ResponseModal(SysStatus.TOKEN_IS_VALID));
			return null;
		} catch (JwtExpiredTokenException expiredEx) {
			mapper.writeValue(response.getWriter(), new ResponseModal(SysStatus.TOKEN_IS_EXPIRED));
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

	@RequestMapping(value = "/uaa/validateJwt", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ValidateJwtVo validateJwt(String tokenPayload) throws Exception{
		Jws<Claims> jwsClaims;
		ValidateJwtVo vdj = new ValidateJwtVo();

		RawAccessJwtToken AccessToken = new RawAccessJwtToken(tokenExtractor.extract(tokenPayload));
		jwsClaims = AccessToken.parseClaims(jwtSettings.getTokenSigningKey());
		String userId = jwsClaims.getBody().get("userId").toString();
        String orgCode = jwsClaims.getBody().get("orgCode").toString();
        vdj.setUserId(userId);
        vdj.setOrgCode(orgCode);
		return vdj;
	}

	@RequestMapping(value = "/uaa/hello")
	public String hello() {
		System.out.println("----hello");
		return "hello";

	}
}
