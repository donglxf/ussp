package com.ht.ussp.uc.app.resource;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.ht.ussp.common.Constants;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInContrast;
import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.model.SelfBoaInUserInfo;
import com.ht.ussp.uc.app.service.HtBoaInContrastService;
import com.ht.ussp.uc.app.service.HtBoaInLoginService;
import com.ht.ussp.uc.app.service.HtBoaInUserBusinessService;
import com.ht.ussp.uc.app.vo.PageVo;
import com.ht.ussp.uc.app.vo.UserMessageVo;
import com.ht.ussp.util.EncryptUtil;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping(value = "/userbusiness")
@Log4j2
public class UserBusinessResource{

    @Autowired
    private HtBoaInUserBusinessService htBoaInUserBusinessService;
    @Autowired
    private HtBoaInLoginService htBoaInLoginService;
    @Autowired
   	private HtBoaInContrastService htBoaInContrastService;
    
    @ApiOperation(value = "对内：用户个人信息查询", notes = "已登录用户查看自己的个人信息")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "path", dataType = "int")
    @RequestMapping(value = {"/getUserInfo"}, method = RequestMethod.GET)
    public ResponseModal getUserInfo(@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "用户个人信息查询：user/in/selfinfo param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "userId: " + userId, sl);
        SelfBoaInUserInfo u = new SelfBoaInUserInfo();
        u.setUserId(userId);
        List<SelfBoaInUserInfo> selfUserInfoList = htBoaInUserBusinessService.findAll(u);
        el = System.currentTimeMillis();
        log.debug(logEnd, "userId: " + userId, msg, el, el - sl);
        return new ResponseModal("200", msg, selfUserInfoList.get(0));
    }
 
    @ApiOperation(value = "用户信息分页查询")
    @PostMapping(value = "/loadListByPage", produces = {"application/json"})
    public PageResult<List<UserMessageVo>> loadListByPage(PageVo page) {
        return htBoaInUserBusinessService.getUserListPage(new PageRequest(page.getPage(), page.getLimit()), page.getOrgCode(), page.getKeyWord(), page.getQuery());
    }

    @PostMapping(value = "/add")
    public Result addAsync(@RequestBody UserMessageVo userMessageVo, @RequestHeader("userId") String loginUserId) {
        if (userMessageVo != null) {
            //String userId = UUID.randomUUID().toString().replace("-", "");
        	/*String userId = user.getUserId();//作为用户的登录账号，修改为不是自动生成
            user.setUserId(userId);*/
            HtBoaInUser user = new HtBoaInUser();
            user.setDataSource(Constants.USER_DATASOURCE_1);
            user.setEmail(userMessageVo.getEmail());
            user.setIsOrgUser(1);
            user.setJobNumber(StringUtils.isEmpty(userMessageVo.getJobNumber())?null:userMessageVo.getJobNumber());
            user.setMobile(userMessageVo.getMobile());
            user.setOrgCode(userMessageVo.getOrgCode());
            user.setUserName(userMessageVo.getUserName());
            user.setIdNo((StringUtils.isEmpty(userMessageVo.getIdNo())?null:userMessageVo.getIdNo()));
            user.setRootOrgCode(userMessageVo.getRootOrgCode());
            user.setOrgPath(userMessageVo.getOrgPath());
            user.setUserType("10");
            user.setCreateOperator(loginUserId);
            user.setUpdateOperator(loginUserId);
            user.setDelFlag(0);
            user.setStatus("0");
            user.setCreatedDatetime(new Date());
            user.setLastModifiedDatetime(new Date());
            
            HtBoaInLogin loginInfo = new HtBoaInLogin();
           // loginInfo.setLoginId(UUID.randomUUID().toString().replace("-", ""));
            //loginInfo.setUserId(userId);
            loginInfo.setLoginId(userMessageVo.getLoginId()); //作为用户的登录账号，修改为不是自动生成
            loginInfo.setCreateOperator(loginUserId);
            loginInfo.setUpdateOperator(loginUserId);
            loginInfo.setStatus(Constants.USER_STATUS_1);
            loginInfo.setPassword(EncryptUtil.passwordEncrypt("123456"));
            loginInfo.setFailedCount(0);
            loginInfo.setRootOrgCode(userMessageVo.getOrgCode());
            loginInfo.setDelFlag(0);
            loginInfo.setCreatedDatetime(new Date());
            loginInfo.setLastModifiedDatetime(new Date());
            boolean isAdd = htBoaInUserBusinessService.saveUserInfoAndLoginInfo(user, loginInfo);
            if (isAdd) {
                return Result.buildSuccess();
            }
        }
        return Result.buildFail();
    }
    
   
    @PostMapping("/delete")
    public Result delAsync(String userId) {
        if (userId != null && !"".equals(userId.trim())) {
            boolean isDel = htBoaInUserBusinessService.setDelFlagByUserId(userId);
            if (isDel) {
                return Result.buildSuccess();
            }
        }
        return Result.buildFail();
    }

    @PostMapping("/deleteTrunc")
    public Result delTrunc(String userId) {
        if (userId != null && !"".equals(userId.trim())) {
        	HtBoaInLogin u = htBoaInLoginService.findByUserId(userId);
        	htBoaInLoginService.delete(u);
        	HtBoaInUser user = htBoaInUserBusinessService.findByUserId(userId);
        	htBoaInUserBusinessService.delete(user);
        	HtBoaInContrast c = htBoaInContrastService.getHtBoaInContrastListByUcBusinessId(userId, "20");
        	htBoaInContrastService.delete(c);
        }
        return Result.buildFail();
    }
    
    @PostMapping("/view")
    public Result viewAsync(String userId) {
        if (userId != null && !"".equals(userId.trim())) {
            return Result.buildSuccess(htBoaInUserBusinessService.getUserByUserId(userId));
        }
        return Result.buildFail();
    }

    @PostMapping("/update")
    public Result updateAsync(@RequestBody UserMessageVo userMessageVo, @RequestHeader("userId") String loginUserId) {
        if (userMessageVo != null) {
        	HtBoaInUser htBoaInUser = null;
        	if(userMessageVo.getJobNumber()!=null&&!"".equals(userMessageVo.getJobNumber())) {
        		htBoaInUser = htBoaInUserBusinessService.findByJobNumber(userMessageVo.getJobNumber());
        		if(htBoaInUser!=null&&!htBoaInUser.getUserId().equals(userMessageVo.getUserId())) {
            		return Result.buildFail("工号已经存在或不可用","工号已经存在或不可用");
                 } 
        	}
            if(userMessageVo.getMobile()!=null) {
        		htBoaInUser = htBoaInUserBusinessService.findByMobile(userMessageVo.getMobile());
        		if(htBoaInUser!=null&&!htBoaInUser.getUserId().equals(userMessageVo.getUserId())) {
            		return Result.buildFail("手机号已经存在或不可用","手机号已经存在或不可用");
                 } 
        	}
           if(userMessageVo.getEmail()!=null) {
        		htBoaInUser = htBoaInUserBusinessService.findByEmail(userMessageVo.getEmail());
        		if(htBoaInUser!=null&&!htBoaInUser.getUserId().equals(userMessageVo.getUserId())) {
            		return Result.buildFail("邮箱已经存在或不可用","邮箱已经存在或不可用");
                 } 
        	}
			if (userMessageVo.getLoginId() != null) {
				HtBoaInLogin htBoaInLogin = htBoaInLoginService.findByLoginId(userMessageVo.getLoginId());
				if (htBoaInLogin != null && !htBoaInLogin.getUserId().equals(userMessageVo.getUserId())) {
					return Result.buildFail("登录Id已经存在或不可用", "登录Id已经存在或不可用");
				} else if (htBoaInLogin != null && htBoaInLogin.getUserId().equals(userMessageVo.getUserId())) {
					 
				} else {
					HtBoaInLogin htBoaInLogins = htBoaInLoginService.findByUserId(userMessageVo.getUserId());
					htBoaInLogins.setLoginId(userMessageVo.getLoginId());
					htBoaInLoginService.update(htBoaInLogins);
				}

			}
        	
        	HtBoaInUser user = htBoaInUserBusinessService.findByUserId(userMessageVo.getUserId());
        	if(user==null) {
        		return Result.buildSuccess();
        	}
        	user.setUserId(userMessageVo.getUserId());
            user.setEmail(userMessageVo.getEmail());
            user.setJobNumber(userMessageVo.getJobNumber());
            user.setMobile(userMessageVo.getMobile());
            user.setOrgCode(userMessageVo.getOrgCode());
            user.setUserName(userMessageVo.getUserName());
            user.setIdNo((StringUtils.isEmpty(userMessageVo.getIdNo())?null:userMessageVo.getIdNo()));
            user.setRootOrgCode(userMessageVo.getRootOrgCode());
            user.setOrgPath(userMessageVo.getOrgPath());
            user.setUpdateOperator(loginUserId);
            user.setLastModifiedDatetime(new Date());
            HtBoaInUser u = htBoaInUserBusinessService.update(user);
            //boolean isUpdate = htBoaInUserBusinessService.updateUserByUserId(user);
            if (u !=null) {
                return Result.buildSuccess();
            }
        }
        return Result.buildFail();
    }
     
    @ApiOperation(value = "校验数据的重复性 true：可用  false：不可用")
    @PostMapping(value = "/checkUserExist")
    public Result checkUserExist(String jobnum,String mobile,String email,String loginid,String userId) {
    	HtBoaInUser htBoaInUser = null;
    	if(jobnum!=null) {
    		htBoaInUser = htBoaInUserBusinessService.findByJobNumber(jobnum);
    	}else if(mobile!=null) {
    		htBoaInUser = htBoaInUserBusinessService.findByMobile(mobile);
    	}else if(email!=null) {
    		htBoaInUser = htBoaInUserBusinessService.findByEmail(email);
    	}else if(loginid!=null) {
    		HtBoaInLogin htBoaInLogin = htBoaInLoginService.findByLoginId(loginid);
    		if(htBoaInLogin==null) {
    	       return Result.buildSuccess();
            }else {
            	userId = userId==null?"":userId;
            	if(userId.equals(htBoaInLogin.getUserId())) {
            		return Result.buildSuccess();
            	}else {
            		return Result.buildFail();
            	}
            }
    	}
		if(htBoaInUser==null) {
	       return Result.buildSuccess();
        }else {
        	userId = userId==null?"":userId;
        	if(userId.equals(htBoaInUser.getUserId())) {
        		return Result.buildSuccess();
        	}else {
        		return Result.buildFail();
        	}
        }
    }
}
