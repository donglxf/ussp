package com.ht.ussp.uc.app.resource;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.ht.ussp.common.Constants;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInOperatorLog;
import com.ht.ussp.uc.app.domain.HtBoaInPwdHist;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResetPwd;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInLoginService;
import com.ht.ussp.uc.app.service.HtBoaInOperatorLogService;
import com.ht.ussp.uc.app.service.HtBoaInPwdHistService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * @ClassName: HtBoaInLoginResource
 * @Description: 登录信息
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月9日 上午10:16:45
 */

@RestController
@RequestMapping(value = "/login")
public class LoginResource {

    private static final Logger log = LoggerFactory .getLogger(LoginResource.class);

    @Autowired
    private HtBoaInUserService htBoaInUserService;
    @Autowired
    private HtBoaInLoginService htBoaInLoginService;
    @Autowired
    private HtBoaInPwdHistService htBoaInPwdHistService;
    @Autowired
    private HtBoaInOperatorLogService htBoaInOperatorLogService;

    @SuppressWarnings("unchecked")
    @ApiOperation(value = "对内：忘记密码/重置密码", notes = "用户通过用户的手机号、邮箱和历史密码信息进行密码重置")
    @RequestMapping(value = { "/in/resetpwd" }, method = RequestMethod.POST)
    public ResponseModal resetPwd(@RequestBody ResetPwd resetPwd) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "忘记密码/重置密码：login/in/resetpwd param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, resetPwd.toString(), sl);
        HtBoaInUser htBoaInUser = new HtBoaInUser();
        htBoaInUser.setMobile(resetPwd.getMobile());
        htBoaInUser.setEmail(resetPwd.getEmail());
        List<HtBoaInUser> htBoaInUserList = htBoaInUserService .findAll(htBoaInUser);
        r = exceptionReturn(logEnd, "resetPwd: " + resetPwd, htBoaInUserList, sl, "个人用户信息", 1);
        if (null != r)
            return r;
        htBoaInUser = htBoaInUserList.get(0);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", htBoaInUser.getUserId());
        PageConf pageConf = new PageConf();
        pageConf.setPage(0);
        pageConf.setSize(resetPwd.getHistPwds().size());
        pageConf.setSortNames(Arrays.asList("pwdCreTime"));
        pageConf.setSortOrders(Arrays.asList(Direction.DESC));
        Page<HtBoaInPwdHist> htBoaInPwdHistList = (Page<HtBoaInPwdHist>) htBoaInPwdHistService .findAllByPage(pageConf, map);
        r = exceptionReturn(logEnd, "resetPwd: " + resetPwd, htBoaInPwdHistList.getContent(), sl, "用户历史密码信息", resetPwd.getHistPwds().size());
        if (null != r)
            return r;
        HtBoaInLogin u = new HtBoaInLogin();
        u.setUserId(htBoaInUser.getUserId());
        List<HtBoaInLogin> htBoaInLoginList = htBoaInLoginService.findAll(u);
        r = exceptionReturn(logEnd, "resetPwd: " + resetPwd, htBoaInLoginList,  sl, "用户登录信息", 1);
        if (null != r)
            return r;
        u = htBoaInLoginList.get(0);
        u.setPassword(resetPwd.getNewPwd());
        HtBoaInPwdHist htBoaInPwdHist = new HtBoaInPwdHist();
        htBoaInPwdHist.setUserId(u.getUserId());
        htBoaInPwdHist.setPassword(resetPwd.getNewPwd());
        htBoaInPwdHist.setPwdCreTime(new Timestamp(System.currentTimeMillis()));
        htBoaInLoginService.update(u);
        htBoaInPwdHistService.add(htBoaInPwdHist);
        el = System.currentTimeMillis();
        log.debug(logEnd, "resetPwd: " + resetPwd, msg, el, el - sl);
        return new ResponseModal("200", "成功");
    }

    protected ResponseModal exceptionReturn(String logEnd, String param,
            List<?> list, long sl, String exInfo, int row) {
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

    @GetMapping("/updateFailCount")
    @ApiOperation(value = "登录成功，更新用户登录信息")
    public void updateFailCount(@RequestParam("userId")String userId, @RequestParam("failedCount")Integer failedCount,@RequestParam("app") String app) {
    	if(StringUtils.isEmpty(userId)) {
    		return;
    	} 
    	try {
    		HtBoaInLogin htBoaInLogin = htBoaInLoginService.findByUserId(userId);
        	if(htBoaInLogin!=null) {
        		htBoaInLogin.setFailedCount(failedCount);
        		if(failedCount>20) {
        			htBoaInLogin.setStatus(Constants.USER_STATUS_5);
        		}
        		htBoaInLoginService.update(htBoaInLogin);
        		HtBoaInOperatorLog u = new HtBoaInOperatorLog();
        		u.setActionName("login");
        		u.setActionTime(new Date());
        		u.setApp(app);
    			u.setResult(failedCount+"");
    			if(failedCount>0) {
    			   u.setResult(1+"");
    			}
        		u.setUserId(userId);
        		htBoaInOperatorLogService.add(u);
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
    @ApiOperation(value = "修改用户登录状态")
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
