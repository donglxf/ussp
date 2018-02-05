package com.ht.ussp.uc.app.resource;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInPositionUser;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInPositionUserService;
import com.ht.ussp.uc.app.vo.PageVo;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

/**
 * @Description: 用户岗位
 * @date 2018年1月10日 下午14:44:16
 */

@RestController
@RequestMapping(value = "/userposition")
@Log4j2
public class UserPositionResource {

    @Autowired
    private HtBoaInPositionUserService htBoaInPositionUserService;

    @SuppressWarnings("rawtypes")
   	@ApiOperation(value = "对内：根据UserId查询用户岗位", notes = "根据UserId查询用户岗位")
       @PostMapping(value = { "/listUserPositionByPage"}, produces = {"application/json"})
       public PageResult<HtBoaInPositionUser> listUserPositionByPage(PageVo page) {
       	PageResult result = new PageResult();
       	PageConf pageConf = new PageConf();
       	pageConf.setPage(page.getPage());
       	pageConf.setSize(page.getLimit());
       	pageConf.setSearch(page.getKeyWord());
           long sl = System.currentTimeMillis(), el = 0L;
           String msg = "成功";
           String logHead = "根据UserId查询用户岗位：user/listUserPositionByPage param-> {}";
           String logStart = logHead + " | START:{}";
           String logEnd = logHead + " {} | END:{}, COST:{}";
           log.info(logStart, "page: " + page, sl);
           
           result =  htBoaInPositionUserService.listPositionUserByPage(pageConf,page.getQuery()); 
           el = System.currentTimeMillis();
           log.info(logEnd, "page: " + page, msg, el, el - sl);
           return result;
       }

    @ApiOperation(value = "对内：新增/编辑用户岗位记录", notes = "提交用户岗位信息新增/编辑角色")
    @PostMapping(value = { "/add" }, produces = {"application/json"})
    public Result add(@RequestBody HtBoaInPositionUser htBoaInPositionUser,@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "boaInRoleInfo: " + htBoaInPositionUser, sl);
        HtBoaInPositionUser u = null;
        if(htBoaInPositionUser.getId()!=null && htBoaInPositionUser.getId()>0) {
        	u = htBoaInPositionUserService.findById(htBoaInPositionUser.getId());
        	if(u==null) {
        		u = new HtBoaInPositionUser();
        	}
        }else {
        	u = new HtBoaInPositionUser();
        }
       
        u.setCreatedDatetime(new Date());
        u.setLastModifiedDatetime(new Date());
        u.setDelFlag(0);
        u.setUserId(htBoaInPositionUser.getUserId());
        u.setPositionCode(htBoaInPositionUser.getPositionCode());
        if(htBoaInPositionUser.getId()!=null && htBoaInPositionUser.getId()>0) {
        	u.setId(htBoaInPositionUser.getId());
        	u.setUpdateOperator(userId);
        	u = htBoaInPositionUserService.update(u);
        } else {
        	u.setCreatedDatetime(new Date());
        	u.setCreateOperator(userId);
        	htBoaInPositionUserService.delete(u);
            u = htBoaInPositionUserService.add(u);
        }
        el = System.currentTimeMillis();
        log.info(logEnd, "boaInRoleInfo: " + htBoaInPositionUser, msg, el, el - sl);
        return Result.buildSuccess();
       // return new ResponseModal(200, msg, u);
    }
    
    @SuppressWarnings({ "rawtypes", "unused" })
	@ApiOperation(value = "对内：禁用/启用用户岗位", notes = "禁用/启用用户岗位")
    @PostMapping(value = { "/stop" }, produces = {"application/json"})
    public Result stop(Long id,String status) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        //log.info(logStart, "boaInRoleInfo: " + boaInRoleInfo, sl);
        HtBoaInPositionUser u = null;
        if(id>0) {
        	u = htBoaInPositionUserService.findById(id);
        }else {
        	return Result.buildFail();
        }
        if(u==null) {
    		return Result.buildFail();
    	}
        u.setLastModifiedDatetime(new Date());
        u.setDelFlag(Integer.parseInt(status));
        u = htBoaInPositionUserService .update(u);
        
        el = System.currentTimeMillis();
        log.info(logEnd, "boaInRoleInfo: " + u, msg, el, el - sl);
        return Result.buildSuccess();
    }
    
    @ApiOperation(value = "对内：删除用户岗位记录", notes = "提交角色编号，可批量删除")
    @ApiImplicitParam(name = "codes", value = "角色编号集", required = true, dataType = "Codes")
    @PostMapping(value = {"/delete" }, produces = {"application/json"})
    public Result delete(  int id) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "角色记录删除：role/in/delete param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "codes: " + id, sl);
        HtBoaInPositionUser u = new HtBoaInPositionUser();
        u.setId((long) id);
        htBoaInPositionUserService.delete(u);
        el = System.currentTimeMillis();
        log.info(logEnd, "codes: " + id, msg, el, el - sl);
        return Result.buildSuccess();
        //return new ResponseModal(200, msg);
    }
}
