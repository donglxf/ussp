package com.ht.ussp.gateway.app.security.ajax;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ht.ussp.gateway.app.feignClients.UserClient;
import com.ht.ussp.gateway.app.model.ResponseModal;
import com.ht.ussp.gateway.app.model.UserContext;
import com.ht.ussp.gateway.app.service.User;


/**
 * 
* @ClassName: AjaxAuthenticationProvider
* @Description: 用户认证
* @author wim qiuwenwu@hongte.info
* @date 2018年1月6日 上午11:54:30
 */
@Component
public class AjaxAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	private UserClient userClient;
	
    private final BCryptPasswordEncoder encoder;
//    private final DatabaseUserService userService;

//    @Autowired
//    public AjaxAuthenticationProvider(final DatabaseUserService userService, final BCryptPasswordEncoder encoder) {
//        this.userService = userService;
//        this.encoder = encoder;
//    }
    @Autowired
    public AjaxAuthenticationProvider(final BCryptPasswordEncoder encoder) {
//        this.userService = userService;
        this.encoder = encoder;
    }
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");
        String userName=null;
        String app=null;
        String params = (String) authentication.getPrincipal();
        if(params.indexOf(";")>0) {
        	 app=params.split(";")[0];
        	userName=params.split(";")[1];
        }
        String password = (String) authentication.getCredentials(); 
        
//        User user = userService.getByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        ResponseModal loginJson = userClient.validateUser(app,userName);
        if(loginJson==null) {
        	throw new UsernameNotFoundException("User not found:" + userName);
        }
        String userId=null;
        ResponseModal passwordJson = userClient.getLoginInfo(userId);
//        if(!passwordJson.containsKey("password")&&!encoder.matches(password, passwordJson.getString("password"))) {
//                  throw new BadCredentialsException("Authentication Failed. Username or Password not valid");
//        	
//        }
        User user = new User();
        if (user.getRoles() == null) throw new InsufficientAuthenticationException("User has no roles assigned");
        
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getRole().authority()))
                .collect(Collectors.toList());
        
        UserContext userContext = UserContext.create(user.getUsername(), authorities);
        
        return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
