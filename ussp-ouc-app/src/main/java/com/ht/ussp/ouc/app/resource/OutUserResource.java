package com.ht.ussp.ouc.app.resource;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.common.Constants;
import com.ht.ussp.common.SysStatus;
import com.ht.ussp.core.Result;
import com.ht.ussp.ouc.app.domain.HtBoaOutLogin;
import com.ht.ussp.ouc.app.domain.HtBoaOutUser;
import com.ht.ussp.ouc.app.service.HtBoaOutLoginService;
import com.ht.ussp.ouc.app.service.HtBoaOutUserService;
import com.ht.ussp.util.EncryptUtil;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;


@RestController
@RequestMapping(value = "/user")
@Log4j2
public class OutUserResource{ 
 
	@Autowired
	HtBoaOutUserService htBoaOutUserService;
	
	@Autowired
	HtBoaOutLoginService htBoaOutLoginService;

	@ApiOperation(value = "用户注册", notes = "注册类型：sms:短信注册 email:邮箱注册 normal:用户名密码注册")
	@GetMapping(value = "/regist")
    public Result regist(@RequestParam(value = "userName")String userName,@RequestParam(value = "password")String password,@RequestParam(value = "app")String app,@RequestParam(value = "registType")String registType) {
		if(StringUtils.isEmpty(userName)) {
			return Result.buildFail(); 
		}
		String userId = UUID.randomUUID().toString().replace("-", "");
		HtBoaOutUser htBoaOutUser = new HtBoaOutUser();
		htBoaOutUser.setUserId(userId);
		htBoaOutUser.setUserName(userName);
		htBoaOutUser.setRegistType(registType);
		if("sms".equals(registType)) {
			htBoaOutUser.setMobile(userName);
			HtBoaOutUser u =htBoaOutUserService.findByMobile(userName);
			if(u!=null&&userName.equals(u.getMobile())) {
				return Result.buildFail("9999","手机号已被注册"); 
			}
		}else if("email".equals(registType)) {
			htBoaOutUser.setEmail(userName);
			HtBoaOutUser u =htBoaOutUserService.findByEmail(userName);
			if(u!=null&&userName.equals(u.getEmail())) {
				return Result.buildFail("9999","邮箱已被注册"); 
			}
		}else if("normal".equals(registType)) {
			htBoaOutUser.setUserName(userName);
		}else {
			return Result.buildFail("9999","注册类型不对【"+registType+"】"); 
		}
		htBoaOutUser.setDataSource(app);
		htBoaOutUser.setCreatedDatetime(new Date());
		htBoaOutUser.setLastModifiedDatetime(new Date());
		htBoaOutUser = htBoaOutUserService.saveUser(htBoaOutUser);
         
		if(htBoaOutUser!=null) {
			HtBoaOutLogin loginInfo = new HtBoaOutLogin();
			if("normal".equals(registType)) {
				loginInfo.setLoginId(htBoaOutUser.getUserName());
			}else {
				loginInfo.setLoginId(UUID.randomUUID().toString().replace("-", ""));
			}
			loginInfo = htBoaOutLoginService.findByLoginId(loginInfo.getLoginId());
			if(loginInfo!=null) {
				loginInfo.setLoginId(UUID.randomUUID().toString().replace("-", ""));
			}else {
				loginInfo = new HtBoaOutLogin();
				if("normal".equals(registType)) {
					loginInfo.setLoginId(htBoaOutUser.getUserName());
				}else {
					loginInfo.setLoginId(UUID.randomUUID().toString().replace("-", ""));
				}
			}
	        loginInfo.setUserId(htBoaOutUser.getUserId());
	        loginInfo.setStatus(Constants.USER_STATUS_0);
	        loginInfo.setPassword(EncryptUtil.passwordEncrypt(password));
	        loginInfo.setFailedCount(0);
	        loginInfo.setDelFlag(0);
	        loginInfo.setCreatedDatetime(new Date());
	        loginInfo.setLastModifiedDatetime(new Date());
	        loginInfo = htBoaOutLoginService.saveUserLogin(loginInfo);
	        return Result.buildSuccess(htBoaOutUser.getUserId());
		}
        return Result.buildFail();
    }
	
	@ApiOperation(value = "验证用户", notes = "登录type：sms:短信  email:邮箱  normal:用户名密码 ")
	@GetMapping(value = "/validateUser")
    public Result validateUser(@RequestParam(value = "userName")String userName,@RequestParam(value = "password")String password,@RequestParam(value = "app")String app,@RequestParam(value = "type")String type) {
        if("normal".equals(type)) {
        	HtBoaOutLogin htBoaOutLogin = htBoaOutLoginService.findByUserId(userName);
        	if(htBoaOutLogin!=null) {
        		 //验证原密码是否正确
                if(!EncryptUtil.matches(password,htBoaOutLogin.getPassword())){
                	return Result.buildFail(SysStatus.PWD_INVALID.getStatus(),"密码输入不正确");
                }else {
                	return Result.buildSuccess(htBoaOutLogin.getUserId());
                }
        	}else {
        		return Result.buildFail("9999","找不到相关用户信息");
        	}
		} else {
			HtBoaOutUser htBoaOutUser = htBoaOutUserService.findByEmailOrMobile(userName, userName);
			if (htBoaOutUser == null) {
				return Result.buildFail("9999", "找不到相关用户信息");
			} else {
				HtBoaOutLogin htBoaOutLogin = htBoaOutLoginService.findByUserId(htBoaOutUser.getUserId());
				if (htBoaOutLogin != null) {
					// 验证原密码是否正确
					return Result.buildSuccess(htBoaOutLogin.getUserId());
				} else {
					return Result.buildFail("9999", "找不到相关用户信息");
				}
			}
		}
    }
}
