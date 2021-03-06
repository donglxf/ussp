package com.ht.ussp.uc.app.resource;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.common.Constants;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.domain.HtBoaInUserApp;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInAppService;
import com.ht.ussp.uc.app.service.HtBoaInUserAppService;
import com.ht.ussp.uc.app.vo.PageVo;
import com.ht.ussp.util.JsonUtil;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

/**
 * @Description: 用户系统
 * @date 2018年1月10日 下午14:44:16
 */

@RestController
@RequestMapping(value = "/userapp")
@Log4j2
public class UserAppResource {

    @Autowired
    private HtBoaInUserAppService htBoaInUserAppService;
    @Autowired
    private HtBoaInAppService htBoaInAppService;
   
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
   	@ApiOperation(value = "对内：根据UserId查询用户系统", notes = "根据UserId查询用户系统")
    @PostMapping(value = {"/listUserAppByPage" }, produces = { "application/json" })
	public PageResult<HtBoaInUserApp> listUserAppByPage(PageVo page) {
		PageResult result = new PageResult();
		PageConf pageConf = new PageConf();
		pageConf.setPage(page.getPage());
		pageConf.setSize(page.getLimit());
		pageConf.setSearch(page.getKeyWord());
		long sl = System.currentTimeMillis(), el = 0L;
		String msg = "成功";
		String logHead = "根据UserId查询用户系统：user/listUserAppByPage param-> {}";
		String logStart = logHead + " | START:{}";
		String logEnd = logHead + " {} | END:{}, COST:{}";
		log.debug(logStart, "page: " + page, sl);

		result = htBoaInUserAppService.listUserAppByPage(pageConf, page.getQuery());
		el = System.currentTimeMillis();
		log.debug(logEnd, "page: " + page, msg, el, el - sl);
		return result;
	}

