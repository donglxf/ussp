package com.ht.ussp.ouc.app.resource;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.core.Result;
import com.ht.ussp.ouc.app.domain.HtBoaOutLogin;
import com.ht.ussp.ouc.app.domain.HtBoaOutUser;
import com.ht.ussp.ouc.app.service.HtBoaOutLoginService;
import com.ht.ussp.ouc.app.service.HtBoaOutUserService;
import com.ht.ussp.util.EncryptUtil;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;


@RestController
@RequestMapping(value = "/login")
@Log4j2
public class OutUserLoginResource{ 
 
	@Autowired
	HtBoaOutUserService htBoaOutUserService;
	
	@Autowired
	HtBoaOutLoginService htBoaOutLoginService;

	@ApiOperation(value = "忘记密码/重置密码", notes = "registType：1 手机号 2邮箱 3用户名密码")
	@GetMapping(value = "/resetPwd")
    public Result resetPwd(@RequestParam(value = "userName")String userName,@RequestParam(value = "newPassword")String newPassword,@RequestParam(value = "app")String app,@RequestParam(value = "registType")String registType) {
		if(StringUtils.isEmpty(userName)) {
			return Result.buildFail(); 
		}
		HtBoaOutLogin loginInfo = null;
		if("1".equals(registType)) {
			HtBoaOutUser u =htBoaOutUserService.findByMobile(userName);
			if(u!=null) {
				loginInfo = htBoaOutLoginService.findByUserId(u.getUserId());
			} 
		}else if("2".equals(registType)) {
			HtBoaOutUser u =htBoaOutUserService.findByEmail(userName);
			if(u!=null) {
				loginInfo = htBoaOutLoginService.findByUserId(u.getUserId());
			} 
		}else if("3".equals(registType)) {
			loginInfo = htBoaOutLoginService.findByLoginId(userName);
		}else {
			return Result.buildFail("9999","注册类型不对【"+registType+"】"); 
		} 
		if(loginInfo!=null) {
			loginInfo.setPassword(EncryptUtil.passwordEncrypt(newPassword));
	        loginInfo.setFailedCount(0);
	        loginInfo.setDelFlag(0);
	        loginInfo.setLastModifiedDatetime(new Date());
	        loginInfo = htBoaOutLoginService.saveUserLogin(loginInfo);
	        return Result.buildSuccess(loginInfo);
		}else {
			return Result.buildFail("9999","找不到相关用户信息");
		}
    }
}
