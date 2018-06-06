package com.ht.ussp.ouc.app.resource;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.common.SysStatus;
import com.ht.ussp.core.Result;
import com.ht.ussp.ouc.app.domain.HtBoaOutLogin;
import com.ht.ussp.ouc.app.domain.HtBoaOutPwdHist;
import com.ht.ussp.ouc.app.domain.HtBoaOutUser;
import com.ht.ussp.ouc.app.service.HtBoaOutLoginService;
import com.ht.ussp.ouc.app.service.HtBoaOutPwdHistService;
import com.ht.ussp.ouc.app.service.HtBoaOutUserService;
import com.ht.ussp.util.EncryptUtil;
import com.ht.ussp.util.md5.Cryptography;

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
	@Autowired
	HtBoaOutPwdHistService htBoaOutPwdHistService;
	

	@ApiOperation(value = "忘记密码/重置密码", notes = "登录type: sms:短信  email:邮箱  normal:用户名密码")
	@PostMapping(value = "/resetPwd")
    public Result resetPwd(@RequestParam(value = "userName")String userName,@RequestParam(value = "newPassword")String newPassword,@RequestParam(value = "app")String app,@RequestParam(value = "registType")String registType) {
		if(StringUtils.isEmpty(userName)) {
			return Result.buildFail(); 
		}
		HtBoaOutLogin loginInfo = null;
		HtBoaOutUser u = null;
		if("sms".equals(registType)) {
			 u =htBoaOutUserService.findByMobile(userName);
			if(u!=null) {
				loginInfo = htBoaOutLoginService.findByUserId(u.getUserId());
			} 
		}else if("email".equals(registType)) {
			 u =htBoaOutUserService.findByEmail(userName);
			if(u!=null) {
				loginInfo = htBoaOutLoginService.findByUserId(u.getUserId());
			} 
		}else if("normal".equals(registType)) {
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
	        if(u!=null) {
	        	if("10".equals(u.getUserType())) {
	        		u.setUserType("1");
	        		htBoaOutUserService.saveUser(u);
	        	}
	        }
	        return Result.buildSuccess(loginInfo);
		}else {
			return Result.buildFail("9999","找不到相关用户信息");
		}
    }
	
	@ApiOperation(value = "修改密码")
	@PostMapping(value = "/changePwd")
	public Result changePwd(@RequestParam(value = "userId")String userId,@RequestParam(value = "oldPassword")String oldPassword, @RequestParam(value = "newPassword")String newPassword) {
		long sl = System.currentTimeMillis(), el = 0L;
		HtBoaOutLogin u = htBoaOutLoginService.findByUserId(userId);
		HtBoaOutUser htBoaOutUser = htBoaOutUserService.findByUserId(userId);
		if(u==null || htBoaOutUser==null) {
			return Result.buildFail("9999","找不到相关用户信息");
		}
		if(htBoaOutUser!=null) {
			if("10".equals(htBoaOutUser.getUserType())) { //存量用户先验证原密码是否正确，然后转换为新的密码
				try {
					String oldPwd = Cryptography.tripleDESEncrypt(oldPassword, "~#^&tuandai*%#housebaby#111!"); //微信加密key
					if(StringUtils.isNotEmpty(oldPwd)) {
						if(oldPwd.equals(u.getOldPassword())) {
							u.setPassword(EncryptUtil.passwordEncrypt(newPassword));
							u = htBoaOutLoginService.saveUserLogin(u);
							htBoaOutUser.setUserType("1");
							htBoaOutUserService.saveUser(htBoaOutUser);
						}else {
							return Result.buildFail(SysStatus.PWD_INVALID.getStatus(),"密码输入不正确");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// 验证原密码是否正确
		if (!EncryptUtil.matches(oldPassword, u.getPassword())) {
			return Result.buildFail(SysStatus.PWD_INVALID.getStatus(), "原密码输入不正确");
		}

		// 记录历史密码
		HtBoaOutPwdHist htBoaOutPwdHist = new HtBoaOutPwdHist();
		htBoaOutPwdHist.setUserId(u.getUserId());
		htBoaOutPwdHist.setPassword(u.getPassword());
		htBoaOutPwdHist.setPwdCreTime(new Timestamp(System.currentTimeMillis()));
		htBoaOutPwdHist.setLastModifiedDatetime(new Date());

		String newPassWordEncrypt = EncryptUtil.passwordEncrypt(newPassword);
		u.setPassword(newPassWordEncrypt);
		if ("1".equals(u.getStatus())) {
			u.setStatus("0");
		}
		
		htBoaOutLoginService.saveUserLogin(u);
		htBoaOutPwdHistService.save(htBoaOutPwdHist);
		el = System.currentTimeMillis();
		return Result.buildSuccess();
	}
}
