package com.ht.ussp.uc.app.feignserver;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.DdUser;
import com.ht.ussp.uc.app.domain.HtBoaInBmUser;
import com.ht.ussp.uc.app.domain.HtBoaInContrast;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.domain.HtBoaInUserRole;
import com.ht.ussp.uc.app.feignclients.UaaClient;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.DingDingService;
import com.ht.ussp.uc.app.service.HtBoaInBmUserService;
import com.ht.ussp.uc.app.service.HtBoaInContrastService;
import com.ht.ussp.uc.app.service.HtBoaInLoginService;
import com.ht.ussp.uc.app.service.HtBoaInUserBusinessService;
import com.ht.ussp.uc.app.service.HtBoaInUserRoleService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;
import com.ht.ussp.uc.app.vo.BmUserVo;
import com.ht.ussp.uc.app.vo.LoginInfoVo;
import com.ht.ussp.uc.app.vo.UserMessageVo;
import com.ht.ussp.util.md5.DESC;
import com.ht.ussp.util.md5.DecodeResult;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

 
@RestController
@RequestMapping(value = "/userFeignServer")
@Log4j2
public class UserFeignServer{

    @Autowired
    private HtBoaInUserService htBoaInUserService;
    @Autowired
    private HtBoaInLoginService htBoaInLoginService;
    @Autowired
   	private HtBoaInContrastService htBoaInContrastService;
    @Autowired
    private UaaClient uaaClient;
    @Autowired
    private HtBoaInBmUserService htBoaInBmUserService;
    @Autowired
    private HtBoaInUserRoleService htBoaInUserRoleService;
    @Autowired
    private DingDingService dingDingService;
    @Autowired
    private HtBoaInUserBusinessService htBoaInUserBusinessService;
    
    
    /**
     * 保存信贷用户信息<br>
     */
    @ApiOperation(value = "获取网关token")
    @PostMapping(value = "/getUCToken")
    public Result getUCToken(@RequestParam("userInfo") String userInfo) {
    	try {
    		DecodeResult dResult = JSON.parseObject(userInfo,DecodeResult.class);
        	//解密参数
        	if(dResult==null) {
        		return Result.buildFail("9999", "解密参数出错");
        	}
        	if(StringUtils.isEmpty(dResult.getStrJson())) {
        		return Result.buildFail("9999", "请求参数JSON为空");
        	}
        	if(StringUtils.isEmpty(dResult.getKey())) {
        		return Result.buildFail("9999", "验证签名key为空");
        	}
        	
        	String reqBody=DESC.Decode(URLDecoder.decode(dResult.getStrJson(),"UTF-8"));
        	if(StringUtils.isEmpty(reqBody)) {
        		return Result.buildFail("9999", "请求参数为空");
        	}
        	//验证签名
        	String key = DESC.getMD5(String.format("Json:%s-Key:%s", dResult.getStrJson(), DESC.disturbKey));
        	if(!dResult.getKey().equals(key.toUpperCase())) {
        		return Result.buildFail("9999", "验证签名不匹配");
        	}
        	//解密请求参数
        	BmUserVo bmUserVo = JSON.parseObject(reqBody,BmUserVo.class);
        	if(bmUserVo==null) {
        		return Result.buildFail("9999", "解析请求参数json出错");
        	}
        	if(StringUtils.isEmpty(bmUserVo.getBmUserId())) {
        		return Result.buildFail("9999", "bmUserId不能为空");
        	}
        	if(StringUtils.isEmpty(bmUserVo.getEmail())) {
        		return Result.buildFail("9999", "email不能为空");
        	}
        	if(StringUtils.isEmpty(bmUserVo.getApp())) {
        		return Result.buildFail("9999", "app不能为空");
        	}
        	if(StringUtils.isEmpty(bmUserVo.getMobile())) {
        		return Result.buildFail("9999", "手机号不能为空");
        	}
        	HtBoaInContrast htBoaInContrast = htBoaInUserService.saveBmUserInfoByBmToken(bmUserVo);
			if (htBoaInContrast!=null) {
				ResponseModal result = uaaClient.createUCToken(htBoaInContrast.getUcBusinessId(), htBoaInContrast.getBmBusinessId(), 29, 25);
				return Result.buildSuccess(result);
			}else {
				return Result.buildFail("9999","未找到用户信息");
			}
		} catch (Exception e) {
			return Result.buildFail(e.getMessage(),e.getMessage());
		}
    }
    
