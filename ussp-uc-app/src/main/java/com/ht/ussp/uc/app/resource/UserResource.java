package com.ht.ussp.uc.app.resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.model.SysStatus;
import com.ht.ussp.uc.app.service.HtBoaInUserService;

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

	private static final Logger log = LoggerFactory.getLogger(EchoResouce.class);

	@Autowired
	private HtBoaInUserService htBoaInUserService;

	@GetMapping("/validateUser")
	@ApiOperation(value = "验证用户")
	public ResponseModal validateUser(String app, String userName) {
		ResponseModal rm = new ResponseModal();
		HtBoaInUser htBoaInUser = htBoaInUserService.findByUserName(userName);
		if (null == htBoaInUser&&StringUtils.isBlank(htBoaInUser.getUserId())) {
			rm.setSysStatus((SysStatus.USER_NOT_FOUND));
			return rm;
		}
		
		rm.setSysStatus(SysStatus.SUCCESS);
		rm.setResult(htBoaInUser);
		return rm;
	}
}
