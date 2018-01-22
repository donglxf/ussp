package com.ht.ussp.uc.app.resource;

import java.util.Date;

import com.ht.ussp.uc.app.vo.PageVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInPosition;
import com.ht.ussp.uc.app.domain.HtBoaInPositionRole;
import com.ht.ussp.uc.app.model.BoaInPositionInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInPositionRoleService;
import com.ht.ussp.uc.app.service.HtBoaInPositionService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
/**
 * 
 * @ClassName: HtBoaInPositionResource
 * @Description: 岗位信息
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月11日 上午10:46:23
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/position")
public class PositionResource {

    private static final Logger log = LoggerFactory
            .getLogger(PositionResource.class);

    @Autowired
    private HtBoaInPositionService htBoaInPositionService;
    @Autowired
    private HtBoaInPositionRoleService htBoaInPositionRoleService;
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value = "对内：岗位记录查询", notes = "列出所有岗位记录列表信息")
    /*@ApiImplicitParam(name = "pageConf", value = "分页信息实体", required = true, dataType = "PageConf")*/
    @RequestMapping(value = {"/in/list" }, method = RequestMethod.POST)
    public PageResult<BoaInPositionInfo> list(PageVo page) {
    	PageResult result = new PageResult();
    	PageConf pageConf = new PageConf();
    	pageConf.setPage(page.getPage());
    	pageConf.setSize(page.getLimit());
    	pageConf.setSearch(page.getKeyWord());
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "岗位记录查询：position/in/list param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "page: " + page, sl);
        //Object o = htBoaInPositionService.findAllByPage(pageConf); 
        Page<BoaInPositionInfo> pageData = (Page<BoaInPositionInfo>) htBoaInPositionService.findAllByPage(pageConf,page.getQuery());
        el = System.currentTimeMillis();
        log.info(logEnd, "page: " + page, msg, el, el - sl);
        //return new ResponseModal(200, msg, o);
        if (pageData != null) {
            result.count(pageData.getTotalElements()).data(pageData.getContent());
        }
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
        return result;
    }
    
    @ApiOperation(value = "对内：新增/编辑岗位记录", notes = "提交岗位基础信息新增/编辑岗位")
    @ApiImplicitParam(name = "boaInPositionInfo", value = "岗位信息实体", required = true, dataType = "BoaInPositionInfo")
    @RequestMapping(value = {"/in/add" }, method = RequestMethod.POST)
    public Result add(@RequestBody BoaInPositionInfo boaInPositionInfo) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "岗位记录查询：position/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "boaInPositionInfo: " + boaInPositionInfo, sl);
        HtBoaInPosition u = null;
        if(boaInPositionInfo.getId()>0) {
        	u = htBoaInPositionService.findById(boaInPositionInfo.getId());
        	if(u==null) {
        		u = new HtBoaInPosition();
        	}
        }else {
        	u = new HtBoaInPosition();
        }
        u.setLastModifiedDatetime(new Date());
        u.setOrgPath(boaInPositionInfo.getOrgPath());
        u.setParentOrgCode(boaInPositionInfo.getPOrgCode());
        u.setPositionName(boaInPositionInfo.getPositionName());
        u.setPositionNameCn(boaInPositionInfo.getPositionNameCn()==null?boaInPositionInfo.getPositionName():boaInPositionInfo.getPositionNameCn());
        u.setRootOrgCode(boaInPositionInfo.getROrgCode());
        u.setSequence(boaInPositionInfo.getSequence()==null?0:boaInPositionInfo.getSequence());
        u.setPositionCode(boaInPositionInfo.getPositionCode());
        if(boaInPositionInfo.getId()>0) {
        	u.setId(boaInPositionInfo.getId());
        	u = htBoaInPositionService.update(u);
        } else {
        	u.setCreatedDatetime(new Date());
            u.setCreateOperator("1000");
            u = htBoaInPositionService.add(u);
        }
            
        el = System.currentTimeMillis();
        log.info(logEnd, "boaInPositionInfo: " + boaInPositionInfo, msg, el, el - sl);
        return Result.buildSuccess();
        //return new ResponseModal(200, msg, u);
    }
    
    @ApiOperation(value = "对内：删除岗位记录", notes = "提交岗位编号，可批量删除")
    /*@ApiImplicitParam(name = "codes", value = "岗位编号集", required = true, dataType = "Codes")*/
    @RequestMapping(value = {"/in/delete" }, method = RequestMethod.POST)
    public Result delete(int id) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "岗位记录删除：position/in/delete param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "codes: " + id, sl);
        htBoaInPositionService.delete(id);
        el = System.currentTimeMillis();
        log.info(logEnd, "codes: " + id, msg, el, el - sl);
        return Result.buildSuccess();
    }
    
    @ApiOperation(value = "对内：岗位绑定角色", notes = "提交岗位编号和角色编号进行绑定")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "positionCode", value = "岗位编号", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "roleCode", value = "角色编号", required = true, paramType = "form", dataType = "string") })
    @RequestMapping(value = { "/in/binding_role" }, method = RequestMethod.POST)
    public ResponseModal bindingRole(@RequestParam String positionCode,
            @RequestParam String roleCode) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "岗位绑定角色：position/in/binding_role param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "positionCode: " + positionCode + " roleCode: " + roleCode, sl);
        HtBoaInPositionRole u = new HtBoaInPositionRole();
        u.setPositionCode(positionCode);
        u.setRoleCode(roleCode);
        u = htBoaInPositionRoleService.add(u);
        el = System.currentTimeMillis();
        log.info(logEnd, "positionCode: " + positionCode + " roleCode: " + roleCode, msg, el, el - sl);
        return new ResponseModal("200", msg, u);
    }
    
    @ApiOperation(value = "对内：岗位解绑角色", notes = "提交岗位编号和角色编号进行解绑")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "positionCode", value = "岗位编号", required = true, paramType = "form", dataType = "string"),
        @ApiImplicitParam(name = "roleCode", value = "角色编号", required = true, paramType = "form", dataType = "string") })
    @RequestMapping(value = {
            "/in/unbind_role" }, method = RequestMethod.DELETE)
    public ResponseModal unbindRole(@RequestParam String positionCode, @RequestParam String roleCode) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "岗位解绑角色：position/in/unbind_role param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "positionCode: " + positionCode + " roleCode: " + roleCode, sl);
        htBoaInPositionRoleService.delete(positionCode, roleCode);
        el = System.currentTimeMillis();
        log.info(logEnd, "positionCode: " + positionCode + " roleCode: " + roleCode, msg, el, el - sl);
        return new ResponseModal("200", msg);
    }

}