    /**
     * 查询所有系统
     * @param page
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "对内：查询用户系统" )
    @RequestMapping(value = { "/listAppByPage"}, method = RequestMethod.POST)
    public PageResult<HtBoaInUserApp> listAppByPage(PageVo page) {
    	PageResult result = new PageResult();
    	PageConf pageConf = new PageConf();
    	pageConf.setPage(page.getPage());
    	pageConf.setSize(page.getLimit());
    	pageConf.setSearch(page.getKeyWord());
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "查询用户系统：user/listAppByPage param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "page: " + page, sl);
        
        result =  htBoaInUserAppService.listAllUserAppByPage(pageConf,page.getQuery()); 
        el = System.currentTimeMillis();
        log.debug(logEnd, "page: " + page, msg, el, el - sl);
        return result;
    }
    
    @SuppressWarnings("rawtypes")
	@ApiOperation(value = "对内：新增/编辑用户系统记录", notes = "提交用户系统信息新增/编辑角色")
    @RequestMapping(value = { "/add" }, method = RequestMethod.POST)
    public Result add(@RequestBody HtBoaInUserApp htBoaInUserApp,@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "boaInRoleInfo: " + htBoaInUserApp, sl);
        HtBoaInUserApp u = null;
        
        //验证是否已经关联系统
        List<HtBoaInUserApp> listHtBoaInUserApp = htBoaInUserAppService.getUserAppList(htBoaInUserApp.getApp(),htBoaInUserApp.getUserId());
        
		if (listHtBoaInUserApp.isEmpty()) {
			u = new HtBoaInUserApp();
			u.setCreatedDatetime(new Date());
			u.setLastModifiedDatetime(new Date());
			u.setDelFlag(Constants.DEL_0);
			u.setUserId(htBoaInUserApp.getUserId());
			u.setApp(htBoaInUserApp.getApp());
			u.setCreatedDatetime(new Date());
			u.setCreateOperator(userId);
			u.setController("N");
			u = htBoaInUserAppService.add(u);
			if(u!=null) {
				htBoaInUserAppService.pushMq(htBoaInUserApp.getApp(), "addAppUser", JsonUtil.obj2Str(u));
			}
		}
       
        el = System.currentTimeMillis();
        log.debug(logEnd, "boaInRoleInfo: " + htBoaInUserApp, msg, el, el - sl);
        return Result.buildSuccess();
       // return new ResponseModal(200, msg, u);
    }
    
    @SuppressWarnings({ "rawtypes", "unused" })
	@ApiOperation(value = "对内：禁用/启用用户系统", notes = "禁用/启用用户系统")
    @RequestMapping(value = { "/stop" }, method = RequestMethod.POST)
    public Result stop(Long id, String status,@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        //log.debug(logStart, "boaInRoleInfo: " + boaInRoleInfo, sl);
        HtBoaInUserApp u = null;
        if(id>0) {
        	u = htBoaInUserAppService.findById(id);
        }else {
        	return Result.buildFail();
        }
        if(u==null) {
    		return Result.buildFail();
    	}
        u.setLastModifiedDatetime(new Date());
        u.setDelFlag(Integer.parseInt(status));
        u.setUpdateOperator(userId);
        u = htBoaInUserAppService .update(u);
        
        el = System.currentTimeMillis();
        log.debug(logEnd, "boaInRoleInfo: " + u, msg, el, el - sl);
        return Result.buildSuccess();
    }
    @SuppressWarnings({ "rawtypes", "unused" })
   	@ApiOperation(value = "设置用户系统管理员")
	@RequestMapping(value = { "/isController" }, method = RequestMethod.POST)
	public Result isController(Long id, String isController, @RequestHeader("userId") String userId) {
		long sl = System.currentTimeMillis(), el = 0L;
		ResponseModal r = null;
		HtBoaInUserApp u = null;
		if (id > 0) {
			u = htBoaInUserAppService.findById(id);
		} else {
			return Result.buildFail();
		}
		if (u == null) {
			return Result.buildFail();
		}
		u.setLastModifiedDatetime(new Date());
		u.setController("true".equals(isController)?"Y":"N");
		u.setUpdateOperator(userId);
		u = htBoaInUserAppService.update(u);
		return Result.buildSuccess();
	}
    
    @ApiOperation(value = "对内：删除用户系统记录", notes = "提交角色编号，可批量删除")
    @RequestMapping(value = {"/delete" }, method = RequestMethod.POST)
    public Result delete(long id) {
        long sl = System.currentTimeMillis(), el = 0L;
        HtBoaInUserApp u = htBoaInUserAppService.findById(id);
        if(u!=null) {
        	htBoaInUserAppService.delete(u);
        	u.setLastModifiedDatetime(new Date());
			htBoaInUserAppService.pushMq(u.getApp(), "delAppUser", JsonUtil.obj2Str(u));
		}
        el = System.currentTimeMillis();
        return Result.buildSuccess();
    }
    
    
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "删除用户系统记录", notes = "根据用户id,系统code删除")
	@PostMapping(value = { "/deleteByUserIdAndAppCode" }, produces = { "application/json" })
	public Result deleteByUserIdAndAppCode(String userId, String appCode) {
		List<HtBoaInUserApp> userAppList = htBoaInUserAppService.findByUserIdAndApp(userId, appCode);
		if(userAppList!=null) {
			for(HtBoaInUserApp u : userAppList) {
				htBoaInUserAppService.delete(u);
			}
		}
		return Result.buildSuccess();
	}
	
	@ApiOperation(value = "查询系统下所有用户", notes = "查询系统下所有用户")
	@PostMapping(value = "/getUserInfoForApp")
	public PageResult<HtBoaInUser> getUserInfoForApp(PageVo page) {
		PageResult result = new PageResult();
		PageConf pageConf = new PageConf();
		pageConf.setPage(page.getPage());
		pageConf.setSize(page.getLimit());
		pageConf.setSearch(page.getKeyWord());
		long sl = System.currentTimeMillis(), el = 0L;
		String msg = "成功";
		String logHead = "查询系统下所有用户：getUserInfoForApp param-> {}";
		String logStart = logHead + " | START:{}";
		String logEnd = logHead + " {} | END:{}, COST:{}";
		log.debug(logStart, "page: " + page, sl);
		result = htBoaInUserAppService.getUserInfoForAppByPage(pageConf, page.getQuery());
		el = System.currentTimeMillis();
		log.debug(logEnd, "page: " + page, msg, el, el - sl);
		return result;
	}
}
