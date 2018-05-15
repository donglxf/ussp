package com.ht.ussp.uaa.app.exception;

import org.springframework.security.core.AuthenticationException;

import com.ht.ussp.uaa.app.jwt.JwtToken;


/**
 * 
 * @ClassName: JwtExpiredTokenException
 * @Description: TODO
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月6日 上午10:42:07
 */
public class JwtExpiredTokenException extends AuthenticationException {
    private static final long serialVersionUID = -5959543783324224864L;
    
    private JwtToken token;

    public JwtExpiredTokenException(String msg) {
        super(msg);
    }

    public JwtExpiredTokenException(JwtToken token, String msg, Throwable t) {
        super(msg, t);
        this.token = token;
    }

    public String token() {
        return this.token.getToken();
    }
}
