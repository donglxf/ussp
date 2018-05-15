package com.ht.ussp.uc.app.resource;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.common.Constants;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInUserRole;
import com.ht.ussp.uc.app.model.BoaInRoleInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInUserRoleService;
import com.ht.ussp.uc.app.vo.PageVo;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

/**
 * @Description: 用户角色
 * @date 2018年1月10日 下午14:44:16
 */

@RestController
@RequestMapping(value = "/userrole")
@Log4j2
public class UserRoleResource {

    @Autowired
    private HtBoaInUserRoleService htBoaInUserRoleService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value = "对内：根据UserId查询用户角色", notes = "根据UserId查询用户角色")
	@RequestMapping(value = { "/listUserRoleByPage" }, produces = { "application/json" })
	public PageResult<HtBoaInUserRole> listUserRoleByPage(PageVo page) {
		PageResult result = new PageResult();
		PageConf pageConf = new PageConf();
		pageConf.setPage(page.getPage());
		pageConf.setSize(page.getLimit());
		pageConf.setSearch(page.getKeyWord());
		long sl = System.currentTimeMillis(), el = 0L;
		String msg = "成功";
		String logHead = "根据UserId查询用户角色：user/listUserRoleByPage param-> {}";
		String logStart = logHead + " | START:{}";
		String logEnd = logHead + " {} | END:{}, COST:{}";
		log.debug(logStart, "page: " + page, sl);

		result = htBoaInUserRoleService.listUserRoleByPage(pageConf, page.getQuery());
		el = System.currentTimeMillis();
		log.debug(logEnd, "page: " + page, msg, el, el - sl);
		return result;
	}
    
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "获取用户所有角色信息")
	@RequestMapping(value = { "/getUserRole" }, method = RequestMethod.GET)
	public List<BoaInRoleInfo> getUserRole(@RequestParam("userId")String userId) {
    	PageResult result = new PageResult();
    	PageConf pageConf = new PageConf();
		pageConf.setPage(0); 
		pageConf.setSize(500);
		Map<String, String> query = new HashMap<>();
		query.put("userId", userId);
		result = htBoaInUserRoleService.listUserRoleByPage(pageConf, query);
		List<BoaInRoleInfo> listBoaInRoleInfo= (List<BoaInRoleInfo>) result.getData();
		return listBoaInRoleInfo;
	}
    

    @SuppressWarnings("rawtypes")
	@ApiOperation(value = "对内：新增/编辑用户角色记录", notes = "提交用户角色信息新增/编辑角色")
    @PostMapping(value = { "/add" }, produces = {"application/json"})
    public Result add(@RequestBody HtBoaInUserRole htBoaInUserRole,@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "boaInRoleInfo: " + htBoaInUserRole, sl);
        HtBoaInUserRole u = new HtBoaInUserRole();
        
        //验证是否已经分配角色
        List<BoaInRoleInfo> listHtBoaInUserRole = htBoaInUserRoleService.getUserRoleList(htBoaInUserRole.getRoleCode(),htBoaInUserRole.getUserId());
        
		if (listHtBoaInUserRole.isEmpty()) {
			u.setRoleCode(htBoaInUserRole.getRoleCode());
			u.setUserId(htBoaInUserRole.getUserId());
			u.setCreatedDatetime(new Date());
			u.setLastModifiedDatetime(new Date());
			u.setDelFlag(Constants.DEL_0);
			u.setCreatedDatetime(new Date());
			u.setCreateOperator(userId);
			u = htBoaInUserRoleService.add(u);
		}
        
        el = System.currentTimeMillis();
        log.debug(logEnd, "boaInRoleInfo: " + htBoaInUserRole, msg, el, el - sl);
        return Result.buildSuccess();
       // return new ResponseModal(200, msg, u);
    }
    
    @SuppressWarnings({ "rawtypes", "unused" })
	@ApiOperation(value = "对内：禁用/启用用户角色", notes = "禁用/启用用户角色")
    @PostMapping(value = { "/stop" }, produces = {"application/json"})
    public Result stop( Long id, String status,@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        //log.debug(logStart, "boaInRoleInfo: " + boaInRoleInfo, sl);
        HtBoaInUserRole u = null;
        if(id>0) {
        	u = htBoaInUserRoleService.findById(id);
        }else {
        	return Result.buildFail();
        }
        if(u==null) {
    		return Result.buildFail();
    	}
        u.setLastModifiedDatetime(new Date());
        u.setDelFlag(Integer.parseInt(status));
        u.setUpdateOperator(userId);
        u = htBoaInUserRoleService .update(u);
        
        el = System.currentTimeMillis();
        log.debug(logEnd, "boaInRoleInfo: " + u, msg, el, el - sl);
        return Result.buildSuccess();
    }
    
    @SuppressWarnings("rawtypes")
	@ApiOperation(value = "对内：删除用户角色记录", notes = "提交角色编号，可批量删除")
    @ApiImplicitParam(name = "codes", value = "角色编号集", required = true, dataType = "Codes")
    @PostMapping(value = {"/delete" }, produces = {"application/json"})
    public Result delete(int id) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "角色记录删除：role/in/delete param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "codes: " + id, sl);
        HtBoaInUserRole u = new HtBoaInUserRole();
        u.setId((long) id);
        htBoaInUserRoleService.delete(u);
        el = System.currentTimeMillis();
        log.debug(logEnd, "codes: " + id, msg, el, el - sl);
        return Result.buildSuccess();
        //return new ResponseModal(200, msg);
    }
}
