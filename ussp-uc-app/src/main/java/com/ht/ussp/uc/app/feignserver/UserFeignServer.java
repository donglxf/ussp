package com.ht.ussp.uc.app.feignserver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.ht.ussp.common.Constants;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInContrast;
import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.model.SelfBoaInUserInfo;
import com.ht.ussp.uc.app.service.HtBoaInContrastService;
import com.ht.ussp.uc.app.service.HtBoaInLoginService;
import com.ht.ussp.uc.app.service.HtBoaInUserAppService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;
import com.ht.ussp.uc.app.vo.LoginInfoVo;
import com.ht.ussp.uc.app.vo.UserMessageVo;
import com.ht.ussp.util.EncryptUtil;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

 
@RestController
@RequestMapping(value = "/userFeignServer")
@Log4j2
public class UserFeignServer{

    @Autowired
    private HtBoaInUserService htBoaInUserService;
    @Autowired
    private HtBoaInUserAppService htBoaInUserAppService;
    @Autowired
    private HtBoaInLoginService htBoaInLoginService;
    @Autowired
   	private HtBoaInContrastService htBoaInContrastService;
    
    @ApiOperation(value = "根据userId查询用户信息")
    @RequestMapping(value = {"/getUserByUserId"}, method = RequestMethod.GET)
    public ResponseModal getUserByUserId(@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "用户个人信息查询：user/in/selfinfo param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "userId: " + userId, sl);
        SelfBoaInUserInfo u = new SelfBoaInUserInfo();
        u.setUserId(userId);
        List<SelfBoaInUserInfo> selfUserInfoList = htBoaInUserService.findAll(u);
        el = System.currentTimeMillis();
        log.debug(logEnd, "userId: " + userId, msg, el, el - sl);
        return new ResponseModal("200", msg, selfUserInfoList.get(0));
    }

    
    /**
     * 新增信贷用户信息<br>
     *
     * @param user 用户信息数据对象
     * @return com.ht.ussp.core.Result
     * @author 谭荣巧
     * @Date 2018/1/14 12:08
     */
    @GetMapping(value = "/addUser")
    public Result addAsync(@RequestBody UserMessageVo userMessageVo, @RequestHeader("userId") String loginUserId, @RequestHeader("app") String app) {
        if (userMessageVo != null) {
            HtBoaInUser user = new HtBoaInUser();
            user.setDataSource(Constants.USER_DATASOURCE_3);
            user.setEmail(userMessageVo.getEmail());
            user.setIsOrgUser(1);
            user.setJobNumber(userMessageVo.getJobNumber());
            user.setMobile(userMessageVo.getMobile());
            user.setOrgCode(userMessageVo.getOrgCode());
            user.setUserName(userMessageVo.getUserName());
            user.setIdNo(userMessageVo.getIdNo());
            user.setRootOrgCode(userMessageVo.getRootOrgCode());
            user.setOrgPath(userMessageVo.getOrgPath());
            user.setUserType("10");
            user.setCreateOperator(loginUserId);
            user.setUpdateOperator(loginUserId);
            user.setDelFlag(0);
            
            HtBoaInLogin loginInfo = new HtBoaInLogin();
           // loginInfo.setLoginId(UUID.randomUUID().toString().replace("-", ""));
            //loginInfo.setUserId(userId);
            loginInfo.setLoginId(userMessageVo.getLoginId()); //作为用户的登录账号，修改为不是自动生成
            loginInfo.setCreateOperator(loginUserId);
            loginInfo.setUpdateOperator(loginUserId);
            loginInfo.setStatus(Constants.USER_STATUS_0);
            loginInfo.setPassword(EncryptUtil.passwordEncrypt("123456"));
            loginInfo.setFailedCount(0);
            loginInfo.setRootOrgCode(userMessageVo.getOrgCode());
            loginInfo.setDelFlag(0);
            boolean isAdd = htBoaInUserService.saveUserInfoAndLoginInfo(user, loginInfo);
            if (isAdd) {
                return Result.buildSuccess();
            }
        }
        return Result.buildFail();
    }
    
