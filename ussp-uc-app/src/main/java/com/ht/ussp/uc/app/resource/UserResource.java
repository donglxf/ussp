package com.ht.ussp.uc.app.resource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInPwdHist;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.domain.HtBoaInUserApp;
import com.ht.ussp.uc.app.domain.HtBoaInUserRole;
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
import com.ht.ussp.uc.app.vo.PageVo;
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
@CrossOrigin(origins = "*")
@RequestMapping(value = "/user")
@Log4j2
public class UserResource {

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

    @ApiOperation(value = "对内：用户个人信息查询", notes = "已登录用户查看自己的个人信息")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "path", dataType = "int")
    @RequestMapping(value = { "/in/selfinfo/{userId}"}, method = RequestMethod.GET)
    public ResponseModal getSelfInfo(@PathVariable String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "用户个人信息查询：user/in/selfinfo param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "userId: " + userId, sl);
        SelfBoaInUserInfo u = new SelfBoaInUserInfo();
        u.setUserId(userId);
        List<SelfBoaInUserInfo> selfUserInfoList = htBoaInUserService.findAll(u);
        r = exceptionReturn(logEnd, "userId: " + userId, selfUserInfoList, sl, "个人用户信息", 1);
        if (null != r)
            return r;
        el = System.currentTimeMillis();
        log.info(logEnd, "userId: " + userId, msg, el, el - sl);
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
        log.info(logStart, "selfBoaInUserInfo: " + selfBoaInUserInfo, sl);
        HtBoaInUser u = new HtBoaInUser();
        BeanUtils.setObjectFieldsEmpty(u);
        u.setUserId(selfBoaInUserInfo.getUserId());
        List<HtBoaInUser> htBoaInUserList = htBoaInUserService.findAll(u);
        r = exceptionReturn(logEnd, "selfBoaInUserInfo: " + selfBoaInUserInfo, htBoaInUserList, sl, "个人用户信息", 1);
        if (null != r)
            return r;
        u = htBoaInUserList.get(0);
        if(selfBoaInUserInfo.getOrgCode()!=null && ""!=selfBoaInUserInfo.getOrgCode()) {
        	 u.setOrgCode(selfBoaInUserInfo.getOrgCode());
        }
        if(selfBoaInUserInfo.getRootOrgCode()!=null && ""!=selfBoaInUserInfo.getRootOrgCode()) {
        	u.setRootOrgCode(selfBoaInUserInfo.getRootOrgCode());
        }
        if(selfBoaInUserInfo.getIdNo()!=null && ""!=selfBoaInUserInfo.getIdNo()) {
        	u.setIdNo(selfBoaInUserInfo.getIdNo());
        }
        u.setEmail(selfBoaInUserInfo.getEmail());
        u.setMobile(selfBoaInUserInfo.getMobile());
        u.setUserName(selfBoaInUserInfo.getUserName());
        htBoaInUserService.update(u);

		SelfBoaInUserInfo s = new SelfBoaInUserInfo();
		s.setUserId(u.getUserId());
		List<SelfBoaInUserInfo> selfUserInfoList = htBoaInUserService.findAll(s);
		r = exceptionReturn(logEnd, "selfBoaInUserInfo: " + selfBoaInUserInfo, selfUserInfoList, sl, "个人用户信息", 1);
		if (null != r)
			return r;
		el = System.currentTimeMillis();
		log.info(logEnd, "selfUserInfo: " + selfBoaInUserInfo, msg, el, el - sl);
		return new ResponseModal("200", msg, selfUserInfoList.get(0));
	}

    @ApiOperation(value = "对内：修改密码", notes = "修改密码")
    @RequestMapping(value = { "/in/changePwd" }, method = RequestMethod.POST)
    public ResponseModal changePwd(@RequestBody ChangePwd changePwd) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "修改密码：login/in/changePwd param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, changePwd.toString(), sl);
        HtBoaInUser htBoaInUser = new HtBoaInUser();
        htBoaInUser.setUserId(changePwd.getUserId());
        List<HtBoaInUser> htBoaInUserList = htBoaInUserService .findAll(htBoaInUser);
        r = exceptionReturn(logEnd, "changePwd: " + changePwd, htBoaInUserList, sl, "个人用户信息", 1);
        if (null != r)
            return r;
        htBoaInUser = htBoaInUserList.get(0);

        HtBoaInLogin u = htBoaInLoginService.findByUserId(htBoaInUser.getUserId());
        //验证原密码是否正确
        if(!u.getPassword().equals(changePwd.getOldPwd())) {
        	return new ResponseModal("500", "原密码输入不正确");
        }
        u.setPassword(changePwd.getNewPwd());

        //记录历史密码
        HtBoaInPwdHist htBoaInPwdHist = new HtBoaInPwdHist();
        htBoaInPwdHist.setUserId(u.getUserId());
        htBoaInPwdHist.setPassword(changePwd.getNewPwd());
        htBoaInPwdHist.setPwdCreTime(new Timestamp(System.currentTimeMillis()));
        htBoaInPwdHist.setLastModifiedDatetime(new Date());
        htBoaInLoginService.update(u);
        htBoaInPwdHistService.add(htBoaInPwdHist);
        el = System.currentTimeMillis();
        log.info(logEnd, "resetPwd: " + changePwd, msg, el, el - sl);
        return new ResponseModal("200", "成功");
    }

    @ApiOperation(value = "对内：根据UserId查询用户角色", notes = "根据UserId查询用户角色")
    @RequestMapping(value = { "/listUserRoleByPage"}, method = RequestMethod.GET)
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
        log.info(logStart, "page: " + page, sl);
        result =  htBoaInUserRoleService.listUserRoleByPage(pageConf,page.getQuery());
        el = System.currentTimeMillis();
        log.info(logEnd, "page: " + page, msg, el, el - sl);
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
	 * 
	 * @Title: validateUser 
	 * @Description: 验证用户有效性
	 * @return ResponseModal
	 * @throws
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
		HtBoaInUser htBoaInUser = htBoaInUserService.findByUserName(userName);
		if (LogicUtil.isNull(htBoaInUser) || LogicUtil.isNullOrEmpty(htBoaInUser.getUserId())) {
			rm.setSysStatus((SysStatus.USER_NOT_FOUND));
			return rm;
		} else if (htBoaInUser.getDelFlag() == 1) {
			log.info("该用户已被删除！");
			rm.setSysStatus(SysStatus.USER_HAS_DELETED);
		} else {
			BeanUtils.deepCopy(htBoaInUser, userVo);
		}

		// 验证用户与系统是否匹配,匹配返回controller
		String controller = htBoaInUserAppService.findUserAndAppInfo(htBoaInUser.getUserId(), app);
		if (!LogicUtil.isNullOrEmpty(controller)) {
			userVo.setController(controller);
		} else {
			log.info("用户来源不正确！");
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

    /**
	 *
	 * @Title: getRoleCodes
	 * @Description: 获取用户角色编码
	 * @return ResponseModal
	 * @throws
	 * @author wim qiuwenwu@hongte.info
	 * @date 2018年1月18日 下午6:29:59
	 */
	@GetMapping("/getRoleCodes")
	@ApiOperation(value = "获取用户角色编码")
	public ResponseModal getRoleCodes(@RequestParam(value = "userId", required = true) String userId) {
		ResponseModal rm = new ResponseModal();
		List<String> roleCodes = new ArrayList<>();
		roleCodes=htBoaInUserRoleService.getAllRoleCodes(userId);
		if (roleCodes.isEmpty()) {
			rm.setSysStatus(SysStatus.NO_ROLE);
			return rm;
		} else {
			rm.setSysStatus(SysStatus.SUCCESS);
			rm.setResult(roleCodes);
			return rm;
		}

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
	@PostMapping(value = "/loadListByPage")
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
    public Result addAsync(@RequestBody HtBoaInUser user) {
        if (user != null) {
            String userId = UUID.randomUUID().toString().replace("-", "");
            user.setUserId(userId);
            //TODO 需要获取登录信息，设置创建人，修改人
            user.setCreateOperator("10003");
            user.setUpdateOperator("10003");
            user.setDelFlag(0);
            HtBoaInLogin loginInfo = new HtBoaInLogin();
            loginInfo.setLoginId(UUID.randomUUID().toString().replace("-", ""));
            loginInfo.setUserId(userId);
            //TODO 需要获取登录信息，设置创建人，修改人
            loginInfo.setCreateOperator("10003");
            loginInfo.setUpdateOperator("10003");
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

	@PostMapping("/delete/{userId}")
	public Result delAsync(@PathVariable String userId) {
		if (userId != null && !"".equals(userId.trim())) {
			boolean isDel = htBoaInUserService.setDelFlagByUserId(userId);
			if (isDel) {
				return Result.buildSuccess();
			}
		}
		return Result.buildFail();
	}

	@PostMapping("/view/{userId}")
	public Result viewAsync(@PathVariable String userId) {
		if (userId != null && !"".equals(userId.trim())) {
			return Result.buildSuccess(htBoaInUserService.getUserByUserId(userId));
		}
		return Result.buildFail();
	}

    @PostMapping("/update")
    public Result updateAsync(@RequestBody HtBoaInUser user) {
        if (user != null) {
            // TODO 需要获取登录信息，设置修改人
            user.setUpdateOperator("测试人");
            boolean isUpdate = htBoaInUserService.updateUserByUserId(user);
            if (isUpdate) {
                return Result.buildSuccess();
            }
        }
        return Result.buildFail();
    }
    
	@PostMapping(value = "/queryResource")
	@ApiOperation(value = "查找资源")
	public ResponseModal queryResource(@RequestHeader("app") String app, @RequestHeader("userId") String userId) {
		
		ResponseModal rm = new ResponseModal();
		List<HtBoaInUserApp> htBoaInUserApp = htBoaInUserAppRepository.findByuserId(userId);
		
		
		return rm;
		
	}  
    

}
