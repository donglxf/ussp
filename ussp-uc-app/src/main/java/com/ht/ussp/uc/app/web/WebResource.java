package com.ht.ussp.uc.app.web;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.StringUtils;
import com.ht.ussp.common.SysStatus;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.domain.HtBoaInPublish;
import com.ht.ussp.uc.app.domain.HtBoaInPwdHist;
import com.ht.ussp.uc.app.domain.HtBoaInResource;
import com.ht.ussp.uc.app.domain.HtBoaInRole;
import com.ht.ussp.uc.app.domain.HtBoaInRoleRes;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.feignclients.UaaClient;
import com.ht.ussp.uc.app.model.ChangePwd;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.model.SelfBoaInUserInfo;
import com.ht.ussp.uc.app.service.HtBoaInLoginService;
import com.ht.ussp.uc.app.service.HtBoaInOrgService;
import com.ht.ussp.uc.app.service.HtBoaInPublishService;
import com.ht.ussp.uc.app.service.HtBoaInPwdHistService;
import com.ht.ussp.uc.app.service.HtBoaInResourceService;
import com.ht.ussp.uc.app.service.HtBoaInRoleResService;
import com.ht.ussp.uc.app.service.HtBoaInRoleService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;
import com.ht.ussp.uc.app.vo.UserMessageVo;
import com.ht.ussp.uc.app.vo.ValidateJwtVo;
import com.ht.ussp.util.EncryptUtil;
import com.ht.ussp.util.FastJsonUtil;
import com.ht.ussp.util.JsonUtil;

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
    private HtBoaInResourceService htBoaInResourceService;
    @Autowired
    private UaaClient uaaClient;
    @Autowired
    private HtBoaInOrgService htBoaInOrgService;
    @Autowired
    private HtBoaInPublishService htBoaInPublishService;
    
    @Autowired
    private HtBoaInRoleService htBoaInRoleService;
    @Autowired
    private HtBoaInRoleResService htBoaInRoleResService;
    
    
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
    	 ResponseModal rm = uaaClient.validateJwt("Bearer "+token);
    	 ValidateJwtVo vdj = new ValidateJwtVo();
         vdj = FastJsonUtil.objectToPojo(rm.getResult(), ValidateJwtVo.class);
         
         if(StringUtils.isEmpty(selfBoaInUserInfo.getUserId())) {
        	 return Result.buildFail();
         }
         if(!selfBoaInUserInfo.getUserId().equals(vdj.getUserId())) {
        	 return Result.buildFail();
         }
         HtBoaInUser u = htBoaInUserService.findByUserId(selfBoaInUserInfo.getUserId());
         if(u==null) {
        	 return Result.buildFail();
         }
         if (selfBoaInUserInfo.getOrgCode() != null && "" != selfBoaInUserInfo.getOrgCode()) {
             u.setOrgCode(selfBoaInUserInfo.getOrgCode());
         }
         if (selfBoaInUserInfo.getRootOrgCode() != null && "" != selfBoaInUserInfo.getRootOrgCode()) {
             u.setRootOrgCode(selfBoaInUserInfo.getRootOrgCode());
         }
         if(!StringUtils.isEmpty(selfBoaInUserInfo.getEmail())) {
        	 if(!checkUserExist(null, null, selfBoaInUserInfo.getEmail(), null, u.getUserId())) {
        		 return Result.buildFail("email已经存在","email已经存在");
        	 }
         }
		 if(!StringUtils.isEmpty(selfBoaInUserInfo.getMobile())) {
			 if(!checkUserExist(null, selfBoaInUserInfo.getMobile(), null, null, u.getUserId())) {
        		 return Result.buildFail("手机号已经存在","手机号已经存在");
        	 } 
		 }
         u.setEmail(selfBoaInUserInfo.getEmail());
         u.setMobile(selfBoaInUserInfo.getMobile());
         u.setUserName(selfBoaInUserInfo.getUserName());
         htBoaInUserService.update(u);

         HtBoaInUser u1 = htBoaInUserService.findByUserId(selfBoaInUserInfo.getUserId());
         
         return Result.buildSuccess(u1);
    }
    
    public boolean checkUserExist(String jobnum,String mobile,String email,String loginid,String userId) {
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
    	       return true;
            }else {
            	userId = userId==null?"":userId;
            	if(userId.equals(htBoaInLogin.getUserId())) {
            		return true;
            	}else {
            		return false;
            	}
            }
    	}
		if(htBoaInUser==null) {
			return true;
        }else {
        	userId = userId==null?"":userId;
        	if(userId.equals(htBoaInUser.getUserId())) {
        		return true;
        	}else {
        		return false;
        	}
        }
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
        if("1".equals(u.getStatus())) {
    		u.setStatus("0");
    	}
        htBoaInLoginService.update(u);
        htBoaInPwdHistService.add(htBoaInPwdHist);
        el = System.currentTimeMillis();
        log.debug(logEnd, "resetPwd: " + changePwd, msg, el, el - sl);
        return Result.buildSuccess();
    }
    
    @GetMapping("/exportResource")
    public void exportResource(HttpServletRequest request, HttpServletResponse response,@RequestParam("app")  String app ,@RequestParam("reqType") String reqType) {
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		// 导出txt文件
		response.setContentType("text/plain");
		String fileName =app+"_source_data.json";
		try {
			response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8") );// 导出中文名称
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		BufferedOutputStream buff = null;
		StringBuffer write = new StringBuffer();
		ServletOutputStream outSTr = null;
		try {
			if(StringUtils.isEmpty(reqType)) {
				return ;
			}
			String[] reqTypes = reqType.split(",");
			if(reqTypes!=null&&reqTypes.length>0) {
				outSTr = response.getOutputStream();// 建立
				buff = new BufferedOutputStream(outSTr);
				Map<String,String> map = new HashMap<String,String>();
				for(String reqtype : reqTypes) {
					if("resource".equals(reqtype)) {
						List<HtBoaInResource> listHtBoaInResource=htBoaInResourceService.getAllByApp(app);
						map.put("resource", JsonUtil.obj2Str(listHtBoaInResource));
					}else if("role".equals(reqtype)) {
						List<HtBoaInRole> listHtBoaInRole=htBoaInRoleService.getAllByApp(app);
						map.put("role", JsonUtil.obj2Str(listHtBoaInRole));
					}else if("role_resource".equals(reqtype)) {
						List<HtBoaInRoleRes> listHtBoaInResource=htBoaInRoleResService.getAllByApp(app);
						map.put("roleResource", JsonUtil.obj2Str(listHtBoaInResource));
					}
				}
				write.append(JSONUtils.toJSONString(map));
				buff.write(write.toString().getBytes("UTF-8"));
				buff.flush();
				buff.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				buff.close();
				outSTr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
    
    /**
     * 
     * @param request
     * @param response
     * @param type 1 升级脚本  2 回滚脚本
     * @param resultDataCode 数据解析版本
     */
    @GetMapping("/downAnalysisResData")
    public void downAnalysisResData(HttpServletRequest request, HttpServletResponse response,@RequestParam("type")  String type,@RequestParam("resultDataCode")  String resultDataCode) {
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		List<HtBoaInPublish> listHtBoaInPublish = htBoaInPublishService.getHtBoaInPublishListByPublishCode(resultDataCode);
		HtBoaInPublish htBoaInPublish = new HtBoaInPublish();
		if(listHtBoaInPublish!=null&&!listHtBoaInPublish.isEmpty()) {
			htBoaInPublish = listHtBoaInPublish.get(0);
		}
		if(htBoaInPublish==null) {
			return;
		}
		String publishSql = htBoaInPublish.getPublishSql();
		String fallBackSql = htBoaInPublish.getFallBackSql();
		String fileName = resultDataCode;
		if(StringUtils.isEmpty(type)) {
			return ;
		}else {
			if("1".equals(type)) {
				fileName = htBoaInPublish.getPublishFileName();
			}else if("2".equals(type)) {
				fileName = htBoaInPublish.getFallBackFileName();
			}
		}
		// 导出txt文件
		response.setContentType("text/plain");
		try {
			response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8") );// 导出中文名称
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		BufferedOutputStream buff = null;
		StringBuffer write = new StringBuffer();
		ServletOutputStream outSTr = null;
		try {
			outSTr = response.getOutputStream();// 建立
			buff = new BufferedOutputStream(outSTr);
			if("1".equals(type)) {
				write.append(publishSql);
			}else if("2".equals(type)) {
				write.append(fallBackSql);
			}
			buff.write(write.toString().getBytes("UTF-8"));
			buff.flush();
			buff.close();
			if("1".equals(type)) {
				htBoaInPublish.setIsdown("1");
				htBoaInPublish.setUpdateDatetime(new Date());
				htBoaInPublishService.save(htBoaInPublish);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				buff.close();
				outSTr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
