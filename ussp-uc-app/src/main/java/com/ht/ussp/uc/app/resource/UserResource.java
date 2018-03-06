package com.ht.ussp.uc.app.resource;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInPwdHist;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.domain.HtBoaInUserRole;
import com.ht.ussp.uc.app.feignclients.EipClient;
import com.ht.ussp.uc.app.model.ChangePwd;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.model.SelfBoaInUserInfo;
import com.ht.ussp.uc.app.model.SysStatus;
import com.ht.ussp.uc.app.service.HtBoaInLoginService;
import com.ht.ussp.uc.app.service.HtBoaInPwdHistService;
import com.ht.ussp.uc.app.service.HtBoaInUserAppService;
import com.ht.ussp.uc.app.service.HtBoaInUserRoleService;
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
    private HtBoaInUserRoleService htBoaInUserRoleService;
    @Autowired
    private HtBoaInPwdHistService htBoaInPwdHistService;
    
    @Autowired
	private EipClient eipClient;

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
        htBoaInUserService.update(u);

        HtBoaInUser u1 = htBoaInUserService.findByUserId(selfBoaInUserInfo.getUserId());
        el = System.currentTimeMillis();
        log.debug(logEnd, "selfUserInfo: " + selfBoaInUserInfo, msg, el, el - sl);
        return new ResponseModal("200", msg, u1 );
    }

    @ApiOperation(value = "对内：修改密码", notes = "修改密码")
    @RequestMapping(value = {"/in/changePwd"}, method = RequestMethod.POST)
    public ResponseModal changePwd(@RequestBody ChangePwd changePwd, @RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "修改密码：login/in/changePwd param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, changePwd.toString(), sl);

        HtBoaInLogin u = htBoaInLoginService.findByUserId(userId);
        //验证原密码是否正确
        /*if (!u.getPassword().equals(EncryptUtil.passwordEncrypt(changePwd.getOldPwd()))) {
            return new ResponseModal("500", "原密码输入不正确");
        }*/
        if(!EncryptUtil.matches(changePwd.getOldPwd(),u.getPassword())) {
        	return new ResponseModal("500", "原密码输入不正确");
        }
        String newPassWordEncrypt = EncryptUtil.passwordEncrypt(changePwd.getNewPwd());
        u.setPassword(newPassWordEncrypt);

        //记录历史密码
        HtBoaInPwdHist htBoaInPwdHist = new HtBoaInPwdHist();
        htBoaInPwdHist.setUserId(u.getUserId());
        htBoaInPwdHist.setPassword(newPassWordEncrypt);
        htBoaInPwdHist.setPwdCreTime(new Timestamp(System.currentTimeMillis()));
        htBoaInPwdHist.setLastModifiedDatetime(new Date());
        htBoaInLoginService.update(u);
        htBoaInPwdHistService.add(htBoaInPwdHist);
        el = System.currentTimeMillis();
        log.debug(logEnd, "resetPwd: " + changePwd, msg, el, el - sl);
        return new ResponseModal("200", "成功");
    }

    @ApiOperation(value = "对内：根据UserId查询用户角色", notes = "根据UserId查询用户角色")
    @RequestMapping(value = {"/listUserRoleByPage"}, method = RequestMethod.GET)
    public PageResult<HtBoaInUserRole> listUserRoleByPage(PageVo page) {
        PageResult result = new PageResult();
        PageConf pageConf = new PageConf();
        pageConf.setPage(page.getPage());
        pageConf.setSize(page.getLimit());
        pageConf.setSearch(page.getKeyWord());
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "根据UserId查询用户角色：user/listUserRoleByPage param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "page: " + page, sl);
        result = htBoaInUserRoleService.listUserRoleByPage(pageConf, page.getQuery());
        el = System.currentTimeMillis();
        log.debug(logEnd, "page: " + page, msg, el, el - sl);
        return result;
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
        HtBoaInUser htBoaInUser = htBoaInUserService.findByUserIdOrEmailOrMobileOrJobNumber(userName,userName,userName,userName);
        
          
        if (LogicUtil.isNull(htBoaInUser) || LogicUtil.isNullOrEmpty(htBoaInUser.getUserId())) {
            rm.setSysStatus((SysStatus.USER_NOT_FOUND));
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
            rm.setSysStatus(SysStatus.USER_NOT_MATCH_APP);
            return rm;

        }
        // 获取用户登录信息
        HtBoaInLogin htBoaInLogin = htBoaInLoginService.findByUserId(htBoaInUser.getUserId());
        if (LogicUtil.isNotNull((htBoaInLogin))) {
            BeanUtils.deepCopy(htBoaInLogin, userVo);
        }

        userVo.setApp(app);
        rm.setSysStatus(SysStatus.SUCCESS);
        rm.setResult(userVo);
        return rm;
    }

