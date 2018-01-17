package com.ht.ussp.uc.app.resource;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInRole;
import com.ht.ussp.uc.app.model.BoaInRoleInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInRoleService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @ClassName: HtBoaInRoleResource
 * @Description: 角色信息
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月12日 下午02:06:13
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/role")
public class RoleResource {

    private static final Logger log = LoggerFactory.getLogger(RoleResource.class);

    @Autowired
    private HtBoaInRoleService htBoaInRoleService;
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ApiOperation(value = "对内：角色记录查询", notes = "列出所有角色记录列表信息")
   /* @ApiImplicitParam(name = "pageConf", value = "分页信息实体", required = true, dataType = "PageConf")*/
    @RequestMapping(value = {"/in/list" }, method = RequestMethod.POST)
    public PageResult<BoaInRoleInfo> list(com.ht.ussp.uc.app.vo.Page page) {
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
        log.info(logStart, "pageConf: " + pageConf, sl);
        /*Object o = htBoaInRoleService.findAllByPage(pageConf);*/
        Page<BoaInRoleInfo> pageData = (Page<BoaInRoleInfo>) htBoaInRoleService.findAllByPage(pageConf,page.getQuery());
        el = System.currentTimeMillis();
        log.info(logEnd, "pageConf: " + pageConf, msg, el, el - sl);
        if (pageData != null) {
            result.count(pageData.getTotalElements()).data(pageData.getContent());
        }
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
        return result;
    }
    
    @ApiOperation(value = "对内：新增/编辑角色记录", notes = "提交角色基础信息新增/编辑角色")
    @ApiImplicitParam(name = "boaInRoleInfo", value = "角色信息实体", required = true, dataType = "BoaInRoleInfo")
    @RequestMapping(value = { "/in/add" }, method = RequestMethod.POST)
    public Result add(@RequestBody BoaInRoleInfo boaInRoleInfo) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "boaInRoleInfo: " + boaInRoleInfo, sl);
        HtBoaInRole u = null;
        if(boaInRoleInfo.getId()>0) {
        	u = htBoaInRoleService.findById(boaInRoleInfo.getId());
        	if(u==null) {
        		u = new HtBoaInRole();
        	}
        }else {
        	u = new HtBoaInRole();
        }
        u.setRoleCode(boaInRoleInfo.getRoleCode());
        u.setLastModifiedDatetime(new Date());
        u.setRoleName(boaInRoleInfo.getRoleName());
        u.setRoleNameCn(boaInRoleInfo.getRoleNameCn());
        u.setRootOrgCode(boaInRoleInfo.getROrgCode());
        u.setStatus(boaInRoleInfo.getStatus());
        if(boaInRoleInfo.getId()>0) {
        	u.setId(boaInRoleInfo.getId());
        	u = htBoaInRoleService.update(u);
        } else {
        	u.setCreatedDatetime(new Date());
            u.setCreateOperator("1000");
            u = htBoaInRoleService.add(u);
        }
        el = System.currentTimeMillis();
        log.info(logEnd, "boaInRoleInfo: " + boaInRoleInfo, msg, el, el - sl);
        return Result.buildSuccess();
       // return new ResponseModal(200, msg, u);
    }
    
    @ApiOperation(value = "对内：删除角色记录", notes = "提交角色编号，可批量删除")
    @ApiImplicitParam(name = "codes", value = "角色编号集", required = true, dataType = "Codes")
    @RequestMapping(value = {"/in/delete/{id}" }, method = RequestMethod.POST)
    public Result delete(@PathVariable int id) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "角色记录删除：role/in/delete param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "codes: " + id, sl);
        htBoaInRoleService.delete(id);
        el = System.currentTimeMillis();
        log.info(logEnd, "codes: " + id, msg, el, el - sl);
        return Result.buildSuccess();
        //return new ResponseModal(200, msg);
    }


}
