package com.ht.ussp.uc.app.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.vo.Page;
import com.ht.ussp.uc.app.vo.UserMessageVo;
import com.ht.ussp.util.DtoUtil;
import com.ht.ussp.util.EncryptUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.domain.HtBoaInUserApp;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.model.SysStatus;
import com.ht.ussp.uc.app.service.HtBoaInLoginService;
import com.ht.ussp.uc.app.service.HtBoaInPositionRoleService;
import com.ht.ussp.uc.app.service.HtBoaInPositionUserService;
import com.ht.ussp.uc.app.service.HtBoaInUserAppService;
import com.ht.ussp.uc.app.service.HtBoaInUserRoleService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;
import com.ht.ussp.uc.app.util.BeanUtils;
import com.ht.ussp.uc.app.util.LogicUtil;
import com.ht.ussp.uc.app.vo.UserVo;

import io.swagger.annotations.ApiOperation;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: UserResource
 * @Description: TODO
 * @date 2018年1月8日 下午8:13:27
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/member")
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
    private HtBoaInPositionUserService htBoaInPositionUserService;

    @Autowired
    private HtBoaInPositionRoleService htBoaInPositionRoleService;

    /**
     * @return ResponseModal @throws
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
        // 通过岗位编码查用关联的角色编码
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
    public PageResult<List<UserMessageVo>> loadListByPage(Page page) {
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
