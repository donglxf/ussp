package com.ht.ussp.ouc.app.resource;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.common.Constants;
import com.ht.ussp.common.SysStatus;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.ouc.app.domain.HtBoaOutLogin;
import com.ht.ussp.ouc.app.domain.HtBoaOutPwdHist;
import com.ht.ussp.ouc.app.domain.HtBoaOutUser;
import com.ht.ussp.ouc.app.service.HtBoaOutLoginService;
import com.ht.ussp.ouc.app.service.HtBoaOutPwdHistService;
import com.ht.ussp.ouc.app.service.HtBoaOutUserService;
import com.ht.ussp.ouc.app.vo.PageVo;
import com.ht.ussp.util.DateUtil;
import com.ht.ussp.util.EncryptUtil;
import com.ht.ussp.util.md5.Cryptography;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.extern.log4j.Log4j2;


@RestController
@RequestMapping(value = "/user")
@Log4j2
public class OutUserResource{ 
 
	@Autowired
	HtBoaOutUserService htBoaOutUserService;
	
	@Autowired
	HtBoaOutLoginService htBoaOutLoginService;
	
	@Autowired
	HtBoaOutPwdHistService htBoaOutPwdHistService;

	@ApiOperation(value = "外部用户注册", notes = "返回userId")
	@ApiImplicitParams({ @ApiImplicitParam(name = "userName", paramType = "query",dataType = "String", required = true, value = "注册账号"),
			@ApiImplicitParam(name = "password", paramType = "query",dataType = "String", required = true, value = "密码"),
			@ApiImplicitParam(name = "app", paramType = "query",dataType = "String", required = true, value = "系统编码"),
			@ApiImplicitParam(name = "registType", paramType = "query",dataType = "String", required = true, value = "注册类型：sms:短信验证码注册 email:邮箱注册 normal:用户名密码注册") })
	@ApiResponse(code=0,  message = "userId")
	@PostMapping(value = "/regist")
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
			HtBoaOutLogin loginInfo = htBoaOutLoginService.findByLoginId(userName);
			if(loginInfo!=null) {
				return Result.buildFail("9999","用户名已存在"); 
			}
			htBoaOutUser.setUserName(userName); 
		}else {
			return Result.buildFail("9999","注册类型不对【"+registType+"】"); 
		}
		htBoaOutUser.setDataSource(app);
		htBoaOutUser.setCreatedDatetime(new Date());
		htBoaOutUser.setStatus("0");
		htBoaOutUser.setUserType("0");
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
	        loginInfo.setStatus("0");
	        loginInfo.setCreatedDatetime(new Date());
	        loginInfo.setLastModifiedDatetime(new Date());
	        loginInfo = htBoaOutLoginService.saveUserLogin(loginInfo);
	        return Result.buildSuccess(htBoaOutUser.getUserId());
		}
        return Result.buildFail();
    }
	
	@ApiOperation(value = "验证用户", notes = "登录type：sms:短信  email:邮箱  normal:用户名密码 ")
	@ApiImplicitParams({ @ApiImplicitParam(name = "userName", paramType = "query",dataType = "String", required = true, value = "注册账号"),
		@ApiImplicitParam(name = "password", paramType = "query",dataType = "String", required = true, value = "密码"),
		@ApiImplicitParam(name = "app",paramType = "query",dataType = "String", required = true, value = "系统编码"),
		@ApiImplicitParam(name = "type", paramType = "query",dataType = "String", required = true, value = "登录type：sms:短信验证  email:邮箱  normal:用户名密码 ") })
	@GetMapping(value = "/validateUser")
    public Result validateUser(@RequestParam(value = "userName")String userName,@RequestParam(value = "password")String password,@RequestParam(value = "app")String app,@RequestParam(value = "type")String type) {
		HtBoaOutUser htBoaOutUser =null;
		HtBoaOutLogin htBoaOutLogin = null;
		if("email".equals(type)) {
			htBoaOutUser = htBoaOutUserService.findByEmail(userName);
			if (htBoaOutUser == null) {
				return Result.buildFail("9999", "找不到相关用户信息");
			}
			htBoaOutLogin = htBoaOutLoginService.findByUserId(htBoaOutUser.getUserId());
		}else if("sms".equals(type)) {
			htBoaOutUser = htBoaOutUserService.findByMobile(userName);
			if (htBoaOutUser == null) {
				return Result.buildFail("9999", "找不到相关用户信息");
			}
			htBoaOutLogin = htBoaOutLoginService.findByUserId(htBoaOutUser.getUserId());
		}else if("normal".equals(type)) { 
			htBoaOutLogin = htBoaOutLoginService.findByLoginId(userName);
			if(htBoaOutLogin!=null) {
				htBoaOutUser = htBoaOutUserService.findByUserId(htBoaOutLogin.getUserId());
				if (htBoaOutUser == null) {
					return Result.buildFail("9999", "找不到相关用户信息");
				}
			}
		}else {
			return Result.buildFail("9999","登录类型不存在【"+type+"】"); 
      	}
		if (htBoaOutLogin != null) {
			if(htBoaOutLogin.getFailedCount()>=5 && "2".equals(htBoaOutLogin.getStatus())) {
				if(htBoaOutLogin.getBlockedDateTime()!=null) {
					Date lockedTime = DateUtil.addHour2Date(24,htBoaOutLogin.getBlockedDateTime());
					if(lockedTime.getTime()<=DateUtil.getNow().getTime()) { //如果锁定时间超过24小时则解锁
						htBoaOutLogin.setFailedCount(0);
						htBoaOutLogin.setBlockedDateTime(null);
						htBoaOutLogin.setStatus("0");
						htBoaOutLogin = htBoaOutLoginService.saveUserLogin(htBoaOutLogin);
						if("2".equals(htBoaOutUser.getStatus())) {
							htBoaOutUser.setStatus("0");
							htBoaOutUserService.saveUser(htBoaOutUser);
						}
					}else {
						if(!"2".equals(htBoaOutUser.getStatus())) { 
							htBoaOutUser.setStatus("2");
							htBoaOutUserService.saveUser(htBoaOutUser);
						}
						return Result.buildFail(SysStatus.PWD_LOCKING.getStatus(),SysStatus.PWD_LOCKING.getMsg());
					}
				}
			}
			//存量用户处理
    		if(htBoaOutUser!=null) {
    			if(!"0".equals(htBoaOutUser.getStatus())) {
    				return Result.buildFail(SysStatus.USER_NOT_FOUND.getStatus(),SysStatus.USER_NOT_FOUND.getMsg());
    			}
    			if(StringUtils.isNotEmpty(htBoaOutUser.getUserType())) {
    				if("10".equals(htBoaOutUser.getUserType())) { //存量用户先验证原密码是否正确，然后转换为新的密码
    					try {
							String oldPwd = Cryptography.tripleDESEncrypt(password, "~#^&tuandai*%#housebaby#111!"); //微信加密key
							if(StringUtils.isNotEmpty(oldPwd)) {
								if(oldPwd.equals(htBoaOutLogin.getOldPassword())) {
									htBoaOutLogin.setPassword(EncryptUtil.passwordEncrypt(password));
									htBoaOutLogin = htBoaOutLoginService.saveUserLogin(htBoaOutLogin);
									htBoaOutUser.setUserType("1");
									htBoaOutUserService.saveUser(htBoaOutUser);
								}else {
									Integer failCount = htBoaOutLogin.getFailedCount()+1;
									htBoaOutLogin.setFailedCount(failCount);
									if(failCount>=5) {
										htBoaOutLogin.setBlockedDateTime(new Date());
										htBoaOutLogin.setStatus("2");
									}
									htBoaOutLogin = htBoaOutLoginService.saveUserLogin(htBoaOutLogin);
									return Result.buildFail(SysStatus.PWD_INVALID.getStatus(),"密码输入不正确,错误次数="+failCount);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
    				}
    			}
    		}
			//验证原密码是否正确
            if(!EncryptUtil.matches(password,htBoaOutLogin.getPassword())){
            	Integer failCount = htBoaOutLogin.getFailedCount()+1;
				htBoaOutLogin.setFailedCount(failCount);
				if(failCount>=5) {
					htBoaOutLogin.setBlockedDateTime(new Date());
					htBoaOutLogin.setStatus("2");
				}
				htBoaOutLogin = htBoaOutLoginService.saveUserLogin(htBoaOutLogin);
				return Result.buildFail(SysStatus.PWD_INVALID.getStatus(),"密码输入不正确,错误次数="+failCount);
            }else {
            	htBoaOutLogin.setFailedCount(0);
            	htBoaOutLogin.setBlockedDateTime(null);
				htBoaOutLogin = htBoaOutLoginService.saveUserLogin(htBoaOutLogin);
            	return Result.buildSuccess(htBoaOutLogin.getUserId());
            }
		} else {
			return Result.buildFail("9999", "找不到相关用户信息");
		}
    }
	
	@PostMapping("/changeStatus")
	public Result changeStatus(String userId,String status) {
		if (userId != null && !"".equals(userId.trim())) {
			if("0".equals(status)) {//恢复用户状态
				HtBoaOutLogin u = htBoaOutLoginService.findByUserId(userId);
				u.setStatus("0");
				u.setLastModifiedDatetime(new Date());
				htBoaOutLoginService.saveUserLogin(u);
			} 
			HtBoaOutUser user = htBoaOutUserService.findByUserId(userId);
			user.setStatus(status);
			htBoaOutUserService.delete(user);
		}
		return Result.buildFail();
	}
	
	@ApiOperation(value = "重置密码")
	@PostMapping(value = "/restPwd")
	@ResponseBody
	public Result restPwd(String userId) {
		String newPassWord = "123456";// EncryptUtil.genRandomNum(6).toUpperCase();
		String newPassWordEncrypt = EncryptUtil.passwordEncrypt(newPassWord);
		if (userId != null && userId != "" && userId.length() > 0) {
			HtBoaOutLogin u = htBoaOutLoginService.findByUserId(userId);
			u.setPassword(newPassWordEncrypt);
			htBoaOutLoginService.saveUserLogin(u);

			// 记录历史密码
			HtBoaOutPwdHist htBoaOutPwdHist = new HtBoaOutPwdHist();
			htBoaOutPwdHist.setUserId(u.getUserId());
			htBoaOutPwdHist.setPassword(newPassWordEncrypt);
			htBoaOutPwdHist.setPwdCreTime(new Timestamp(System.currentTimeMillis()));
			htBoaOutPwdHist.setLastModifiedDatetime(new Date());
			htBoaOutPwdHistService.save(htBoaOutPwdHist);
		}
		return Result.buildSuccess();
	}

	@PostMapping("/deleteTrunc")
	public Result delTrunc(String userId) {
		if (userId != null && !"".equals(userId.trim())) {
			HtBoaOutLogin u = htBoaOutLoginService.findByUserId(userId);
			htBoaOutLoginService.delete(u);
			HtBoaOutUser user = htBoaOutUserService.findByUserId(userId);
			htBoaOutUserService.delete(user);
		}
		return Result.buildFail();
	}
	
	@ApiOperation(value = "外部用户信息分页查询")
	@PostMapping(value = "/loadListByPage", produces = { "application/json" })
	public PageResult<List<HtBoaOutUser>> loadListByPage(PageVo page) {
		return htBoaOutUserService.getUserListPage(new PageRequest(page.getPage(), page.getLimit()), page.getQuery());
	}
	
	@ApiOperation(value = "获取当前登录用户信息", notes = "通过网关访问，获取头部userId")
	@GetMapping(value = "/getCurUserInfo")
    public Result getCurUserInfo(@RequestHeader("userId") String userId) {
		HtBoaOutUser user = htBoaOutUserService.findByUserId(userId);
		return Result.buildSuccess(user);
	}
	
	@ApiOperation(value = "修改用户电话号码")
	@PostMapping(value = "/changeMobile", produces = { "application/json" })
	public Result changeMobile(String userId,String newMobile) {
		HtBoaOutUser user = htBoaOutUserService.findByUserId(userId);
		if(user!=null) {
			if(StringUtils.isNotEmpty(newMobile)) {
				HtBoaOutUser users = htBoaOutUserService.findByMobile(newMobile);
				if(users!=null) {
					if(!user.getUserId().equals(users.getUserId())) {
						return Result.buildFail("9999", "电话号码已经被使用");
					}
				}
				user.setMobile(newMobile);
				user.setUserName(newMobile);
				htBoaOutUserService.saveUser(user);
			}
		}
		return Result.buildSuccess();
	}
}
