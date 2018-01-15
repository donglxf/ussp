package com.ht.ussp.uc.app.resource;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.domain.HtBoaInPosition;
import com.ht.ussp.uc.app.domain.HtBoaInPositionUser;
import com.ht.ussp.uc.app.domain.HtBoaInRole;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.domain.HtBoaInUserRole;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.model.SelfBoaInUserInfo;
import com.ht.ussp.uc.app.service.HtBoaInOrgService;
import com.ht.ussp.uc.app.service.HtBoaInPositionService;
import com.ht.ussp.uc.app.service.HtBoaInPositionUserService;
import com.ht.ussp.uc.app.service.HtBoaInRoleService;
import com.ht.ussp.uc.app.service.HtBoaInUserRoleService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;
import com.ht.ussp.uc.app.util.BeanUtils;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @ClassName: HtBoaInUserResource
 * @Description: 登录信息
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月10日 下午14:44:16
 */

@RestController
@RequestMapping(value = "/user")
public class HtBoaInUserResource {

    private static final Logger log = LoggerFactory
            .getLogger(HtBoaInUserResource.class);

    @Autowired
    private HtBoaInUserService htBoaInUserService;
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

    @ApiOperation(value = "对内：用户个人信息查询", notes = "已登录用户查看自己的个人信息")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "path", dataType = "int")
    @RequestMapping(value = {
            "/in/selfinfo/{userId}" }, method = RequestMethod.GET)
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
    @RequestMapping(value = { "/in/selfinfo/set" }, method = RequestMethod.POST)
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

}
