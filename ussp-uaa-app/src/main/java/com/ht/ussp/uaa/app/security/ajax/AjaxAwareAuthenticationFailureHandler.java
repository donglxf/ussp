package com.ht.ussp.uaa.app.security.ajax;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.ussp.common.SysStatus;
import com.ht.ussp.uaa.app.exception.AuthMethodNotSupportedException;
import com.ht.ussp.uaa.app.exception.JwtExpiredTokenException;
import com.ht.ussp.uaa.app.model.ResponseModal;

/**
 * 
* @ClassName: AjaxAwareAuthenticationFailureHandler
* @Description: 授权失败处理
* @author wim qiuwenwu@hongte.info
* @date 2018年1月6日 上午11:47:17
 */
@Component
public class AjaxAwareAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper mapper;
    
    @Autowired
    public AjaxAwareAuthenticationFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }	
    
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException e) throws IOException, ServletException {
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		
		if (e instanceof BadCredentialsException) {
			mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.INVALID_USER));
		} else
			if (e instanceof JwtExpiredTokenException) {
				mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.TOKEN_IS_EXPIRED));
		} else if (e instanceof AuthMethodNotSupportedException) {
			mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.METHOD_NOT_SUPPORTED));
		}
		mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.AUTH_FAILED));
	}
}