//    /**
//	 *
//	 * @Title: getRoleCodes
//	 * @Description: 获取用户角色编码
//	 * @return ResponseModal
//	 * @throws
//	 * @author wim qiuwenwu@hongte.info
//	 * @date 2018年1月18日 下午6:29:59
//	 */
//	@GetMapping("/getRoleCodes")
//	@ApiOperation(value = "获取用户角色编码")
//	public ResponseModal getRoleCodes(@RequestParam(value = "userId", required = true) String userId) {
//		ResponseModal rm = new ResponseModal();
//		List<String> roleCodes = new ArrayList<>();
//		roleCodes=htBoaInUserRoleService.getAllRoleCodes(userId);
//		if (roleCodes.isEmpty()) {
//			rm.setSysStatus(SysStatus.NO_ROLE);
//			return rm;
//		} else {
//			rm.setSysStatus(SysStatus.SUCCESS);
//			rm.setResult(roleCodes);
//			return rm;
//		}
//
//	}

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
    public Result addAsync(@RequestBody HtBoaInUser user, @RequestHeader("userId") String loginUserId) {
        if (user != null) {
            //String userId = UUID.randomUUID().toString().replace("-", "");
        	String userId = user.getUserId();//作为用户的登录账号，修改为不是自动生成
            user.setUserId(userId);
            user.setCreateOperator(loginUserId);
            user.setUpdateOperator(loginUserId);
            user.setDelFlag(0);
            HtBoaInLogin loginInfo = new HtBoaInLogin();
            loginInfo.setLoginId(UUID.randomUUID().toString().replace("-", ""));
            loginInfo.setUserId(userId);
            loginInfo.setCreateOperator(loginUserId);
            loginInfo.setUpdateOperator(loginUserId);
            loginInfo.setStatus("0");
            loginInfo.setPassword(EncryptUtil.passwordEncrypt("123456"));
            loginInfo.setFailedCount(0);
            loginInfo.setRootOrgCode(user.getRootOrgCode());
            loginInfo.setDelFlag(0);
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

    @PostMapping("/view")
    public Result viewAsync(String userId) {
        if (userId != null && !"".equals(userId.trim())) {
            return Result.buildSuccess(htBoaInUserService.getUserByUserId(userId));
        }
        return Result.buildFail();
    }

    @PostMapping("/update")
    public Result updateAsync(@RequestBody HtBoaInUser user, @RequestHeader("userId") String loginUserId) {
        if (user != null) {
            user.setUpdateOperator(loginUserId);
            boolean isUpdate = htBoaInUserService.updateUserByUserId(user);
            if (isUpdate) {
                return Result.buildSuccess();
            }
        }
        return Result.buildFail();
    }

    @ApiOperation(value = "对内，获取用户登录信息")
    @GetMapping(value = "/getLoginUserInfo")
    public LoginInfoVo getLoginUserInfo(@RequestParam("userId") String userId) {
        return htBoaInUserService.queryUserInfo(userId);
    }

    @ApiOperation(value = "重置密码并发邮件")
    @PostMapping(value = "/sendEmailRestPwd")
    @ResponseBody
    public Result sendEmailRestPwd(String userId) {
    	EmailVo emailVo = new EmailVo();
    	emailVo.setSubject("重置密码");
    	String newPassWord = EncryptUtil.genRandomNum(6).toUpperCase();
    	String newPassWordEncrypt = EncryptUtil.passwordEncrypt(newPassWord);
    	emailVo.setText("您重置之后的密码为："+newPassWord);
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
            
            Set to = new HashSet<>();
    		to.add(htBoaInUser.getEmail());
    		emailVo.setTo(to);
    		try {
    			Result result =  eipClient.sendEmail(emailVo);
    			log.debug("发送邮件："+result);
			} catch (Exception e) {
				log.debug("发送邮件Exception："+e.getMessage());
			}
    	}
    	return Result.buildSuccess(); 
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
				String newPassWord = EncryptUtil.genRandomNum(6).toUpperCase();
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
					emailVo.setTo(to);
					try {
		    			Result result =  eipClient.sendEmail(emailVo);
		    			log.debug("发送邮件："+result);
					} catch (Exception e) {
						log.debug("发送邮件Exception："+e.getMessage());
					}
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

   
}
