package com.ht.ussp.uc.app.resource;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.ht.ussp.bean.LoginUserInfoHelper;
import com.ht.ussp.client.dto.LoginInfoDto;
import com.ht.ussp.common.Constants;
import com.ht.ussp.common.SysStatus;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInBmUser;
import com.ht.ussp.uc.app.domain.HtBoaInContrast;
import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInPwdHist;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.model.SelfBoaInUserInfo;
import com.ht.ussp.uc.app.service.HtBoaInBmUserService;
import com.ht.ussp.uc.app.service.HtBoaInContrastService;
import com.ht.ussp.uc.app.service.HtBoaInLoginService;
import com.ht.ussp.uc.app.service.HtBoaInPwdHistService;
import com.ht.ussp.uc.app.service.HtBoaInUserAppService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;
import com.ht.ussp.uc.app.vo.EmailVo;
import com.ht.ussp.uc.app.vo.LoginInfoVo;
import com.ht.ussp.uc.app.vo.PageVo;
import com.ht.ussp.uc.app.vo.ResetPwdUser;
import com.ht.ussp.uc.app.vo.UserMessageVo;
import com.ht.ussp.uc.app.vo.UserVo;
import com.ht.ussp.util.BeanUtils;
import com.ht.ussp.util.EncryptUtil;
import com.ht.ussp.util.LogicUtil;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

/**
 * @author adol yaojiehong@hongte.info
 * @ClassName: HtBoaInUserResource
 * @Description: 登录信息
 * @date 2018年1月10日 下午14:44:16
 */

@RestController
@RequestMapping(value = "/user")
@Log4j2
public class UserResource{

    @Autowired
    private HtBoaInUserService htBoaInUserService;
    @Autowired
    private HtBoaInUserAppService htBoaInUserAppService;
    @Autowired
    private HtBoaInLoginService htBoaInLoginService;
    @Autowired
    private HtBoaInPwdHistService htBoaInPwdHistService;
    @Autowired
   	private LoginUserInfoHelper loginUserInfoHelper;
    @Autowired
   	private HtBoaInContrastService htBoaInContrastService;
    @Autowired
    private HtBoaInBmUserService htBoaInBmUserService;
    
