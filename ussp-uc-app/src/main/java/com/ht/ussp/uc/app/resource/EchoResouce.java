package com.ht.ussp.uc.app.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @ClassName: EchoResouce
 * @Description: demo
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月8日 下午8:20:32
 */
@RestController
@RequestMapping(value="/echo")
public class EchoResouce {
	private static final Logger log = LoggerFactory.getLogger(EchoResouce.class);

	 @ApiOperation("get demo")
	    @RequestMapping(value={"/getHeaderDemo"}, method=RequestMethod.GET)
	    public String getEcho() {
		 log.info("====userId===="+1111);
	        return "echo";
	    }
	 
	 @ApiOperation(value="post demo",notes="for create")
	    @RequestMapping(value={""}, method=RequestMethod.POST)
	    public String postEcho() {
	        return "put success";
	    }
	 
	    @ApiOperation(value="", notes="用ID查询")
	    @ApiImplicitParam(name = "id", value = "ID", required = true, dataType = "String")
	    @RequestMapping(value="/{id}", method=RequestMethod.GET)
	    public String getId(@PathVariable String id) {
	        return id;
	    }
}
