package com.ht.ussp.uaa.app.security.ajax;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ht.ussp.common.SysStatus;
import com.ht.ussp.uaa.app.feignClient.UserClient;
import com.ht.ussp.uaa.app.model.ResponseModal;
import com.ht.ussp.uaa.app.vo.UserVo;
import com.ht.ussp.util.EncryptUtil;
import com.ht.ussp.util.FastJsonUtil;


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

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");
        String userName=authentication.getPrincipal().toString();
        String app=authentication.getDetails().toString();
//        String params = (String) authentication.getPrincipal();
//        if(params.indexOf(";")>0) {
//        	 app=params.split(";")[0];
//        	userName=params.split(";")[1];
//        }
        if(StringUtils.isEmpty(app)) {
        	throw new BadCredentialsException(SysStatus.USER_NOT_RELATE_APP.getStatus());
        }
        
        ResponseModal loginJson = userClient.validateUser(app,userName);
        if(StringUtils.isNotEmpty(loginJson.getStatus_code())) {
        	if(loginJson.getStatus_code().startsWith("99")) {
        		throw new BadCredentialsException(loginJson.getStatus_code());
        	}
        }
        if(!"0000".equals(loginJson.getStatus_code())) {
        	throw new AuthenticationCredentialsNotFoundException(loginJson.getStatus_code());
        }

        UserVo userVo=new UserVo();
        userVo=FastJsonUtil.objectToPojo(loginJson.getResult(), UserVo.class);

        String presentPassword = (String) authentication.getCredentials();

 //     Bcrypt加密方法，在注册加密时用
//      BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
//		String hashPass = encode.encode(presentPassword);
//		logger.info("加密后的密码为："+hashPass);
        if(!EncryptUtil.matches(presentPassword,userVo.getPassword())) {
        	//增加错误次数
        	Integer failcount = userVo.getFailedCount()+1;
        	userClient.updateFailCount(userVo.getUserId(),failcount,app);
                  throw new BadCredentialsException("您输入的密码不正确!");
        }
        //登录成功则还原错误次数
    	userClient.updateFailCount(userVo.getUserId()==null?"0":userVo.getUserId(),0,app);
        //获取用户角色编码
//        if("N".equals(userVo.getController())) {
//        	 ResponseModal roleCodes = userClient.getRoleCodes(userVo.getUserId());
//        	 
//        	 if(!"0000".equals(loginJson.getStatus_code())) {
//             	throw new AuthenticationCredentialsNotFoundException(loginJson.getResult_msg());
//             }
//        	
//        	Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(); 
//        	
//        	//将角色编码转换为list
//        	List<String> list=new ArrayList<String>();
//        	String str= FastJsonUtil.objectToJson(roleCodes.getResult());
//        	list=FastJsonUtil.jsonToList(str, String.class);
//        	
//        	//转换成GrantedAuthority集合
//        	for(String roleCode:list) {
//        		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleCode); 
//        		authorities.add(authority);
//        	}
//        	 return new UsernamePasswordAuthenticationToken(userVo, null, authorities);
//        }

        return new UsernamePasswordAuthenticationToken(userVo, null, null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