    @ApiOperation(value = "修改用户状态 用户状态 0 正常  1禁用 2离职  4冻结 5锁定")
    @PostMapping("/changUserState")
    public Result changUserState(@RequestParam("bmUserId")String bmUserId,@RequestParam("status")String status) {
    	if(StringUtils.isEmpty(bmUserId)) {
    		return Result.buildFail();
    	} 
    	try {
    		List<HtBoaInContrast> listHtBoaInContrast = htBoaInContrastService.getHtBoaInContrastListByBmUserId(bmUserId, "20");
        	HtBoaInContrast htBoaInContrast  = null;
        	//查看是否存在用户，如果存在用户则不用新增用户
        	 if(listHtBoaInContrast!=null&&!listHtBoaInContrast.isEmpty()) {
        		 htBoaInContrast = listHtBoaInContrast.get(0);
        	 }
        	 if(htBoaInContrast==null) {
        		 return Result.buildFail("9999","未找到相关用户信息");
        	 }
        	HtBoaInUser htBoaInUser =  htBoaInUserService.findByUserId(htBoaInContrast.getUcBusinessId());
        	if(htBoaInUser!=null) {
        		htBoaInUser.setStatus(status);
        		htBoaInUserService.update(htBoaInUser);
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return Result.buildSuccess();
    }
    
    @ApiOperation(value = "获取指定userId用户信息",notes="userId为空并且bmUserId不为空，则根据信贷userId查询UC用户信息（userId：Uc用户id,bmUserId:信贷用户id,app:系统编码）")
    @GetMapping(value = "/getUserInfoByUserId")
    public Result getUserInfoByUserId(@RequestParam("userId")String userId, @RequestParam("bmUserId")String bmUserId, @RequestParam("app") String app) {
    	LoginInfoVo loginInfoVo = null;
    	if(StringUtils.isEmpty(userId)||userId.length()==0||"null".equals(userId)) {
    		List<HtBoaInContrast> listHtBoaInContrast= htBoaInContrastService.getHtBoaInContrastListByBmUserId(bmUserId,"20");
    		if(listHtBoaInContrast==null||listHtBoaInContrast.isEmpty()) {
    			if(!StringUtils.isEmpty(bmUserId)) {
        			if(loginInfoVo==null) {
        				List<HtBoaInBmUser> listHtBoaInBmUser = htBoaInBmUserService.getHtBoaInBmUserByUserId(bmUserId);
        				if(listHtBoaInBmUser!=null && !listHtBoaInBmUser.isEmpty()) {
        					loginInfoVo = new LoginInfoVo();
        					loginInfoVo.setBmOrgCode(listHtBoaInBmUser.get(0).getOrgCode());
        					loginInfoVo.setBmUserId(bmUserId);
        					loginInfoVo.setEmail(listHtBoaInBmUser.get(0).getEmail());
        					loginInfoVo.setJobNumber(listHtBoaInBmUser.get(0).getJobNumber());
        					loginInfoVo.setUserName(listHtBoaInBmUser.get(0).getUserName());
        					loginInfoVo.setMobile(listHtBoaInBmUser.get(0).getMobile());
        					try {//历史用户信息转存（方便贷后查询历史记录） 
        						HtBoaInUser u = htBoaInUserService.saveBmUserInfo(listHtBoaInBmUser.get(0));
        						if(u!=null) {
        							loginInfoVo.setUserId(u.getUserId());
        							loginInfoVo.setMobile(u.getMobile());
        						}
							} catch (Exception e) {
								 e.printStackTrace();
							}
        				}
        	    	}
        		}
    		}else {
    			userId=listHtBoaInContrast.get(0).getUcBusinessId();
    			loginInfoVo = htBoaInUserService.queryUserInfo(userId,app);
    		}
    	}else {
    		loginInfoVo = htBoaInUserService.queryUserInfo(userId,app);
    	}
    	if(loginInfoVo!=null) {
    		UserMessageVo userMessageVo = htBoaInUserBusinessService.getUserBusiByUserId(loginInfoVo.getUserId());
    		loginInfoVo.setBussinesOrgCode(userMessageVo.getBussinesOrgCode());
    		loginInfoVo.setBranchCode(userMessageVo.getBranchCode());
    		loginInfoVo.setDistrictCode(userMessageVo.getDistrictCode());
    		loginInfoVo.setProvince(userMessageVo.getProvince());
    		loginInfoVo.setCity(userMessageVo.getCity());
    	}
        return Result.buildSuccess(loginInfoVo);
    }
    
    @ApiOperation(value = "查询角色下所有用户", notes = "查询角色下所有用户")
	@PostMapping(value = "/getUserInfoForRole")
	public Result getUserInfoForRole(String roleCode,String keyword) {
		List<HtBoaInUser> listHtBoaInUser = new ArrayList<HtBoaInUser>();
		List<HtBoaInUserRole> listHtBoaInUserRole =  htBoaInUserRoleService.findByRoleCode(roleCode);
		for(HtBoaInUserRole u : listHtBoaInUserRole) {
			HtBoaInUser user = htBoaInUserService.findByUserId(u.getUserId());
			if(user!=null) {
				if(StringUtils.isEmpty(keyword)) {
					listHtBoaInUser.add(user);
				}else {
					boolean isAdd = false;
					if(!StringUtils.isEmpty(user.getUserName())) {
						if(user.getUserName().contains(keyword)) {
							isAdd = true;
						}
					}
					if(!StringUtils.isEmpty(user.getEmail())) {
						if(user.getEmail().contains(keyword)) {
							isAdd = true;
						}
					}
					if(!StringUtils.isEmpty(user.getJobNumber())) {
						if(user.getJobNumber().contains(keyword)) {
							isAdd = true;
						}
					}
					if(!StringUtils.isEmpty(user.getMobile())) {
						if(user.getMobile().contains(keyword)) {
							isAdd = true;
						}
					}
					if(isAdd){
						listHtBoaInUser.add(user);
					}
				}
			}else {
				htBoaInUserRoleService.delete(u);
			}
			
		}
		return Result.buildSuccess(listHtBoaInUser);
	}
    
    @ApiOperation(value = "获取指定业务机构下用户列表",notes="")
    @ApiImplicitParams({ @ApiImplicitParam(name = "busiOrgCode", paramType = "query",dataType = "String", required = true, value = "查询指定机构下用户"),
		@ApiImplicitParam(name = "isAllSub", paramType = "query",dataType = "String", required = true, value = "传值（1，2） 1:只当前机构下所有用户  2:包括机构下以及所有子机构用户") })
    @PostMapping(value = "/getUserListByBusiOrg")
    public Result getUserListByBusiOrg(@RequestParam("busiOrgCode")String busiOrgCode, @RequestParam("isAllSub")String isAllSub) {
    	List<UserMessageVo> listUserMessageVo = htBoaInUserBusinessService.getUserBusiListByBusiOrgCode(busiOrgCode,isAllSub);
        return Result.buildSuccess(listUserMessageVo);
    }
    
    @ApiOperation(value = "获取钉钉用户数据", notes = "获取钉钉用户数据，pms")
	@PostMapping(value = "/getAllDDUser")
	public Result getAllDDUser() { 
    	List<DdUser> listDDUser = dingDingService.getDDUserList();
    	return Result.buildSuccess(listDDUser);
    }
    
}
