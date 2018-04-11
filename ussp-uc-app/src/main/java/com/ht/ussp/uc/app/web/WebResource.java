package com.ht.ussp.uc.app.web;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.ht.ussp.common.SysStatus;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.domain.HtBoaInPwdHist;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.feignclients.UaaClient;
import com.ht.ussp.uc.app.model.ChangePwd;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.model.SelfBoaInUserInfo;
import com.ht.ussp.uc.app.service.HtBoaInLoginService;
import com.ht.ussp.uc.app.service.HtBoaInOrgService;
import com.ht.ussp.uc.app.service.HtBoaInPwdHistService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;
import com.ht.ussp.uc.app.vo.UserMessageVo;
import com.ht.ussp.uc.app.vo.ValidateJwtVo;
import com.ht.ussp.util.EncryptUtil;
import com.ht.ussp.util.FastJsonUtil;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

 
/**
 * 不校验token
 * @author tangxs
 *
 */
@RestController
@RequestMapping(value = "/web")
@Log4j2
public class WebResource{

    @Autowired
    private HtBoaInUserService htBoaInUserService;
    @Autowired
    private HtBoaInLoginService htBoaInLoginService;
    @Autowired
    private HtBoaInPwdHistService htBoaInPwdHistService;
    @Autowired
    private UaaClient uaaClient;
    @Autowired
    private HtBoaInOrgService htBoaInOrgService;
    
    @ApiOperation(value = "外部系统获取用户信息")
    @PostMapping("/getUserForOther")
    public Result getUserForOther(String token) {
    	 ResponseModal rm = uaaClient.validateJwt("Bearer "+token);
         ValidateJwtVo vdj = new ValidateJwtVo();
         vdj = FastJsonUtil.objectToPojo(rm.getResult(), ValidateJwtVo.class);
         if(vdj==null) {
         	return Result.buildFail();
         }
         if(!StringUtils.isEmpty(vdj.getUserId())) {
        	 HtBoaInUser htBoaInUser = htBoaInUserService.findByUserId(vdj.getUserId());
        	 UserMessageVo userMessageVo = new UserMessageVo();
        	 userMessageVo.setUserId(htBoaInUser.getUserId());
        	 userMessageVo.setUserName(htBoaInUser.getUserName());
        	 userMessageVo.setId(htBoaInUser.getId());
        	 userMessageVo.setEmail(htBoaInUser.getEmail());
        	 userMessageVo.setMobile(htBoaInUser.getMobile());
        	 userMessageVo.setOrgCode(htBoaInUser.getOrgCode());
        	 userMessageVo.setJobNumber(htBoaInUser.getJobNumber());
        	 List<HtBoaInOrg> listOrg = htBoaInOrgService.findByOrgCode(htBoaInUser.getOrgCode());
        	 if(listOrg!=null&&!listOrg.isEmpty()) {
        		 userMessageVo.setOrgName(listOrg.get(0).getOrgNameCn());
        	 }
        	 HtBoaInLogin htBoaInLogin = htBoaInLoginService.findByUserId(htBoaInUser.getUserId());
        	 if(htBoaInLogin!=null) {
        		 userMessageVo.setLoginId(htBoaInLogin.getLoginId());
        	 }
        	 return Result.buildSuccess(userMessageVo);
         }
        return Result.buildFail(rm.getStatus_code(), rm.getResult_msg());
    }
    
    @ApiOperation(value = "外部系统更新用户信息")
    @PostMapping("/updateUserForOther")
    public Result updateUserForOther(@RequestParam("token")  String token,@RequestBody SelfBoaInUserInfo selfBoaInUserInfo) {
    	 ResponseModal rm = uaaClient.validateJwt(token);
         ValidateJwtVo vdj = (ValidateJwtVo) rm.getResult();
         
         if(StringUtils.isEmpty(selfBoaInUserInfo.getUserId())) {
        	 return Result.buildFail();
         }
         if(!selfBoaInUserInfo.getUserId().equals(vdj.getUserId())) {
        	 return Result.buildFail();
         }
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
         
         return Result.buildSuccess(u1);
    }
    
    
    @ApiOperation(value = "修改密码")
    @RequestMapping(value = {"/changePwd"}, method = RequestMethod.POST)
    public Result changePwd(@RequestBody ChangePwd changePwd) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "修改密码：changePwd param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, changePwd.toString(), sl);
        
        ResponseModal rm = uaaClient.validateJwt("Bearer "+changePwd.getToken());
        ValidateJwtVo vdj = new ValidateJwtVo();
        vdj = FastJsonUtil.objectToPojo(rm.getResult(), ValidateJwtVo.class);
        if(vdj==null) {
        	return Result.buildFail();
        }
        HtBoaInLogin u = htBoaInLoginService.findByUserId(vdj.getUserId());
        //验证原密码是否正确
        if(!EncryptUtil.matches(changePwd.getOldPwd(),u.getPassword())) {
        	return Result.buildFail(SysStatus.PWD_INVALID.getStatus(),"原密码输入不正确");
        }
        
        //记录历史密码
        HtBoaInPwdHist htBoaInPwdHist = new HtBoaInPwdHist();
        htBoaInPwdHist.setUserId(u.getUserId());
        htBoaInPwdHist.setPassword(u.getPassword());
        htBoaInPwdHist.setPwdCreTime(new Timestamp(System.currentTimeMillis()));
        htBoaInPwdHist.setLastModifiedDatetime(new Date());
        
        String newPassWordEncrypt = EncryptUtil.passwordEncrypt(changePwd.getNewPwd());
        u.setPassword(newPassWordEncrypt);
        htBoaInLoginService.update(u);
        
        htBoaInPwdHistService.add(htBoaInPwdHist);
        el = System.currentTimeMillis();
        log.debug(logEnd, "resetPwd: " + changePwd, msg, el, el - sl);
        return Result.buildSuccess();
    }
    
}
