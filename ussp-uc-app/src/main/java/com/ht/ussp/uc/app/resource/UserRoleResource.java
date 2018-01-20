package com.ht.ussp.uc.app.resource;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInRole;
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
@CrossOrigin(origins = "*")
@RequestMapping(value = "/userrole")
@Log4j2
public class UserRoleResource {

    @Autowired
    private HtBoaInUserRoleService htBoaInUserRoleService;

    @SuppressWarnings("rawtypes")
   	@ApiOperation(value = "对内：根据UserId查询用户角色", notes = "根据UserId查询用户角色")
    @RequestMapping(value = { "/listUserRoleByPage"}, method = RequestMethod.POST)
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
           log.info(logStart, "page: " + page, sl);
           
           result =  htBoaInUserRoleService.listUserRoleByPage(pageConf,page.getQuery()); 
           el = System.currentTimeMillis();
           log.info(logEnd, "page: " + page, msg, el, el - sl);
           return result;
     }

    @ApiOperation(value = "对内：新增/编辑用户角色记录", notes = "提交用户角色信息新增/编辑角色")
    @RequestMapping(value = { "/add" }, method = RequestMethod.POST)
    public Result add(@RequestBody HtBoaInUserRole htBoaInUserRole) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "boaInRoleInfo: " + htBoaInUserRole, sl);
        HtBoaInUserRole u = null;
        if(htBoaInUserRole.getId()>0) {
        	u = htBoaInUserRoleService.findById(htBoaInUserRole.getId());
        	if(u==null) {
        		u = new HtBoaInUserRole();
        	}
        }else {
        	u = new HtBoaInUserRole();
        }
       
        u.setCreatedDatetime(new Date());
        u.setLastModifiedDatetime(new Date());
        u.setDelFlag(0);
        u.setRoleCode(htBoaInUserRole.getRoleCode());
        u.setUserId(htBoaInUserRole.getUserId());
        
        if(htBoaInUserRole.getId()>0) {
        	u.setId(htBoaInUserRole.getId());
        	u = htBoaInUserRoleService.update(u);
        } else {
        	u.setCreatedDatetime(new Date());
        	u.setCreateOperator("10001");
            u = htBoaInUserRoleService.add(u);
        }
        el = System.currentTimeMillis();
        log.info(logEnd, "boaInRoleInfo: " + htBoaInUserRole, msg, el, el - sl);
        return Result.buildSuccess();
       // return new ResponseModal(200, msg, u);
    }
    
    @SuppressWarnings({ "rawtypes", "unused" })
	@ApiOperation(value = "对内：禁用/启用用户角色", notes = "禁用/启用用户角色")
    @RequestMapping(value = { "/stop/{id}/{status}" }, method = RequestMethod.POST)
    public Result stop(@PathVariable Long id,@PathVariable String status) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        //log.info(logStart, "boaInRoleInfo: " + boaInRoleInfo, sl);
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
        u = htBoaInUserRoleService .update(u);
        
        el = System.currentTimeMillis();
        log.info(logEnd, "boaInRoleInfo: " + u, msg, el, el - sl);
        return Result.buildSuccess();
    }
    
    @ApiOperation(value = "对内：删除用户角色记录", notes = "提交角色编号，可批量删除")
    @ApiImplicitParam(name = "codes", value = "角色编号集", required = true, dataType = "Codes")
    @RequestMapping(value = {"/delete/{id}" }, method = RequestMethod.POST)
    public Result delete(@PathVariable int id) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "角色记录删除：role/in/delete param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "codes: " + id, sl);
        HtBoaInUserRole u = new HtBoaInUserRole();
        u.setId((long) id);
        htBoaInUserRoleService.delete(u);
        el = System.currentTimeMillis();
        log.info(logEnd, "codes: " + id, msg, el, el - sl);
        return Result.buildSuccess();
        //return new ResponseModal(200, msg);
    }
}
