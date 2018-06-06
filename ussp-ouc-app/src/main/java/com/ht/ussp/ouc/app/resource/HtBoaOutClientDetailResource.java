package com.ht.ussp.ouc.app.resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.common.SysStatus;
import com.ht.ussp.core.Result;
import com.ht.ussp.ouc.app.domain.HtBoaOutClientDetail;
import com.ht.ussp.ouc.app.service.HtBoaOutClientDetailService;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

/**
 * 
 * @ClassName: HtBoaOutClientDetailResource
 * @Description: 客户端信息
 * @author wim qiuwenwu@hongte.info
 * @date 2018年5月16日 下午7:53:58
 */
@Log4j2
@RestController
@RequestMapping(value = "/client")
public class HtBoaOutClientDetailResource {
	
	@Autowired
	private HtBoaOutClientDetailService htBoaOutClientDetailService;

	@ApiOperation(value = "获取客户端信息")
	@GetMapping("/getClientInfo")
	public Result saveSmsToRedis(@RequestParam("app") String app){
		if(StringUtils.isBlank(app)) {
			return Result.buildFail(SysStatus.ERROR_PARAM.getStatus(), SysStatus.ERROR_PARAM.getMsg());
		}
		HtBoaOutClientDetail htBoaOutClientDetail=htBoaOutClientDetailService.findByAppCode(app);
		if(null==htBoaOutClientDetail) {
			return Result.buildFail(SysStatus.NO_RESULT.getStatus(),SysStatus.NO_RESULT.getMsg());
		}
		return Result.buildSuccess(htBoaOutClientDetail);		
		
	}

}
