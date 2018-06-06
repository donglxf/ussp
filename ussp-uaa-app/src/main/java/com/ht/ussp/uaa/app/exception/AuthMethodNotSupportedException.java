package com.ht.ussp.uaa.app.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * 
 * @ClassName: AuthMethodNotSupportedException
 * @Description: 请求类型异常
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月6日 上午11:32:02
 */
public class AuthMethodNotSupportedException extends AuthenticationServiceException {
    private static final long serialVersionUID = 3705043083010304496L;

    public AuthMethodNotSupportedException(String msg) {
        super(msg);
    }
}
