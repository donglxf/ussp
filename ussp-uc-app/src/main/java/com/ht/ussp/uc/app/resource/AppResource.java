package com.ht.ussp.uc.app.resource;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.common.Constants;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInApp;
import com.ht.ussp.uc.app.model.BoaInAppInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInAppService;
import com.ht.ussp.uc.app.vo.PageVo;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInAppResource
 * @Description: 系统信息
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
        HtBoaInApp htBoaInApp = htBoaInAppService.findById(id);
        rm.setStatus_code("200");
        rm.setResult(htBoaInApp);
        log.debug("====htBoaInApp====" + htBoaInApp);
        return rm;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ApiOperation(value = "对内：系统记录查询")
    @RequestMapping(value = {"/list" }, method = RequestMethod.POST)
    public PageResult<BoaInAppInfo> list(PageVo page) {
    	PageResult result = new PageResult();
    	PageConf pageConf = new PageConf();
    	pageConf.setPage(page.getPage());
    	pageConf.setSize(page.getLimit());
    	pageConf.setSearch(page.getKeyWord());
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/list param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "pageConf: " + pageConf, sl);
        Page<BoaInAppInfo> pageData = (Page<BoaInAppInfo>) htBoaInAppService.findAllByPage(pageConf,page.getQuery());
        el = System.currentTimeMillis();
        log.debug(logEnd, "pageConf: " + pageConf, msg, el, el - sl);
        if (pageData != null) {
            result.count(pageData.getTotalElements()).data(pageData.getContent());
        }
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
        return result;
    }
    
    @SuppressWarnings({ "unused", "rawtypes" })
	@ApiOperation(value = "对内：新增/编辑系统信息记录")
    @RequestMapping(value = { "/add" }, method = RequestMethod.POST)
    public Result add(@RequestBody BoaInAppInfo boaInAppInfo,@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "boaInAppInfo: " + boaInAppInfo, sl);
        HtBoaInApp u = null;
        if(boaInAppInfo.getId()>0) {
        	u = htBoaInAppService.findById(boaInAppInfo.getId());
        	if(u==null) {
        		u = new HtBoaInApp();
        	}
        }else {
        	u = new HtBoaInApp();
        }
        u.setApp(boaInAppInfo.getApp());
        u.setLastModifiedDatetime(new Date());
        u.setName(boaInAppInfo.getNameCn());
        u.setNameCn(boaInAppInfo.getNameCn());
        if(boaInAppInfo.getId()>0) {
        	u.setId(boaInAppInfo.getId());
        	u.setUpdateOperator(userId);
        	u = htBoaInAppService.update(u);
        } else {
        	u.setCreatedDatetime(new Date());
        	u.setStatus("0");
            u.setCreateOperator(userId);
            u = htBoaInAppService.add(u);
        }
        el = System.currentTimeMillis();
        log.debug(logEnd, "boaInAppInfo: " + boaInAppInfo, msg, el, el - sl);
        return Result.buildSuccess();
       // return new ResponseModal(200, msg, u);
    }
    
    @SuppressWarnings({ "unused", "rawtypes" })
	@ApiOperation(value = "对内：禁用/启用角色", notes = "禁用/启用角色")
    @RequestMapping(value = { "/stop" }, method = RequestMethod.POST)
    public Result stop( Long id, String status,@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        //log.debug(logStart, "boaInAppInfo: " + boaInAppInfo, sl);
        HtBoaInApp u = null;
        if(id>0) {
        	u = htBoaInAppService.findById(id);
        }else {
        	return Result.buildFail();
        }
        if(u==null) {
    		return Result.buildFail();
    	}
        u.setLastModifiedDatetime(new Date());
        u.setStatus(status);
        u.setUpdateOperator(userId);
        u = htBoaInAppService.update(u);
        
        el = System.currentTimeMillis();
        log.debug(logEnd, "boaInAppInfo: " + u, msg, el, el - sl);
        return Result.buildSuccess();
       // return new ResponseModal(200, msg, u);
    }
    
    @SuppressWarnings("rawtypes")
	@ApiOperation(value = "对内：物理删除角色记录", notes = "提交角色编号，可批量删除")
    @ApiImplicitParam(name = "codes", value = "角色编号集", required = true, dataType = "Codes")
    @RequestMapping(value = {"/deleteTrunc" }, method = RequestMethod.POST)
    public Result deleteTrunc(int id) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "角色记录删除：role/in/delete param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "codes: " + id, sl);
        htBoaInAppService.delete(id);
        el = System.currentTimeMillis();
        log.debug(logEnd, "codes: " + id, msg, el, el - sl);
        return Result.buildSuccess();
        //return new ResponseModal(200, msg);
    }

 
      
     @SuppressWarnings("rawtypes")
     @ApiOperation(value = "对内：删除标记岗位记录", notes = "提交岗位编号，可批量删除")
     @RequestMapping(value = {"/delete" }, method = RequestMethod.POST)
     public Result delete(long id,@RequestHeader("userId") String userId) {
          long sl = System.currentTimeMillis(), el = 0L;
          String msg = "成功";
          String logHead = "岗位记录删除：position/in/delete param-> {}";
          String logStart = logHead + " | START:{}";
          String logEnd = logHead + " {} | END:{}, COST:{}";
          log.debug(logStart, "codes: " + id, sl);
          HtBoaInApp u = htBoaInAppService.findById(id);
          u.setDelFlag(Constants.DEL_1);
          u.setStatus(Constants.STATUS_1);
          u.setUpdateOperator(userId);
          u.setLastModifiedDatetime(new Date());
          htBoaInAppService.update(u);
          el = System.currentTimeMillis();
          log.debug(logEnd, "codes: " + id, msg, el, el - sl);
          return Result.buildSuccess();
          
       }
     
     
     @SuppressWarnings("rawtypes")
     @ApiOperation(value = "对内：机构编码是否可用  true：可用  false：不可用")
     @PostMapping(value = {"/isExistAppCode" }, produces = {"application/json"} )
     public Result isExistAppCode( String appCode) {
        List<HtBoaInApp> listHtBoaInApp = htBoaInAppService.findByAppCode(appCode);
        if(listHtBoaInApp.isEmpty()) {
        	return Result.buildSuccess();
        }else {
        	return Result.buildFail();
        }
     }
}
