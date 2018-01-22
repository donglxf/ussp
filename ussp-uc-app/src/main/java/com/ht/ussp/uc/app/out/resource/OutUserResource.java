package com.ht.ussp.uc.app.out.resource;

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

import com.ht.ussp.uc.app.domain.HtBoaOutRole;
import com.ht.ussp.uc.app.domain.HtBoaOutUser;
import com.ht.ussp.uc.app.domain.HtBoaOutUserRole;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.model.SelfBoaOutUserInfo;
import com.ht.ussp.uc.app.service.HtBoaOutRoleService;
import com.ht.ussp.uc.app.service.HtBoaOutUserRoleService;
import com.ht.ussp.uc.app.service.HtBoaOutUserService;
import com.ht.ussp.util.BeanUtils;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @ClassName: HtBoaOutUserResource
 * @Description: 登录信息
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月10日 下午14:44:16
 */

@RestController
@RequestMapping(value = "/user")
public class OutUserResource {

    private static final Logger log = LoggerFactory
            .getLogger(OutUserResource.class);

    @Autowired
    private HtBoaOutUserService htBoaOutUserService;
    @Autowired
    private HtBoaOutRoleService htBoaOutRoleService;
    @Autowired
    private HtBoaOutUserRoleService htBoaOutUserRoleService;

    @ApiOperation(value = "对外：用户个人信息查询", notes = "已登录用户查看自己的个人信息")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "path", dataType = "int")
    @RequestMapping(value = {
            "/out/selfinfo/{userId}" }, method = RequestMethod.GET)
    public ResponseModal getSelfInfo(@PathVariable String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "用户个人信息查询：user/out/selfinfo param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "userId: " + userId, sl);
        SelfBoaOutUserInfo u = new SelfBoaOutUserInfo();
        u.setUserId(userId);
        List<SelfBoaOutUserInfo> selfUserInfoList = htBoaOutUserService.findAll(u);
        r = exceptionReturn(logEnd, "userId: " + userId, selfUserInfoList, sl, "个人用户信息", 1);
        if (null != r)
            return r;
        el = System.currentTimeMillis();
        log.info(logEnd, "userId: " + userId, msg, el, el - sl);
        return new ResponseModal(200, msg, selfUserInfoList.get(0));
    }

    @ApiOperation(value = "对外：用户个人信息修改", notes = "已登录用户修改自己的个人信息")
    @ApiImplicitParam(name = "selfBoaOutUserInfo", value = "用户个人信息实体", required = true, dataType = "SelfBoaOutUserInfo")
    @RequestMapping(value = { "/out/selfinfo/set" }, method = RequestMethod.POST)
    public ResponseModal setSelfInfo(@RequestBody SelfBoaOutUserInfo selfBoaOutUserInfo) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "用户个人信息修改：user/out/selfinfo/set param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "selfBoaOutUserInfo: " + selfBoaOutUserInfo, sl);
        HtBoaOutUser u = new HtBoaOutUser();
        BeanUtils.setObjectFieldsEmpty(u);
        u.setUserId(selfBoaOutUserInfo.getUserId());
        List<HtBoaOutUser> htBoaOutUserList = htBoaOutUserService.findAll(u);
        r = exceptionReturn(logEnd, "selfBoaOutUserInfo: " + selfBoaOutUserInfo,
                htBoaOutUserList, sl, "个人用户信息", 1);
        if (null != r)
            return r;
        u = htBoaOutUserList.get(0);
        List<HtBoaOutRole> htBoaOutRoleList = htBoaOutRoleService
                .findByRoleCodeIn(selfBoaOutUserInfo.getRoleCodes());
        r = exceptionReturn(logEnd, "selfUserInfo: " + selfBoaOutUserInfo,
                htBoaOutRoleList, sl, "角色信息", selfBoaOutUserInfo.getRoleCodes().size());
        if (null != r)
            return r;
        u.setEmail(selfBoaOutUserInfo.getEmail());
        u.setIdNo(selfBoaOutUserInfo.getIdNo());
        u.setMobile(selfBoaOutUserInfo.getMobile());
        u.setUserName(selfBoaOutUserInfo.getUserName());
        HtBoaOutUserRole htBoaOutUserRole = new HtBoaOutUserRole();
        htBoaOutUserRole.setUserId(u.getUserId());
        htBoaOutUserRoleService.delete(htBoaOutUserRole);
        List<HtBoaOutUserRole> htBoaOutUserRoleList = new ArrayList<HtBoaOutUserRole>();
        for (String roleCode : selfBoaOutUserInfo.getRoleCodes()) {
            HtBoaOutUserRole t = new HtBoaOutUserRole();
            t.setUserId(u.getUserId());
            t.setRoleCode(roleCode);
            htBoaOutUserRoleList.add(t);
        }
        if (!htBoaOutUserRoleList.isEmpty())
            htBoaOutUserRoleService.add(htBoaOutUserRoleList);
        SelfBoaOutUserInfo s = new SelfBoaOutUserInfo();
        u.setUserId(u.getUserId());
        List<SelfBoaOutUserInfo> selfUserInfoList = htBoaOutUserService.findAll(s);
        r = exceptionReturn(logEnd, "selfBoaOutUserInfo: " + selfBoaOutUserInfo,
                selfUserInfoList, sl, "个人用户信息", 1);
        if (null != r)
            return r;
        el = System.currentTimeMillis();
        log.info(logEnd, "selfUserInfo: " + selfBoaOutUserInfo, msg, el, el - sl);
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
