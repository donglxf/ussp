package com.ht.ussp.uc.app.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInService;
import com.ht.ussp.uc.app.domain.HtBoaInServiceApi;
import com.ht.ussp.uc.app.domain.HtBoaInServiceCall;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInServiceApiService;
import com.ht.ussp.uc.app.service.HtBoaInServiceCallService;
import com.ht.ussp.uc.app.service.HtBoaInServiceService;
import com.ht.ussp.uc.app.vo.AppAndServiceVo;
import com.ht.ussp.uc.app.vo.PageVo;
import com.ht.ussp.uc.app.vo.ServiceAuthServiceVo;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/authservice")
public class ServiceAuthResource {

    private static final Logger log = LoggerFactory.getLogger(ServiceAuthResource.class);

    @Autowired
    private HtBoaInServiceService htBoaInServiceService;
    
    @Autowired
    private HtBoaInServiceCallService htBoaInServiceCallService;
    
    @Autowired
    private HtBoaInServiceApiService htBoaInServiceApiService;
    
	@ApiOperation(value = "获取微服务结构树")
    @PostMapping("/app/getAppServiceTree")
    public List<AppAndServiceVo> getAppServiceTree() {
    	return htBoaInServiceService.loadAppAndServiceVoList();
    }
	
	@ApiOperation(value = "获取对应已授权的微服务")
    @PostMapping("/listCalledService")
    public PageResult  listCalledService(PageVo page) {
		PageResult result = new PageResult();
    	PageConf pageConf = new PageConf();
    	pageConf.setPage(page.getPage());
    	pageConf.setSize(page.getLimit());
    	pageConf.setSearch(page.getKeyWord());
    	 return htBoaInServiceCallService.findAllByPage(pageConf,page.getQuery());
    }
    
	@SuppressWarnings({ "unused", "rawtypes" })
	@ApiOperation(value = "新增授权微服务")
	@PostMapping("/addCalledService")
	public Result add(@RequestBody HtBoaInServiceCall htBoaInServiceCall, @RequestHeader("userId") String userId) {
		long sl = System.currentTimeMillis(), el = 0L;
		ResponseModal r = null;
		String msg = "成功";
		String logHead = "新增授权微服务：addCalledService param-> {}";
		String logStart = logHead + " | START:{}";
		String logEnd = logHead + " {} | END:{}, COST:{}";
		log.debug(logStart, "htBoaInServiceCall: " + htBoaInServiceCall, sl);
		HtBoaInServiceCall u = null;
		if (htBoaInServiceCall.getId() != null) {
			u = htBoaInServiceCallService.findById(htBoaInServiceCall.getId());
		}
		if (u == null) {
			u = new HtBoaInServiceCall();
		}
		u.setCallServiceCode(htBoaInServiceCall.getCallServiceCode());
		u.setAuthServiceCode(UUID.randomUUID().toString().replace("-", ""));
		u.setMainServiceCode(htBoaInServiceCall.getMainServiceCode());
		u.setStatus(StringUtils.isEmpty(htBoaInServiceCall.getStatus())?"0":htBoaInServiceCall.getStatus());
		u.setCreatedDatetime(new Date());
		u.setUpdateDatetime(new Date());
		u.setUpdateOperator(userId);
		u.setCreateOperator(userId);
		u.setJapVersion("0");
		try {
			htBoaInServiceCall = htBoaInServiceCallService.add(u);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.buildFail(e.getLocalizedMessage(), e.getMessage());
		}
		el = System.currentTimeMillis();
		log.debug(logEnd, "htBoaInServiceCall : " + htBoaInServiceCall, msg, el, el - sl);
		return Result.buildSuccess(htBoaInServiceCall);
	}
	
	@SuppressWarnings({ "unused", "rawtypes" })
	@ApiOperation(value = "批量新增授权微服务")
	@PostMapping("/addCalledServiceBatch")
	public Result addCalledServiceBatch(@RequestBody ServiceAuthServiceVo serviceAuthServiceVo, @RequestHeader("userId") String userId) {
		long sl = System.currentTimeMillis(), el = 0L;
		ResponseModal r = null;
		String msg = "成功";
		String logHead = "批量新增授权微服务：addCalledServiceBatch param-> {}";
		String logStart = logHead + " | START:{}";
		String logEnd = logHead + " {} | END:{}, COST:{}";
		log.debug(logStart, "serviceAuthServiceVo: " + serviceAuthServiceVo, sl);
		List<HtBoaInServiceCall> listHtBoaInServiceCall = new ArrayList<HtBoaInServiceCall>();
		if(serviceAuthServiceVo!=null) {
			if(!StringUtils.isEmpty(serviceAuthServiceVo.getMainServiceCode()) && serviceAuthServiceVo.getServiceList()!=null && !serviceAuthServiceVo.getServiceList().isEmpty()) {
				for(HtBoaInService htBoaInService : serviceAuthServiceVo.getServiceList()) {
					HtBoaInServiceCall u = htBoaInServiceCallService.findByMainServiceCodeAndCallService(serviceAuthServiceVo.getMainServiceCode(),htBoaInService.getServiceCode());
					if (u == null) {
						 u = new HtBoaInServiceCall();
						if(!serviceAuthServiceVo.getMainServiceCode().equals(htBoaInService.getServiceCode())) {
							u.setCallServiceCode(htBoaInService.getServiceCode());
							u.setAuthServiceCode(UUID.randomUUID().toString().replace("-", ""));
							u.setMainServiceCode(serviceAuthServiceVo.getMainServiceCode());
							u.setStatus("0");
							u.setJapVersion("0");
							u.setCreatedDatetime(new Date());
							u.setUpdateDatetime(new Date());
							u.setUpdateOperator(userId);
							u.setCreateOperator(userId);
							listHtBoaInServiceCall.add(u);
						}
					}
				}
			}
		}
		if(listHtBoaInServiceCall!=null&&!listHtBoaInServiceCall.isEmpty()) {
			listHtBoaInServiceCall = htBoaInServiceCallService.addList(listHtBoaInServiceCall);
		}
		el = System.currentTimeMillis();
		log.debug(logEnd, "listHtBoaInServiceCall : " + listHtBoaInServiceCall, msg, el, el - sl);
		return Result.buildSuccess(listHtBoaInServiceCall);
	}
	
