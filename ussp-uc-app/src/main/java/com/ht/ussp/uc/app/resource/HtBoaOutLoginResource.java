package com.ht.ussp.uc.app.resource;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.uc.app.domain.HtBoaOutLogin;
import com.ht.ussp.uc.app.domain.HtBoaOutPwdHist;
import com.ht.ussp.uc.app.domain.HtBoaOutUser;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResetPwd;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaOutLoginService;
import com.ht.ussp.uc.app.service.HtBoaOutPwdHistService;
import com.ht.ussp.uc.app.service.HtBoaOutUserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @ClassName: HtBoaOutLoginResource
 * @Description: 登录信息
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月9日 上午10:16:45
 */

@RestController
@RequestMapping(value = "/login")
public class HtBoaOutLoginResource {

    private static final Logger log = LoggerFactory
            .getLogger(HtBoaOutLoginResource.class);

    @Autowired
    private HtBoaOutUserService htBoaOutUserService;
    @Autowired
    private HtBoaOutLoginService htBoaOutLoginService;
    @Autowired
    private HtBoaOutPwdHistService htBoaOutPwdHistService;

    @SuppressWarnings("unchecked")
    @ApiOperation(value = "对外：忘记密码/重置密码", notes = "用户通过用户的手机号、邮箱和历史密码信息进行密码重置")
    @ApiImplicitParam(name = "resetPwd", value = "重置密码信息验证实体", required = true, dataType = "resetPwd")
    @RequestMapping(value = { "/out/resetpwd" }, method = RequestMethod.POST)
    public ResponseModal resetPwd(@RequestBody ResetPwd resetPwd) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "忘记密码/重置密码：login/in/resetpwd param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, resetPwd.toString(), sl);
        HtBoaOutUser htBoaOutUser = new HtBoaOutUser();
        htBoaOutUser.setMobile(resetPwd.getMobile());
        htBoaOutUser.setEmail(resetPwd.getEmail());
        List<HtBoaOutUser> htBoaOutUserList = htBoaOutUserService
                .findAll(htBoaOutUser);
        r = exceptionReturn(logEnd, "resetPwd: " + resetPwd, htBoaOutUserList,
                sl, "个人用户信息", 1);
        if (null != r)
            return r;
        htBoaOutUser = htBoaOutUserList.get(0);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", htBoaOutUser.getUserId());
        PageConf pageConf = new PageConf();
        pageConf.setPage(0);
        pageConf.setSize(resetPwd.getHistPwds().size());
        pageConf.setSortNames(Arrays.asList("pwdCreTime"));
        pageConf.setSortOrders(Arrays.asList(Direction.DESC));
        Page<HtBoaOutPwdHist> htBoaOutPwdHistList = (Page<HtBoaOutPwdHist>) htBoaOutPwdHistService
                .findAllByPage(pageConf, map);
        r = exceptionReturn(logEnd, "resetPwd: " + resetPwd,
                htBoaOutPwdHistList.getContent(), sl, "用户历史密码信息",
                resetPwd.getHistPwds().size());
        if (null != r)
            return r;
        HtBoaOutLogin u = new HtBoaOutLogin();
        u.setUserId(htBoaOutUser.getUserId());
        List<HtBoaOutLogin> htBoaOutLoginList = htBoaOutLoginService.findAll(u);
        r = exceptionReturn(logEnd, "resetPwd: " + resetPwd, htBoaOutLoginList,
                sl, "用户登录信息", 1);
        if (null != r)
            return r;
        u = htBoaOutLoginList.get(0);
        u.setPassword(resetPwd.getNewPwd());
        HtBoaOutPwdHist htBoaOutPwdHist = new HtBoaOutPwdHist();
        htBoaOutPwdHist.setUserId(u.getUserId());
        htBoaOutPwdHist.setPassword(resetPwd.getNewPwd());
        htBoaOutPwdHist
                .setPwdCreTime(new Timestamp(System.currentTimeMillis()));
        htBoaOutLoginService.update(u);
        htBoaOutPwdHistService.add(htBoaOutPwdHist);
        el = System.currentTimeMillis();
        log.info(logEnd, "resetPwd: " + resetPwd, msg, el, el - sl);
        return new ResponseModal(200, "成功");
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