    @ApiOperation(value = "对内：用户个人信息查询", notes = "已登录用户查看自己的个人信息")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "path", dataType = "int")
    @RequestMapping(value = {"/in/selfinfo"}, method = RequestMethod.GET)
    public ResponseModal getSelfInfo(@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "用户个人信息查询：user/in/selfinfo param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "userId: " + userId, sl);
        SelfBoaInUserInfo u = new SelfBoaInUserInfo();
        u.setUserId(userId);
        List<SelfBoaInUserInfo> selfUserInfoList = htBoaInUserService.findAll(u);
        r = exceptionReturn(logEnd, "userId: " + userId, selfUserInfoList, sl, "个人用户信息", 1);
        if (null != r)
            return r;
        el = System.currentTimeMillis();
        log.debug(logEnd, "userId: " + userId, msg, el, el - sl);
        return new ResponseModal("200", msg, selfUserInfoList.get(0));
    }

    @ApiOperation(value = "对内：用户个人信息修改", notes = "已登录用户修改自己的个人信息")
    @ApiImplicitParam(name = "selfBoaInUserInfo", value = "用户个人信息实体", required = true, dataType = "SelfBoaInUserInfo")
    @RequestMapping(value = {"/in/selfinfo/set"}, method = RequestMethod.POST)
    public ResponseModal setSelfInfo(@RequestBody SelfBoaInUserInfo selfBoaInUserInfo) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "用户个人信息修改：user/in/selfinfo/set param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "selfBoaInUserInfo: " + selfBoaInUserInfo, sl);
        HtBoaInUser u = htBoaInUserService.findByUserId(selfBoaInUserInfo.getUserId());
         
        if (selfBoaInUserInfo.getOrgCode() != null && "" != selfBoaInUserInfo.getOrgCode()) {
            u.setOrgCode(selfBoaInUserInfo.getOrgCode());
        }
        if (selfBoaInUserInfo.getRootOrgCode() != null && "" != selfBoaInUserInfo.getRootOrgCode()) {
            u.setRootOrgCode(selfBoaInUserInfo.getRootOrgCode());
        }
        if (selfBoaInUserInfo.getIdNo() != null && "" != selfBoaInUserInfo.getIdNo()) {
            u.setIdNo(selfBoaInUserInfo.getIdNo());
        }
        u.setEmail(selfBoaInUserInfo.getEmail());
        u.setMobile(selfBoaInUserInfo.getMobile());
        u.setUserName(selfBoaInUserInfo.getUserName());
        u.setLastModifiedDatetime(new Date());
        htBoaInUserService.update(u);

        HtBoaInUser u1 = htBoaInUserService.findByUserId(selfBoaInUserInfo.getUserId());
        el = System.currentTimeMillis();
        log.debug(logEnd, "selfUserInfo: " + selfBoaInUserInfo, msg, el, el - sl);
        return new ResponseModal("200", msg, u1 );
    }

    protected ResponseModal exceptionReturn(String logEnd, String param, List<?> list, long sl, String exInfo, int row) {
        if (null == exInfo)
            exInfo = "";
        if (null == list || list.isEmpty()) {
            String msg = "无效参数，" + exInfo + "查无信息体";
            long el = System.currentTimeMillis();
            log.error(logEnd, param, msg, el, el - sl);
            return new ResponseModal("500", msg);
        } else if (row != list.size()) {
            String msg = "查询异常！查出" + exInfo + "记录数不符合要求";
            long el = System.currentTimeMillis();
            log.error(logEnd, param, msg, el, el - sl);
            return new ResponseModal("500", msg);
        }
        return null;
    }

    /**
     * @return ResponseModal
     * @throws
     * @Title: validateUser
     * @Description: 验证用户有效性
     * @author wim qiuwenwu@hongte.info
     * @date 2018年1月18日 下午6:29:39
     */

    @GetMapping("/validateUser")
    @ApiOperation(value = "验证用户")
    public ResponseModal validateUser(@RequestParam(value = "app", required = true) String app,
                                      @RequestParam(value = "userName", required = true) String userName) {
        ResponseModal rm = new ResponseModal();
        UserVo userVo = new UserVo();
        // 查找用户
        //HtBoaInUser htBoaInUser = htBoaInUserService.findByUserName(userName);
        //修改登录账号为查询userId
        // HtBoaInUser htBoaInUser = htBoaInUserService.findByUserId(userName);
        
        //修改登录账号为userId mobile email 工号
        HtBoaInUser htBoaInUser = null;
		List<HtBoaInUser> htBoaInUserList = htBoaInUserService.findByUserIdOrEmailOrMobileOrJobNumber(userName,userName,userName,userName);
		if(htBoaInUserList!=null&&!htBoaInUserList.isEmpty()) {
			htBoaInUser = htBoaInUserList.get(0);
		}
        
        if(htBoaInUser==null) {
        	HtBoaInLogin htBoaInLogin = htBoaInLoginService.findByLoginId(userName);
        	if(htBoaInLogin!=null) {
        		    if (Constants.USER_STATUS_4.equals(htBoaInLogin.getStatus())) {
        	            log.debug("该用户已被禁用！");
        	            rm.setSysStatus(SysStatus.USER_NOT_FOUND);
        	            return rm;
        	        } else if(Constants.USER_STATUS_5.equals(htBoaInLogin.getStatus())) {
        	        	log.debug("该用户已被锁定！");
         	            rm.setSysStatus(SysStatus.USER_HAS_LOCKED);
         	            return rm;
        	        }
        		htBoaInUser = htBoaInUserService.findByUserId(htBoaInLogin.getUserId());
        	}
        }
        if(htBoaInUser==null) {
            rm.setSysStatus(SysStatus.NO_RESULT);
            return rm;
		}
       if(Constants.USER_STATUS_2.equals(htBoaInUser.getStatus())  ) {
       	    log.debug("该用户已离职！");
            rm.setSysStatus(SysStatus.USER_NOT_FOUND);
            return rm;
       }
          
        if (LogicUtil.isNull(htBoaInUser) || LogicUtil.isNullOrEmpty(htBoaInUser.getUserId())) {
            rm.setSysStatus((SysStatus.INVALID_USER));
            return rm;
        } else if (htBoaInUser.getDelFlag() == 1) {
            log.debug("该用户已被删除！");
            rm.setSysStatus(SysStatus.USER_HAS_DELETED);
        } else {
            BeanUtils.deepCopy(htBoaInUser, userVo);
        }
        
        // 验证用户与系统是否匹配,匹配返回controller
        String controller = htBoaInUserAppService.findUserAndAppInfo(htBoaInUser.getUserId(), app);
        if (!LogicUtil.isNullOrEmpty(controller)) {
            userVo.setController(controller);
        } else {
            log.debug("用户来源不正确！");
            rm.setSysStatus(SysStatus.USER_NOT_RELATE_APP);
            return rm;

        }
        // 获取用户登录信息
        HtBoaInLogin htBoaInLogin = htBoaInLoginService.findByUserId(htBoaInUser.getUserId());
		if (LogicUtil.isNotNull((htBoaInLogin))) {
			BeanUtils.deepCopy(htBoaInLogin, userVo);
			if ("1".equals(htBoaInLogin.getStatus())) { //初始化密码
				rm.setSysStatus(SysStatus.PWD_FIRST_MODIFY);
			} else {
				rm.setSysStatus(SysStatus.SUCCESS);
			}
		}
        //是否首次登录，首次登录需要强制修改密码 (未修改过密码需要首次修改密码才可以使用)
        /*List<HtBoaInPwdHist>   listHtBoaInPwdHist=htBoaInPwdHistService.findByUserId(htBoaInUser.getUserId());
        if(listHtBoaInPwdHist==null||listHtBoaInPwdHist.isEmpty()) {
        	 rm.setSysStatus(SysStatus.PWD_FIRST_MODIFY);
        }else {
        	 rm.setSysStatus(SysStatus.SUCCESS);
        }*/
        
        userVo.setApp(app);
        rm.setResult(userVo);
        return rm;
    }


    /**
     * 用户信息分页查询<br>
     *
     * @param page 分页参数对象
     * @return 结果对象
     * @author 谭荣巧
     * @Date 2018/1/12 9:01
     */
    @ApiOperation(value = "用户信息分页查询")
    @PostMapping(value = "/loadListByPage", produces = {"application/json"})
    public PageResult<List<UserMessageVo>> loadListByPage(PageVo page) {
        return htBoaInUserService.getUserListPage(new PageRequest(page.getPage(), page.getLimit()), page.getOrgCode(),
                page.getKeyWord(), page.getQuery());
    }

    /**
     * 新增用户信息<br>
     *
     * @param user 用户信息数据对象
     * @return com.ht.ussp.core.Result
     * @author 谭荣巧
     * @Date 2018/1/14 12:08
     */
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
            boolean isAdd = htBoaInUserService.saveUserInfoAndLoginInfo(user, loginInfo);
            if (isAdd) {
                return Result.buildSuccess();
            }
        }
        return Result.buildFail();
    }
    
   
    @PostMapping("/delete")
    public Result delAsync(String userId) {
        if (userId != null && !"".equals(userId.trim())) {
            boolean isDel = htBoaInUserService.setDelFlagByUserId(userId);
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
        	HtBoaInUser user = htBoaInUserService.findByUserId(userId);
        	htBoaInUserService.delete(user);
        	HtBoaInContrast c = htBoaInContrastService.getHtBoaInContrastListByUcBusinessId(userId, "20");
        	htBoaInContrastService.delete(c);
        }
        return Result.buildFail();
    }
    
    @PostMapping("/view")
    public Result viewAsync(String userId) {
        if (userId != null && !"".equals(userId.trim())) {
            return Result.buildSuccess(htBoaInUserService.getUserByUserId(userId));
        }
        return Result.buildFail();
    }

    @PostMapping("/update")
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
					return Result.buildFail("登录Id已经存在或不可用", "登录Id已经存在或不可用");
				} else if (htBoaInLogin != null && htBoaInLogin.getUserId().equals(userMessageVo.getUserId())) {
					 
				} else {
					HtBoaInLogin htBoaInLogins = htBoaInLoginService.findByUserId(userMessageVo.getUserId());
					htBoaInLogins.setLoginId(userMessageVo.getLoginId());
					htBoaInLoginService.update(htBoaInLogins);
				}

			}
        	
        	HtBoaInUser user = htBoaInUserService.findByUserId(userMessageVo.getUserId());
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
            HtBoaInUser u = htBoaInUserService.update(user);
            //boolean isUpdate = htBoaInUserService.updateUserByUserId(user);
            if (u !=null) {
                return Result.buildSuccess();
            }
        }
        return Result.buildFail();
    }
    
    @ApiOperation(value = "对内，获取用户登录信息")
    @GetMapping(value = "/getLoginUserInfo")
    public LoginInfoVo getLoginUserInfo(@RequestParam("userId") String userId,@RequestParam("app") String app) {
        return htBoaInUserService.queryUserInfo(userId,app);
    }
    
    
    @PostMapping("/getUserInfoByJobNumber")
    public Result getUserInfoByJobNumber(@RequestParam("jobNumber") String jobNumber) {
        if (jobNumber != null && !"".equals(jobNumber.trim())) {
        	HtBoaInUser htBoaInUser = htBoaInUserService.findByJobNumber(jobNumber);
        	return Result.buildSuccess(htBoaInUser);
        }
        return Result.buildFail();
    }

    @ApiOperation(value = "对内，获取用户信息")
    @GetMapping(value = "/getUserInfoByUserId")
    public LoginInfoVo getUserInfoByUserId(@RequestParam("userId")String userId, @RequestParam("bmUserId")String bmUserId, @RequestParam("app") String app) {
    	LoginInfoVo loginInfoVo = null;
    	if(StringUtils.isEmpty(userId)||userId.length()==0||"null".equals(userId)) {
    		List<HtBoaInContrast> listHtBoaInContrast= htBoaInContrastService.getHtBoaInContrastListByBmUserId(bmUserId,"20");
    		if(listHtBoaInContrast==null||listHtBoaInContrast.isEmpty()) {
    			if(!StringUtils.isEmpty(bmUserId)) {
        			if(loginInfoVo==null) {
        				List<HtBoaInBmUser> listHtBoaInBmUser = htBoaInBmUserService.getHtBoaInBmUserByUserId(bmUserId);
        				if(listHtBoaInBmUser!=null && !listHtBoaInBmUser.isEmpty()) {
        					loginInfoVo = new LoginInfoVo();
        					loginInfoVo.setBmOrgCode(listHtBoaInBmUser.get(0).getOrgCode());
        					loginInfoVo.setBmUserId(bmUserId);
        					loginInfoVo.setEmail(listHtBoaInBmUser.get(0).getEmail());
        					loginInfoVo.setJobNumber(listHtBoaInBmUser.get(0).getJobNumber());
        					loginInfoVo.setUserName(listHtBoaInBmUser.get(0).getUserName());
        					loginInfoVo.setMobile(listHtBoaInBmUser.get(0).getMobile());
        					try {//历史用户信息转存（方便贷后查询历史记录） 
        						HtBoaInUser u = htBoaInUserService.saveBmUserInfo(listHtBoaInBmUser.get(0));
        						if(u!=null) {
        							loginInfoVo.setUserId(u.getUserId());
        							loginInfoVo.setMobile(u.getMobile());
        						}
							} catch (Exception e) {
								 e.printStackTrace();
							}
        				}
        	    	}
        		}
    		}else {
    			userId=listHtBoaInContrast.get(0).getUcBusinessId();
    			loginInfoVo = htBoaInUserService.queryUserInfo(userId,app);
    		}
    	}else {
    		loginInfoVo = htBoaInUserService.queryUserInfo(userId,app);
    	}
        return loginInfoVo;
    }
    
    @ApiOperation(value = "重置密码并发邮件")
    @PostMapping(value = "/sendEmailRestPwd")
    @ResponseBody
    public Result sendEmailRestPwd(String userId) {
    	EmailVo emailVo = new EmailVo();
    	emailVo.setSubject("重置密码");
    	String newPassWord = "123456";//EncryptUtil.genRandomNum(6).toUpperCase();
    	String newPassWordEncrypt = EncryptUtil.passwordEncrypt(newPassWord);
    	if(userId!=null && userId!="" && userId.length()>0) {
    		HtBoaInUser htBoaInUser = htBoaInUserService.findByUserId(userId);
    		HtBoaInLogin u = htBoaInLoginService.findByUserId(userId);
            u.setPassword(newPassWordEncrypt);
            htBoaInLoginService.update(u);
            
            //记录历史密码
            HtBoaInPwdHist htBoaInPwdHist = new HtBoaInPwdHist();
            htBoaInPwdHist.setUserId(u.getUserId());
            htBoaInPwdHist.setPassword(newPassWordEncrypt);
            htBoaInPwdHist.setPwdCreTime(new Timestamp(System.currentTimeMillis()));
            htBoaInPwdHist.setLastModifiedDatetime(new Date());
            htBoaInPwdHistService.add(htBoaInPwdHist);
            emailVo.setText("用户"+htBoaInUser.getUserName()+",重置之后的密码为："+newPassWord);
            Set to = new HashSet<>();
    		to.add(htBoaInUser.getEmail());
    		emailVo.setTo(to);
    		try {
    			//Result result =  eipClient.sendEmail(emailVo);
    			//log.debug("发送邮件："+result);
			} catch (Exception e) {
				log.debug("发送邮件Exception："+e.getMessage());
			}
    	}
    	return Result.buildSuccess(); 
    }
    
    @ApiOperation(value = "根据时间获取指定时间机构信息")
    @GetMapping(value = "/getUserListByTime")
    public List<HtBoaInUser> getUserListByTime(@RequestParam("startTime")String startTime, @RequestParam("endTime")String endTime) {
        return htBoaInUserService.getUserListByTime(startTime,endTime);
    }
    
    
	@ApiOperation(value = "批量重置密码并发邮件")
	@PostMapping(value = "/sendEmailRestPwdBatch")
	@ResponseBody
	public Result sendEmailRestPwdBatch(@RequestBody ResetPwdUser resetPwdUser) {
		EmailVo emailVo = new EmailVo();
		emailVo.setSubject("重置密码");
		if (resetPwdUser != null) {
			for (UserMessageVo userMessageVo : resetPwdUser.getResetPwdUserdata()) {
				String userId = userMessageVo.getUserId();
				String newPassWord ="123456";// EncryptUtil.genRandomNum(6).toLowerCase();
				String newPassWordEncrypt = EncryptUtil.passwordEncrypt(newPassWord);
				if (userId != null && userId != "" && userId.length() > 0) {
					HtBoaInUser htBoaInUser = htBoaInUserService.findByUserId(userId);
					HtBoaInLogin u = htBoaInLoginService.findByUserId(userId);
					emailVo.setText(userMessageVo.getUserName()+",您重置之后的密码为：" + newPassWord);
					u.setPassword(newPassWordEncrypt);
					htBoaInLoginService.update(u);

					// 记录历史密码
					HtBoaInPwdHist htBoaInPwdHist = new HtBoaInPwdHist();
					htBoaInPwdHist.setUserId(u.getUserId());
					htBoaInPwdHist.setPassword(newPassWordEncrypt);
					htBoaInPwdHist.setPwdCreTime(new Timestamp(System.currentTimeMillis()));
					htBoaInPwdHist.setLastModifiedDatetime(new Date());
					htBoaInPwdHistService.add(htBoaInPwdHist);

					Set to = new HashSet<>();
					to.add(htBoaInUser.getEmail());
					emailVo.setBcc("leiricong@hongte.info");
					emailVo.setTo(to);
					/*try {
		    			Result result =  eipClient.sendEmail(emailVo);
		    			log.debug("发送邮件："+result);
					} catch (Exception e) {
						log.debug("发送邮件Exception："+e.getMessage());
					}*/
				}
			}
		}
		return Result.buildSuccess();
	}
    
    @ApiOperation(value = "获取密码为空的用户，需要重置密码")
    @PostMapping(value = "/queryUserIsNullPwd")
    public PageResult<UserMessageVo> queryUserIsNullPwd(PageVo page) {
    	PageResult result = new PageResult();
    	result = htBoaInUserService.queryUserIsNullPwd(new PageRequest(page.getPage(), page.getLimit(),Sort.Direction.ASC,"id"),page.getOrgCode(),page.getKeyWord());
    	return result;
    }

    @ApiOperation(value = "校验数据的重复性 true：可用  false：不可用")
    @PostMapping(value = "/checkUserExist")
    public Result checkUserExist(String jobnum,String mobile,String email,String loginid,String userId) {
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
    
    @ApiOperation(value = "修改用户登录状态")
    @PostMapping("/changUserState")
    public Result changUserState(@RequestParam("userId")String userId,@RequestParam("status")String status) {
    	if(StringUtils.isEmpty(userId)) {
    		return Result.buildFail();
    	} 
    	try {
    		HtBoaInUser htBoaInUser = htBoaInUserService.findByUserId(userId);
        	if(htBoaInUser!=null) {
        		htBoaInUser.setStatus(status);
        		htBoaInUserService.update(htBoaInUser);
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return Result.buildSuccess();
    }
  
    @PostMapping(value = "/getDtoUserInfo")
    public Result getDtoUserInfo(String userId,String bmuserId) {
    	LoginInfoDto l = loginUserInfoHelper.getUserInfoByUserId(userId, bmuserId); 
    	//return Result.buildSuccess(loginUserInfoHelper.getLoginInfo());
    	return Result.buildSuccess(l);
    }
    
}