	@SuppressWarnings({   "rawtypes" })
	@ApiOperation(value = "禁用/启用微服务", notes = "禁用/启用微服务")
    @PostMapping("/stop")
    public Result stop( Long id, String status,@RequestHeader("userId") String userId) {
        HtBoaInServiceCall u = null;
        if(id>0) {
        	u = htBoaInServiceCallService.findById(id);
        }else {
        	return Result.buildFail();
        }
        if(u==null) {
    		return Result.buildFail();
    	}
        u.setUpdateDatetime(new Date());
        u.setStatus(status);
        u.setUpdateOperator(userId);
        u = htBoaInServiceCallService.add(u);
        return Result.buildSuccess(u);
    }
	
	///api
	
	@ApiOperation(value = "获取对应已授权的微服务API")
    @PostMapping("/listServiceAPI")
    public PageResult  listServiceAPI(PageVo page) {
		PageResult result = new PageResult();
    	PageConf pageConf = new PageConf();
    	pageConf.setPage(page.getPage());
    	pageConf.setSize(page.getLimit());
    	pageConf.setSearch(page.getKeyWord());
        return htBoaInServiceApiService.findAllByPage(new PageRequest(page.getPage(), page.getLimit(),new Sort(new Order("apiContent"))),page.getQuery());
    }
    
	@SuppressWarnings({ "unused", "rawtypes" })
	@ApiOperation(value = "新增授权微服务API")
	@PostMapping("/addServiceAPI")
	public Result addServiceAPI(@RequestBody HtBoaInServiceApi htBoaInServiceApi, @RequestHeader("userId") String userId) {
		long sl = System.currentTimeMillis(), el = 0L;
		ResponseModal r = null;
		String msg = "成功";
		String logHead = "新增授权微服务：addCalledService param-> {}";
		String logStart = logHead + " | START:{}";
		String logEnd = logHead + " {} | END:{}, COST:{}";
		log.debug(logStart, "htBoaInServiceApi: " + htBoaInServiceApi, sl);
		HtBoaInServiceApi u = null;
		if (htBoaInServiceApi.getId() != null) {
			u = htBoaInServiceApiService.findById(htBoaInServiceApi.getId());
		}
		if (u == null) {
			u = htBoaInServiceApi;
		}
		u.setStatus("0");
		u.setJapVersion("0");
		u.setCreatedDatetime(new Date());
		u.setUpdateDatetime(new Date());
		u.setUpdateOperator(userId);
		u.setCreateOperator(userId);
		try {
			htBoaInServiceApi = htBoaInServiceApiService.add(u);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.buildFail(e.getLocalizedMessage(), e.getMessage());
		}
		el = System.currentTimeMillis();
		log.debug(logEnd, "htBoaInServiceApi : " + htBoaInServiceApi, msg, el, el - sl);
		return Result.buildSuccess(htBoaInServiceApi);
	}
	
	@SuppressWarnings({   "rawtypes" })
	@ApiOperation(value = "禁用/启用微服务API", notes = "禁用/启用微服务  0:正常 1：")
    @PostMapping("/stopServiceApi")
    public Result stopServiceApi( Long id, String status,@RequestHeader("userId") String userId) {
		HtBoaInServiceApi   u = null;
        if(id>0) {
        	u = htBoaInServiceApiService.findById(id);
        }else {
        	return Result.buildFail();
        }
        if(u==null) {
    		return Result.buildFail();
    	}
        u.setUpdateDatetime(new Date());
        u.setStatus(status);
        u.setUpdateOperator(userId);
        u = htBoaInServiceApiService.add(u);
        return Result.buildSuccess(u);
    }
	
	@SuppressWarnings({   "rawtypes" })
	@ApiOperation(value = "删除微服务API", notes = "删除微服务API")
    @PostMapping("/delServiceApi")
    public Result delServiceApi( Long id, String status,@RequestHeader("userId") String userId) {
		HtBoaInServiceApi   u = null;
        if(id>0) {
        	u = htBoaInServiceApiService.findById(id);
        }else {
        	return Result.buildFail();
        }
        if(u==null) {
    		return Result.buildFail();
    	}
        htBoaInServiceApiService.delete(u.getId());
        return Result.buildSuccess();
    }
}
