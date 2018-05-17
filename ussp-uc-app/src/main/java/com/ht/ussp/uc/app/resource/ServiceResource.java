package com.ht.ussp.uc.app.resource;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInService;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInServiceService;
import com.ht.ussp.uc.app.service.HtBoaInUserAppService;
import com.ht.ussp.uc.app.vo.PageVo;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/minservice")
public class ServiceResource {

    private static final Logger log = LoggerFactory.getLogger(ServiceResource.class);

    @Autowired
    private HtBoaInServiceService htBoaInServiceService;
    
    @Autowired
    private HtBoaInUserAppService htBoaInUserAppService;

    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ApiOperation(value = "微服务列表查询", notes = "列出所有微服务记录列表信息")
    @PostMapping("/getServiceListByPage")
    public PageResult<HtBoaInService> list(PageVo page) {
    	PageResult result = new PageResult();
    	PageConf pageConf = new PageConf();
    	pageConf.setPage(page.getPage());
    	pageConf.setSize(page.getLimit());
    	pageConf.setSearch(page.getKeyWord());
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "微服务记录查询：role/in/list param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "pageConf: " + pageConf, sl);
        result =  htBoaInServiceService.getUserListByPage(new PageRequest(page.getPage(), page.getLimit()),page.getQuery(),page.getKeyWord());
        el = System.currentTimeMillis();
        log.debug(logEnd, "pageConf: " + result, msg, el, el - sl);
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
        return result;
    }
    
    
    @SuppressWarnings({ "unused", "rawtypes" })
	@ApiOperation(value = "新增微服务信息")
    @PostMapping("/add")
    public Result add(@RequestBody HtBoaInService htBoaInService,@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "微服务记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "htBoaInService: " + htBoaInService, sl);
        HtBoaInService u = null;
        if(htBoaInService.getId()!=null) {
        	u = htBoaInServiceService.findById(htBoaInService.getId());
        }
        if(u==null) {
    		u = new HtBoaInService();
    	}
        u.setApp(htBoaInService.getApp());
        u.setMainService(htBoaInService.getMainService());
        u.setMainServiceName(htBoaInService.getMainServiceName());
        u.setServcieCode(UUID.randomUUID().toString().replace("-", ""));
        u.setStatus("0");
    	u.setCreatedDatetime(new Date());
    	u.setUpdateDatetime(new Date());
    	u.setUpdateOperator(userId);
        u.setCreateOperator(userId);
        try {
        	htBoaInService  = htBoaInServiceService.add(u);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.buildFail(e.getLocalizedMessage(), e.getMessage());
		}
        el = System.currentTimeMillis();
        log.debug(logEnd, "htBoaInService: " + htBoaInService, msg, el, el - sl);
        return Result.buildSuccess();
    }
    
    @SuppressWarnings({ "unused", "rawtypes" })
	@ApiOperation(value = "禁用/启用微服务", notes = "禁用/启用微服务")
    @PostMapping("/stop")
    public Result stop( Long id, String status,@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "微服务记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        //log.debug(logStart, "HtBoaInService: " + HtBoaInService, sl);
        HtBoaInService u = null;
        if(id>0) {
        	u = htBoaInServiceService.findById(id);
        }else {
        	return Result.buildFail();
        }
        if(u==null) {
    		return Result.buildFail();
    	}
        u.setUpdateDatetime(new Date());
        u.setStatus(status);
        u.setUpdateOperator(userId);
        u = htBoaInServiceService.add(u);
        el = System.currentTimeMillis();
        log.debug(logEnd, "HtBoaInService: " + u, msg, el, el - sl);
        return Result.buildSuccess();
    }
}
