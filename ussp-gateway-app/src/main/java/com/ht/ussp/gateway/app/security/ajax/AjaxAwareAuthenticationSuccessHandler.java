package com.ht.ussp.gateway.app.security.ajax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.ussp.gateway.app.feignClients.RoleClient;
import com.ht.ussp.gateway.app.jwt.JwtToken;
import com.ht.ussp.gateway.app.jwt.JwtTokenFactory;
import com.ht.ussp.gateway.app.model.ResponseModal;
import com.ht.ussp.gateway.app.vo.UserVo;

/**
 * 
 * @ClassName: AjaxAwareAuthenticationSuccessHandler
 * @Description: 用户验证成功后的处理
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月6日 上午11:47:40
 */
@Component
public class AjaxAwareAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private final ObjectMapper mapper;
	private final JwtTokenFactory tokenFactory;
	@Autowired
	private RoleClient roleClient;

	@Autowired
	public AjaxAwareAuthenticationSuccessHandler(final ObjectMapper mapper, final JwtTokenFactory tokenFactory) {
		this.mapper = mapper;
		this.tokenFactory = tokenFactory;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		UserVo userVo = (UserVo) authentication.getPrincipal();
		JwtToken accessToken = tokenFactory.createAccessJwtToken(userVo);
		JwtToken refreshToken = tokenFactory.createRefreshToken(userVo);

		Map<String, String> tokenMap = new HashMap<String, String>();
		tokenMap.put("token", accessToken.getToken());
		tokenMap.put("refreshToken", refreshToken.getToken());

		// 从authentication中获取用户角色编码
		List<String> list = new ArrayList<String>();
		for (GrantedAuthority roleCode : authentication.getAuthorities()) {
			list.add(roleCode.getAuthority());
		}

		if (list.isEmpty()&& !("N").equals(userVo.getController())) {
			throw new IllegalArgumentException("User doesn't have any privileges");
		}
		// 查找并保存资源
		ResponseModal saveResources = roleClient.saveResources(userVo,list);
		 if(saveResources.getStatus_code()!=1) {
          	throw new AuthenticationCredentialsNotFoundException(saveResources.getResult_msg());
          }
		
		System.out.println(saveResources.getResult());
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		mapper.writeValue(response.getWriter(), tokenMap);

		clearAuthenticationAttributes(request);
	}

	/**
	 * 
	 * @Title: clearAuthenticationAttributes 
	 * @Description: 清除有可能存储的authentication 
	 * @return void 
	 * @throws
	 */
	protected final void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session == null) {
			return;
		}

		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}
}
