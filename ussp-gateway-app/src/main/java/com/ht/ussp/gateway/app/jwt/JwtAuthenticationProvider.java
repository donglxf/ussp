package com.ht.ussp.gateway.app.jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.ht.ussp.gateway.app.config.JwtSettings;
import com.ht.ussp.gateway.app.vo.UserVo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

/**
 * 
 * @ClassName: JwtAuthenticationProvider
 * @Description: 解析JWT
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月6日 上午10:13:08
 */
@Component
@SuppressWarnings("unchecked")
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtSettings jwtSettings;
    
    @Autowired
    public JwtAuthenticationProvider(JwtSettings jwtSettings) {
        this.jwtSettings = jwtSettings;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();
        //验签
        Jws<Claims> jwsClaims = rawAccessToken.parseClaims(jwtSettings.getTokenSigningKey());
//        String subject = jwsClaims.getBody().getSubject();
        List<String> roles = jwsClaims.getBody().get("roles", List.class);
        String userId=jwsClaims.getBody().get("userId").toString();
        String controller=jwsClaims.getBody().get("controller").toString();
        
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(); 
     	for(String roleCode:roles) {
    		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleCode); 
    		authorities.add(authority);
    	}
        
//        List<GrantedAuthority> authorities = scopes.stream()
//            .map(SimpleGrantedAuthority::new)
//            .collect(Collectors.toList());
//        
//        UserContext context = UserContext.create(subject, authorities);
        UserVo userVo=new UserVo();
        userVo.setUserId(userId);
        userVo.setController(controller);
        return new JwtAuthenticationToken(userVo, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
