package com.ht.ussp.uc.app.resource;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInPositionRole;
import com.ht.ussp.uc.app.domain.HtBoaInUserRole;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInPositionRoleService;
import com.ht.ussp.uc.app.vo.PageVo;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

/**
 * @Description: 岗位角色
 * @date 2018年1月10日 下午14:44:16
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/positionrole")
@Log4j2
public class PositionRoleResource {

    @Autowired
    private HtBoaInPositionRoleService htBoaInPositionRoleService;

    @SuppressWarnings("rawtypes")
   	@ApiOperation(value = "对内：根据UserId查询岗位角色", notes = "根据UserId查询岗位角色")
    @RequestMapping(value = { "/listPositionRoleByPage"}, method = RequestMethod.POST)
    public PageResult<HtBoaInUserRole> listPositionRoleByPage(PageVo page) {
       	PageResult result = new PageResult();
       	PageConf pageConf = new PageConf();
       	pageConf.setPage(page.getPage());
       	pageConf.setSize(page.getLimit());
       	pageConf.setSearch(page.getKeyWord());
           long sl = System.currentTimeMillis(), el = 0L;
           String msg = "成功";
           String logHead = "根据UserId查询岗位角色：user/listPositionRoleByPage param-> {}";
           String logStart = logHead + " | START:{}";
           String logEnd = logHead + " {} | END:{}, COST:{}";
           log.info(logStart, "page: " + page, sl);
           
           result =  htBoaInPositionRoleService.listPositionRoleByPage(pageConf,page.getQuery()); 
           el = System.currentTimeMillis();
           log.info(logEnd, "page: " + page, msg, el, el - sl);
           return result;
     }

    @ApiOperation(value = "对内：新增/编辑岗位角色记录", notes = "提交岗位角色信息新增/编辑角色")
    @RequestMapping(value = { "/add" }, method = RequestMethod.POST)
    public Result add(@RequestBody HtBoaInPositionRole htBoaInPositionRole) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "boaInRoleInfo: " + htBoaInPositionRole, sl);
        HtBoaInPositionRole u = null;
        if(htBoaInPositionRole.getId()!=null && htBoaInPositionRole.getId()>0) {
        	u = htBoaInPositionRoleService.findById(htBoaInPositionRole.getId());
        	if(u==null) {
        		u = new HtBoaInPositionRole();
        	}
        }else {
        	u = new HtBoaInPositionRole();
        }
       
        u.setCreatedDatetime(new Date());
        u.setLastModifiedDatetime(new Date());
        u.setDelFlag(0);
        u.setRoleCode(htBoaInPositionRole.getRoleCode());
        u.setPositionCode(htBoaInPositionRole.getPositionCode());
        u.setRootOrgCode("HT");
        if(htBoaInPositionRole.getId()!=null && htBoaInPositionRole.getId()>0) {
        	u.setId(htBoaInPositionRole.getId());
        	u = htBoaInPositionRoleService.update(u);
        } else {
        	u.setCreatedDatetime(new Date());
        	u.setCreateOperator("10001");
            u = htBoaInPositionRoleService.add(u);
        }
        el = System.currentTimeMillis();
        log.info(logEnd, "boaInRoleInfo: " + htBoaInPositionRole, msg, el, el - sl);
        return Result.buildSuccess();
       // return new ResponseModal(200, msg, u);
    }
    
    @SuppressWarnings({ "rawtypes", "unused" })
	@ApiOperation(value = "对内：禁用/启用岗位角色", notes = "禁用/启用岗位角色")
    @RequestMapping(value = { "/stop" }, method = RequestMethod.POST)
    public Result stop(  Long id,  String status) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        //log.info(logStart, "boaInRoleInfo: " + boaInRoleInfo, sl);
        HtBoaInPositionRole u = null;
        if(id>0) {
        	u = htBoaInPositionRoleService.findById(id);
        }else {
        	return Result.buildFail();
        }
        if(u==null) {
    		return Result.buildFail();
    	}
        u.setLastModifiedDatetime(new Date());
        u.setDelFlag(Integer.parseInt(status));
        u = htBoaInPositionRoleService .update(u);
        
        el = System.currentTimeMillis();
        log.info(logEnd, "boaInRoleInfo: " + u, msg, el, el - sl);
        return Result.buildSuccess();
    }
    
    @ApiOperation(value = "对内：删除岗位角色记录")
    @RequestMapping(value = {"/delete" }, method = RequestMethod.POST)
    public Result delete(  Long id) {
        HtBoaInPositionRole u = new HtBoaInPositionRole();
        u.setId(id);
        htBoaInPositionRoleService.delete(u);
        return Result.buildSuccess();
    }
}
