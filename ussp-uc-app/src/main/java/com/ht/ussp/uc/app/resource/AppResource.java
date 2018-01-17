package com.ht.ussp.uc.app.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.uc.app.domain.HtBoaInApp;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInAppService;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * @ClassName: HtBoaInAppResource
 * @Description: 系统信息
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月5日 下午6:36:47
 */

@RestController
@RequestMapping(value = "/system")
public class AppResource {
	
	private static final Logger log = LoggerFactory.getLogger(EchoResouce.class);
	
	@Autowired 
	private HtBoaInAppService htBoaInAppService;

	@ApiOperation("查询系统信息")
	@RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
	public ResponseModal getHtBoaInApp(@PathVariable Long id) {
		ResponseModal rm = new ResponseModal();
		HtBoaInApp htBoaInApp=htBoaInAppService.findById(id);
		rm.setStatus_code(200);
		rm.setResult(htBoaInApp);
		log.info("====htBoaInApp===="+htBoaInApp);
		return rm;
	}
}
