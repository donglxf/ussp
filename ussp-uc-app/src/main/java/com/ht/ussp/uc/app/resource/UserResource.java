package com.ht.ussp.uc.app.resource;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.*;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.model.SelfBoaInUserInfo;
import com.ht.ussp.uc.app.model.SysStatus;
import com.ht.ussp.uc.app.service.*;
import com.ht.ussp.uc.app.util.BeanUtils;
import com.ht.ussp.uc.app.util.LogicUtil;
import com.ht.ussp.uc.app.vo.PageVo;
import com.ht.ussp.uc.app.vo.UserMessageVo;
import com.ht.ussp.uc.app.vo.UserVo;
import com.ht.ussp.util.EncryptUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private HtBoaInOrgService htBoaInOrgService;
    @Autowired
    private HtBoaInRoleService htBoaInRoleService;
    @Autowired
    private HtBoaInPositionService htBoaInPositionService;
    @Autowired
    private HtBoaInUserRoleService htBoaInUserRoleService;
    @Autowired
    private HtBoaInPositionUserService htBoaInPositionUserService;
    @Autowired
    private HtBoaInPositionRoleService htBoaInPositionRoleService;


    @ApiOperation(value = "对内：用户个人信息查询", notes = "已登录用户查看自己的个人信息")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "path", dataType = "int")
    @RequestMapping(value = {
            "/in/selfinfo/{userId}"}, method = RequestMethod.GET)
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
        return new ResponseModal(200, msg, selfUserInfoList.get(0));
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
        r = exceptionReturn(logEnd, "selfBoaInUserInfo: " + selfBoaInUserInfo,
                htBoaInUserList, sl, "个人用户信息", 1);
        if (null != r)
            return r;
        u = htBoaInUserList.get(0);
        HtBoaInOrg htBoaInOrg = new HtBoaInOrg();
        BeanUtils.setObjectFieldsEmpty(htBoaInOrg);
        htBoaInOrg.setOrgCode(selfBoaInUserInfo.getOrgCode());
        htBoaInOrg.setRootOrgCode(selfBoaInUserInfo.getRootOrgCode());
        List<HtBoaInOrg> htBoaInOrgList = htBoaInOrgService.findAll(htBoaInOrg);
        r = exceptionReturn(logEnd, "selfBoaInUserInfo: " + selfBoaInUserInfo,
                htBoaInOrgList, sl, "组织机构信息", 1);
        if (null != r)
            return r;
        List<HtBoaInRole> htBoaInRoleList = htBoaInRoleService
                .findByRoleCodeIn(selfBoaInUserInfo.getRoleCodes());
        r = exceptionReturn(logEnd, "selfUserInfo: " + selfBoaInUserInfo,
                htBoaInRoleList, sl, "角色信息", selfBoaInUserInfo.getRoleCodes().size());
        if (null != r)
            return r;
        List<HtBoaInPosition> htBoaInPositionList = htBoaInPositionService
                .findByPositionCodeIn(selfBoaInUserInfo.getPositionCodes());
        r = exceptionReturn(logEnd, "selfUserInfo: " + selfBoaInUserInfo,
                htBoaInPositionList, sl, "岗位信息", selfBoaInUserInfo.getPositionCodes().size());
        if (null != r)
            return r;
        u.setOrgCode(selfBoaInUserInfo.getOrgCode());
        u.setRootOrgCode(selfBoaInUserInfo.getRootOrgCode());
        u.setEmail(selfBoaInUserInfo.getEmail());
        u.setIdNo(selfBoaInUserInfo.getIdNo());
        u.setMobile(selfBoaInUserInfo.getMobile());
        u.setUserName(selfBoaInUserInfo.getUserName());
        u.setOrgPath(htBoaInOrgList.get(0).getOrgPath());
        HtBoaInUserRole htBoaInUserRole = new HtBoaInUserRole();
        htBoaInUserRole.setUserId(u.getUserId());
        htBoaInUserRoleService.delete(htBoaInUserRole);
        List<HtBoaInUserRole> htBoaInUserRoleList = new ArrayList<HtBoaInUserRole>();
        for (String roleCode : selfBoaInUserInfo.getRoleCodes()) {
            HtBoaInUserRole t = new HtBoaInUserRole();
            t.setUserId(u.getUserId());
            t.setRoleCode(roleCode);
            t.setRootOrgCode(u.getRootOrgCode());
            htBoaInUserRoleList.add(t);
        }
        if (!htBoaInUserRoleList.isEmpty())
            htBoaInUserRoleService.add(htBoaInUserRoleList);
        HtBoaInPositionUser htBoaInPositionUser = new HtBoaInPositionUser();
        htBoaInPositionUser.setUserId(u.getUserId());
        htBoaInPositionUserService.delete(htBoaInPositionUser);
        List<HtBoaInPositionUser> htBoaInPositionUserList = new ArrayList<HtBoaInPositionUser>();
        for (String positionCode : selfBoaInUserInfo.getPositionCodes()) {
            HtBoaInPositionUser t = new HtBoaInPositionUser();
            t.setUserId(u.getUserId());
            t.setPositionCode(positionCode);
            t.setRootOrgCode(u.getRootOrgCode());
            htBoaInPositionUserList.add(t);
        }
        if (!htBoaInPositionUserList.isEmpty())
            htBoaInPositionUserService.add(htBoaInPositionUserList);
        SelfBoaInUserInfo s = new SelfBoaInUserInfo();
        u.setUserId(u.getUserId());
        List<SelfBoaInUserInfo> selfUserInfoList = htBoaInUserService.findAll(s);
        r = exceptionReturn(logEnd, "selfBoaInUserInfo: " + selfBoaInUserInfo,
                selfUserInfoList, sl, "个人用户信息", 1);
        if (null != r)
            return r;
        el = System.currentTimeMillis();
        log.info(logEnd, "selfUserInfo: " + selfBoaInUserInfo, msg, el, el - sl);
        return new ResponseModal(200, msg, selfUserInfoList.get(0));
    }

    protected ResponseModal exceptionReturn(String logEnd, String param,
                                            List<?> list, long sl, String exInfo, int row) {
        if (null == exInfo)
            exInfo = "";
        if (null == list || list.isEmpty()) {
            String msg = "无效参数，" + exInfo + "查无信息体";
            long el = System.currentTimeMillis();
            log.error(logEnd, param, msg, el, el - sl);
            return new ResponseModal(500, msg);
        } else if (row != list.size()) {
            String msg = "查询异常！查出" + exInfo + "记录数不符合要求";
            long el = System.currentTimeMillis();
            log.error(logEnd, param, msg, el, el - sl);
            return new ResponseModal(500, msg);
        }
        return null;
    }

    /**
     * @return ResponseModal 
     * @throws
     * @Title: validateUser
     * @Description: 验证用户有效性
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

        // 验证用户与系统是否匹配
        HtBoaInUserApp htBoaInUserApp = htBoaInUserAppService.findUserAndAppInfo(htBoaInUser.getUserId());
        if (LogicUtil.isNull(htBoaInUserApp) || LogicUtil.isNullOrEmpty(htBoaInUserApp.getApp())) {
            rm.setSysStatus(SysStatus.USER_NOT_RELATE_APP);
            return rm;
        } else if (app.equals(htBoaInUserApp.getApp())) {
            log.info("用户与系统匹配正确！");
            BeanUtils.deepCopy(htBoaInUserApp, userVo);
        } else {
            log.info("用户来源不正确！");
            rm.setSysStatus(SysStatus.USER_NOT_MATCH_APP);
            return rm;
        }
        // 获取用户登录信息
        HtBoaInLogin htBoaInLogin = htBoaInLoginService.findByUserId(htBoaInUser.getUserId());
        if (LogicUtil.isNotNull((htBoaInUserApp))) {
            BeanUtils.deepCopy(htBoaInLogin, userVo);
        }
        rm.setSysStatus(SysStatus.SUCCESS);
        rm.setResult(userVo);
        return rm;
    }

    /**
     * @return ResponseModal
     * @throws
     * @Title: getRoleCodes
     * @Description: 获取用户角色编码
     */
    @GetMapping("/getRoleCodes")
    @ApiOperation(value = "获取用户角色编码")
    public ResponseModal getRoleCodes(@RequestParam(value = "userId", required = true) String userId) {
        ResponseModal rm = new ResponseModal();
        List<String> roleCodes = new ArrayList<>();
        // 查找当前用户的角色编码
        List<String> userRoleCodes = htBoaInUserRoleService.queryRoleCodes(userId);
        if (!userRoleCodes.isEmpty()) {
            userRoleCodes.forEach(userRoleCode -> {
                roleCodes.add(userRoleCode);
            });
        }

        // 查找当前用户岗位编码
        List<String> positionCodes = htBoaInPositionUserService.queryRoleCodes(userId);
        // 通过岗位编码查询关联的角色编码
        if (positionCodes != null && positionCodes.size() > 0) {
            List<String> userRoleCodesByPosition = htBoaInPositionRoleService.queryRoleCodesByPosition(positionCodes);
            if (!userRoleCodesByPosition.isEmpty()) {
                userRoleCodesByPosition.forEach(userRoleCode -> {
                    roleCodes.add(userRoleCode);
                });
            }
        }

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
        return htBoaInUserService.getUserListPage(new PageRequest(page.getPage(), page.getLimit()), page.getOrgCode(), page.getKeyWord(), page.getQuery());
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
            //TODO 需要获取登录信息，设置修改人
            user.setUpdateOperator("测试人");
            boolean isUpdate = htBoaInUserService.updateUserByUserId(user);
            if (isUpdate) {
                return Result.buildSuccess();
            }
        }
        return Result.buildFail();
    }

}
