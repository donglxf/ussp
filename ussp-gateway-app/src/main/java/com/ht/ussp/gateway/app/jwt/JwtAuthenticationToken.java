package com.ht.ussp.gateway.app.jwt;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.ht.ussp.gateway.app.vo.UserVo;


/**
 * 
 * @ClassName: JwtAuthenticationToken
 * @Description: TODO
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月8日 上午10:05:16
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 2877954820905567501L;

    private RawAccessJwtToken rawAccessToken;
    private UserVo userVo;

    public JwtAuthenticationToken(RawAccessJwtToken unsafeToken) {
        super(null);
        this.rawAccessToken = unsafeToken;
        this.setAuthenticated(false);
    }

    public JwtAuthenticationToken(UserVo userVo, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.eraseCredentials();
        this.userVo = userVo;
        super.setAuthenticated(true);
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return rawAccessToken;
    }

    @Override
    public Object getPrincipal() {
        return this.userVo;
    }

    @Override
    public void eraseCredentials() {        
        super.eraseCredentials();
        this.rawAccessToken = null;
    }
}
