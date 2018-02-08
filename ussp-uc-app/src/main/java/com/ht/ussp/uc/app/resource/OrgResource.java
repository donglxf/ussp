/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: OrgResource.java
 * Author:   谭荣巧
 * Date:     2018/1/13 10:33
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.resource;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.common.Constants;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.domain.HtBoaInPosition;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.model.BoaInOrgInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInOrgService;
import com.ht.ussp.uc.app.service.HtBoaInPositionService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;
import com.ht.ussp.uc.app.vo.PageVo;
import com.ht.ussp.uc.app.vo.UserMessageVo;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

/**
 * 组织机构资源类<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/1/13 10:33
 */
@RestController
@RequestMapping(value = "/org")
@Log4j2
public class OrgResource {
    @Autowired
    private HtBoaInOrgService htBoaInOrgService;
    
    @Autowired
    private HtBoaInUserService htBoaInUserService;

    @Autowired
	private HtBoaInPositionService htBoaInPositionService;
    
    /**
     * 根据父机构编码获取组织机构树<br>
     *
     * @param parenOrgCode 父组织机构代码
     * @return zTree数据结构
     * @author 谭荣巧
     * @Date 2018/1/13 10:47
     */
    @PostMapping(value = "/tree", produces = {"application/json"})
    public List<HtBoaInOrg> getOrgTreeList(String parenOrgCode) {
        return htBoaInOrgService.getOrgTreeList(parenOrgCode);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @ApiOperation(value = "对内：机构记录查询", notes = "列出所有机构记录列表信息")
    @PostMapping(value = {"/list"}, produces = {"application/json"} )
    public PageResult<BoaInOrgInfo> list(PageVo page) {
        PageResult result = new PageResult();
        PageConf pageConf = new PageConf();
        pageConf.setPage(page.getPage());
        pageConf.setSize(page.getLimit());
        pageConf.setSearch(page.getKeyWord());
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "机构记录查询：org/list param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "page: " + page, sl);
        Page<BoaInOrgInfo> pageData = (Page<BoaInOrgInfo>) htBoaInOrgService.findAllByPage(pageConf, page.getQuery());
        el = System.currentTimeMillis();
        log.debug(logEnd, "page: " + page, msg, el, el - sl);
        if (pageData != null) {
            result.count(pageData.getTotalElements()).data(pageData.getContent());
        }
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
        return result;
    }

    @SuppressWarnings({"unused", "rawtypes"})
    @ApiOperation(value = "对内：新增/编辑机构记录")
    @PostMapping(value = {"/add"}, produces = {"application/json"} )
    public Result add(@RequestBody BoaInOrgInfo boaInOrgInfo,@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "机构记录查询：org/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "boaInOrgInfo: " + boaInOrgInfo, sl);
        HtBoaInOrg u = null;
        if (boaInOrgInfo.getId() > 0) {
            u = htBoaInOrgService.findById(boaInOrgInfo.getId());
            if (u == null) {
                u = new HtBoaInOrg();
            }
        } else {
            u = new HtBoaInOrg();
        }
        u.setLastModifiedDatetime(new Date());
        u.setOrgCode(boaInOrgInfo.getOrgCode());
        u.setOrgName(boaInOrgInfo.getOrgName());
        u.setOrgNameCn(boaInOrgInfo.getOrgNameCn());
        u.setParentOrgCode(boaInOrgInfo.getParentOrgCode());
        u.setRootOrgCode(boaInOrgInfo.getRootOrgCode());
        u.setOrgPath(boaInOrgInfo.getOrgPath());
        if (boaInOrgInfo.getId() > 0) {
            u.setId(boaInOrgInfo.getId());
            u.setUpdateOperator(userId);
            u = htBoaInOrgService.update(u);
        } else {
            u.setCreatedDatetime(new Date());
            u.setCreateOperator(userId);
            u.setOrgPath(boaInOrgInfo.getOrgPath() + boaInOrgInfo.getOrgCode() + "/");
            u = htBoaInOrgService.add(u);
        }

        el = System.currentTimeMillis();
        log.debug(logEnd, "boaInOrgInfo: " + boaInOrgInfo, msg, el, el - sl);
        return Result.buildSuccess();
    }


    @SuppressWarnings("rawtypes")
    @ApiOperation(value = "对内：删除标记机构记录")
    @PostMapping(value = {"/delete"}, produces = {"application/json"} )
    public Result delete(long id,@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "机构记录删除：org/delete param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "codes: " + id, sl);
        HtBoaInOrg u = htBoaInOrgService.findById(id);
        //机构下有机构，有人员， 有岗位都不可以删除
        HtBoaInOrg htBoaInOrg = new HtBoaInOrg();
        htBoaInOrg.setParentOrgCode(u.getOrgCode());
 		List<HtBoaInOrg> listHtBoaInOrg = htBoaInOrgService.findAll(htBoaInOrg);
		if(!listHtBoaInOrg.isEmpty()) {
			return Result.buildFail("该机构下存在子机构，不可删除！", "该机构下存在子机构，不可删除！");
		}
		HtBoaInUser htBoaInUser = new HtBoaInUser();
		htBoaInUser.setOrgCode(u.getOrgCode());
		List<HtBoaInUser>  listHtBoaInUser = htBoaInUserService.findAll(htBoaInUser);
		if (!listHtBoaInUser.isEmpty()) {
			return Result.buildFail("该机构下存在用户，不可删除！", "该机构下存在用户，不可删除！");
		}
		HtBoaInPosition htBoaInPosition = new HtBoaInPosition();
		htBoaInPosition.setParentOrgCode(u.getOrgCode());
		List<HtBoaInPosition> listHtBoaInPosition = htBoaInPositionService.findAll(htBoaInPosition);
		if (!listHtBoaInPosition.isEmpty()) {
			return Result.buildFail("该机构下存在可用岗位，不可删除！", "该机构下存在可用岗位，不可删除！");
		}
		
        u.setDelFlag(Constants.DEL_1);
        u.setUpdateOperator(userId);
        u.setLastModifiedDatetime(new Date());
        htBoaInOrgService.update(u);
        el = System.currentTimeMillis();
        log.debug(logEnd, "codes: " + id, msg, el, el - sl);
        return Result.buildSuccess();
    }

    @SuppressWarnings("rawtypes")
    @ApiOperation(value = "对内：禁用/启用机构")
    @PostMapping(value = {"/stop"}, produces = {"application/json"} )
    public Result stop(long id, String status,@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "机构记录删除：org/delete param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "codes: " + id, sl);
        HtBoaInOrg u = htBoaInOrgService.findById(id);
        u.setLastModifiedDatetime(new Date());
        u.setUpdateOperator(userId);
        htBoaInOrgService.update(u);
        el = System.currentTimeMillis();
        log.debug(logEnd, "codes: " + id, msg, el, el - sl);
        return Result.buildSuccess();
    }
    
    @SuppressWarnings("rawtypes")
   	@ApiOperation(value = "对内：机构编码是否可用  true：可用  false：不可用")
    @PostMapping(value = {"/isExistOrgCode" }, produces = {"application/json"} )
    public Result isExistOrgCode( String orgCode) {
       List<HtBoaInOrg> listHtBoaInOrg = htBoaInOrgService.findByOrgCode(orgCode);
       if(listHtBoaInOrg.isEmpty()) {
    	   return Result.buildSuccess();
       }else {
    	   return Result.buildFail();
       }
    }
}