    public Result updateAsync(@RequestBody UserMessageVo userMessageVo, @RequestHeader("userId") String loginUserId) {
        if (userMessageVo != null) {
        	HtBoaInUser htBoaInUser = null;
        	if(userMessageVo.getJobNumber()!=null&&!"".equals(userMessageVo.getJobNumber())) {
        		htBoaInUser = htBoaInUserService.findByJobNumber(userMessageVo.getJobNumber());
        		if(htBoaInUser!=null&&!htBoaInUser.getUserId().equals(userMessageVo.getUserId())) {
            		return Result.buildFail("工号已经存在或不可用","工号已经存在或不可用");
                 } 
        	}
            if(userMessageVo.getMobile()!=null) {
        		htBoaInUser = htBoaInUserService.findByMobile(userMessageVo.getMobile());
        		if(htBoaInUser!=null&&!htBoaInUser.getUserId().equals(userMessageVo.getUserId())) {
            		return Result.buildFail("手机号已经存在或不可用","手机号已经存在或不可用");
                 } 
        	}
           if(userMessageVo.getEmail()!=null) {
        		htBoaInUser = htBoaInUserService.findByEmail(userMessageVo.getEmail());
        		if(htBoaInUser!=null&&!htBoaInUser.getUserId().equals(userMessageVo.getUserId())) {
            		return Result.buildFail("邮箱已经存在或不可用","邮箱已经存在或不可用");
                 } 
        	}
			if (userMessageVo.getLoginId() != null) {
				HtBoaInLogin htBoaInLogin = htBoaInLoginService.findByLoginId(userMessageVo.getLoginId());
				if (htBoaInLogin != null && !htBoaInLogin.getUserId().equals(userMessageVo.getUserId())) {
					return Result.buildFail("用户名已经存在或不可用", "用户名已经存在或不可用");
				} else if (htBoaInLogin != null && htBoaInLogin.getUserId().equals(userMessageVo.getUserId())) {
					 
				} else {
					HtBoaInLogin htBoaInLogins = htBoaInLoginService.findByUserId(userMessageVo.getUserId());
					htBoaInLogins.setLoginId(userMessageVo.getLoginId());
					htBoaInLoginService.update(htBoaInLogins);
				}

			}
        	
        	HtBoaInUser user = new HtBoaInUser();
        	user.setUserId(userMessageVo.getUserId());
            user.setEmail(userMessageVo.getEmail());
            user.setJobNumber(userMessageVo.getJobNumber());
            user.setMobile(userMessageVo.getMobile());
            user.setOrgCode(userMessageVo.getOrgCode());
            user.setUserName(userMessageVo.getUserName());
            user.setIdNo(userMessageVo.getIdNo());
            user.setRootOrgCode(userMessageVo.getRootOrgCode());
            user.setOrgPath(userMessageVo.getOrgPath());
            user.setUpdateOperator(loginUserId);
            boolean isUpdate = htBoaInUserService.updateUserByUserId(user);
            if (isUpdate) {
                return Result.buildSuccess();
            }
        }
        return Result.buildFail();
    }
 
    @ApiOperation(value = "获取指定userId用户信息")
    @GetMapping(value = "/getUserInfoByUserId")
    public LoginInfoVo getUserInfoByUserId(@RequestParam("userId")String userId, @RequestParam("bmUserId")String bmUserId, @RequestParam("app") String app) {
    	if(StringUtils.isEmpty(userId)) {
    		List<HtBoaInContrast> listHtBoaInContrast= htBoaInContrastService.getHtBoaInContrastListByBmUserId(bmUserId,"20");
    		if(listHtBoaInContrast==null||listHtBoaInContrast.isEmpty()) {
    			return null;
    		}else {
    			userId=listHtBoaInContrast.get(0).getUcBusinessId();
    		}
    	}
        return htBoaInUserService.queryUserInfo(userId,app);
    }
    
    @ApiOperation(value = "校验数据的重复性  返回true：可用  false：不可用")
    public Result checkUserExist(String jobnum,String mobile,String email,String loginid) {
    	HtBoaInUser htBoaInUser = null;
    	if(jobnum!=null) {
    		htBoaInUser = htBoaInUserService.findByJobNumber(jobnum);
    	}else if(mobile!=null) {
    		htBoaInUser = htBoaInUserService.findByMobile(mobile);
    	}else if(email!=null) {
    		htBoaInUser = htBoaInUserService.findByEmail(email);
    	}else if(loginid!=null) {
    		HtBoaInLogin htBoaInLogin = htBoaInLoginService.findByLoginId(loginid);
    		if(htBoaInLogin==null) {
    	       return Result.buildSuccess();
            }else {
         	   return Result.buildFail();
            }
    	}
		if(htBoaInUser==null) {
	       return Result.buildSuccess();
        }else {
     	   return Result.buildFail();
        }
    }
 
    @ApiOperation(value = "修改用户状态 用户状态 0 正常  1禁用 2离职  4冻结 5锁定")
    @PostMapping("/changUserState")
    public Result changUserState(@RequestParam("userId")String userId,@RequestParam("status")String status) {
    	if(StringUtils.isEmpty(userId)) {
    		return Result.buildFail();
    	} 
    	try {
    		HtBoaInLogin htBoaInLogin = htBoaInLoginService.findByUserId(userId);
        	if(htBoaInLogin!=null) {
        		htBoaInLogin.setStatus(status);
        		htBoaInLoginService.update(htBoaInLogin);
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return Result.buildSuccess();
    }
}
