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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.common.Constants;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.model.BoaInOrgInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInOrgService;
import com.ht.ussp.uc.app.vo.PageVo;

import io.swagger.annotations.ApiImplicitParam;
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
        log.info(logStart, "page: " + page, sl);
        // Object o = htBoaInOrgService.findAllByPage(pageConf);
        Page<BoaInOrgInfo> pageData = (Page<BoaInOrgInfo>) htBoaInOrgService.findAllByPage(pageConf, page.getQuery());
        el = System.currentTimeMillis();
        log.info(logEnd, "page: " + page, msg, el, el - sl);
        // return new ResponseModal(200, msg, o);
        if (pageData != null) {
            result.count(pageData.getTotalElements()).data(pageData.getContent());
        }
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
        return result;
    }

    @SuppressWarnings({"unused", "rawtypes"})
    @ApiOperation(value = "对内：新增/编辑机构记录", notes = "提交机构基础信息新增/编辑机构")
    @ApiImplicitParam(name = "boaInOrgInfo", value = "机构信息实体", required = true, dataType = "BoaInOrgInfo")
    @PostMapping(value = {"/add"}, produces = {"application/json"} )
    public Result add(@RequestBody BoaInOrgInfo boaInOrgInfo) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "机构记录查询：org/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "boaInOrgInfo: " + boaInOrgInfo, sl);
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
            u = htBoaInOrgService.update(u);
        } else {
            u.setCreatedDatetime(new Date());
            u.setCreateOperator("1000");
            u.setOrgPath(boaInOrgInfo.getOrgPath() + boaInOrgInfo.getOrgCode() + "/");
            u = htBoaInOrgService.add(u);
        }

        el = System.currentTimeMillis();
        log.info(logEnd, "boaInOrgInfo: " + boaInOrgInfo, msg, el, el - sl);
        return Result.buildSuccess();
    }


    @SuppressWarnings("rawtypes")
    @ApiOperation(value = "对内：删除标记机构记录", notes = "提交机构编号，可批量删除")
    @PostMapping(value = {"/delete"}, produces = {"application/json"} )
    public Result delete(long id) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "机构记录删除：org/delete param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "codes: " + id, sl);
        HtBoaInOrg u = htBoaInOrgService.findById(id);
        u.setDelFlag(Constants.DEL_1);
        u.setUpdateOperator("del");
        u.setLastModifiedDatetime(new Date());
        htBoaInOrgService.update(u);
        el = System.currentTimeMillis();
        log.info(logEnd, "codes: " + id, msg, el, el - sl);
        return Result.buildSuccess();
    }

    @SuppressWarnings("rawtypes")
    @ApiOperation(value = "对内：禁用/启用机构")
    @PostMapping(value = {"/stop"}, produces = {"application/json"} )
    public Result stop(long id, String status) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "机构记录删除：org/delete param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "codes: " + id, sl);
        HtBoaInOrg u = htBoaInOrgService.findById(id);
        u.setLastModifiedDatetime(new Date());
        htBoaInOrgService.update(u);
        el = System.currentTimeMillis();
        log.info(logEnd, "codes: " + id, msg, el, el - sl);
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
