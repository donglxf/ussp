package com.ht.ussp.uc.app.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.domain.HtBoaInUserApp;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.model.SysStatus;
import com.ht.ussp.uc.app.service.HtBoaInLoginService;
import com.ht.ussp.uc.app.service.HtBoaInUserAppService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;
import com.ht.ussp.uc.app.util.BeanUtils;
import com.ht.ussp.uc.app.util.LogicUtil;
import com.ht.ussp.uc.app.vo.UserVo;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * @ClassName: UserResource
 * @Description: TODO
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月8日 下午8:13:27
 */

@RestController
@RequestMapping(value = "/member")
public class UserResource {

	private static final Logger logger = LoggerFactory.getLogger(EchoResouce.class);

	@Autowired
	private HtBoaInUserService htBoaInUserService;

	@Autowired
	private HtBoaInUserAppService htBoaInUserAppService;
	
	@Autowired
	private HtBoaInLoginService htBoaInLoginService;
	

	/**
	 * 
	 * @Title: validateUser 
	 * @Description: 验证用户有效性 
	 * @return ResponseModal @throws
	 */
	
	@GetMapping("/validateUser")
	@ApiOperation( value = "验证用户")
	public ResponseModal validateUser(@RequestParam(value="app",required=true) String app, @RequestParam(value="userName",required=true) String userName) {
		ResponseModal rm = new ResponseModal();
		UserVo userVo=new UserVo();
		// 查找用户
		HtBoaInUser htBoaInUser = htBoaInUserService.findByUserName(userName);
		if (LogicUtil.isNull(htBoaInUser)||LogicUtil.isNullOrEmpty(htBoaInUser.getUserId())) {
			rm.setSysStatus((SysStatus.USER_NOT_FOUND));
			return rm;
		}else if(htBoaInUser.getDelFlag()==1){
			logger.info("该用户已被删除！");
			rm.setSysStatus(SysStatus.USER_HAS_DELETED);
		}else {
			BeanUtils.deepCopy(htBoaInUser, userVo);
		}
		
		// 验证用户与系统是否匹配
		HtBoaInUserApp htBoaInUserApp = htBoaInUserAppService.findUserAndAppInfo(htBoaInUser.getUserId());
		if (LogicUtil.isNull(htBoaInUserApp) || LogicUtil.isNullOrEmpty(htBoaInUserApp.getApp())) {
			rm.setSysStatus(SysStatus.USER_NOT_RELATE_APP);
			return rm;
		} else if (app.equals(htBoaInUserApp.getApp())) {
			logger.info("用户与系统匹配正确！");
			BeanUtils.deepCopy(htBoaInUserApp, userVo);
		} else {
			logger.info("用户来源不正确！");
			rm.setSysStatus(SysStatus.USER_NOT_MATCH_APP);
			return rm;
		}
		//获取用户登录信息
		HtBoaInLogin htBoaInLogin=htBoaInLoginService.findByUserId(htBoaInUser.getUserId());
		if(LogicUtil.isNotNull((htBoaInUserApp))){
			BeanUtils.deepCopy(htBoaInLogin, userVo);
		}
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
	 */
	@GetMapping("/getRoleCodes")
	@ApiOperation( value = "获取用户角色编码")
	public ResponseModal getRoleCodes(@RequestParam(value="userId",required=true) String userId) {
		ResponseModal rm = new ResponseModal();
		
		
		
		
		return rm;
	}
}
