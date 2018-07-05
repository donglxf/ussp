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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
    @PostMapping(value = {"/list"}, produces = {"application/json"})
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
    @PostMapping(value = {"/add"}, produces = {"application/json"})
    public Result add(@RequestBody BoaInOrgInfo boaInOrgInfo, @RequestHeader("userId") String userId) {
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
        u.setOrgType(boaInOrgInfo.getOrgType());
        u.setParentOrgCode(boaInOrgInfo.getParentOrgCode());
        u.setRootOrgCode(boaInOrgInfo.getRootOrgCode());
        u.setOrgPath(boaInOrgInfo.getOrgPath());
        u.setDataSource(Constants.USER_DATASOURCE_1);
        if (boaInOrgInfo.getId() > 0) {
            u.setId(boaInOrgInfo.getId());
            u.setUpdateOperator(userId);
            u = htBoaInOrgService.update(u);
        } else {
        	u.setDelFlag(Constants.DEL_0);
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
    @PostMapping(value = {"/stop"}, produces = {"application/json"})
    public Result stop(long id, Integer status, @RequestHeader("userId") String userId) {
        HtBoaInOrg u = htBoaInOrgService.findById(id);
        u.setDelFlag(status);
        u.setLastModifiedDatetime(new Date());
        u.setUpdateOperator(userId);
        htBoaInOrgService.update(u);
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
    
    @ApiOperation(value = "根据机构编码查询机构信息")
    @GetMapping(value = "/getOrgInfoByCode")
    public HtBoaInOrg getOrgInfoByCode(String orgCode) {
    	List<HtBoaInOrg> listHtBoaInOrg = htBoaInOrgService.findByOrgCode(orgCode);
    	if(listHtBoaInOrg==null||listHtBoaInOrg.isEmpty()) {
    		return null;
    	} 
        return listHtBoaInOrg.get(0);
    }
        
    @ApiOperation(value = "根据机构编码查询下级机构信息")
    @GetMapping(value = "/getSubOrgInfoByCode")
    public List<HtBoaInOrg> getSubOrgInfoByCode(String parentOrgCode) {
        return htBoaInOrgService.findByParentOrgCode(parentOrgCode);
    }
    
    @ApiOperation(value = "根据时间获取指定时间机构信息")
    @GetMapping(value = "/getOrgListByTime")
    public List<HtBoaInOrg> getOrgListByTime(@RequestParam("startTime")String startTime, @RequestParam("endTime")String endTime) {
        return htBoaInOrgService.getOrgListByTime(startTime,endTime);
    }
    
    /**
     * 获取用户所属机构类型信息 (10:公司 20:中心 30:片区 40:分公司 50部门 60小组)
     *  资源类型枚举值：OrgTypeEnum.ORG_TYPE_COMPANY.getReturnCode()
     * @return
     */
    @ApiOperation(value = "获取用户所属机构类型信息")
    @GetMapping(value = "/getOrgInfoByOrgType")
    public HtBoaInOrg getOrgInfoByOrgType(@RequestParam("orgCode")String orgCode,@RequestParam("orgType") String orgType) {
        return htBoaInOrgService.getOrgInfoByOrgType(orgCode,orgType);
    }
 
    @ApiOperation(value = "产生新的orgcode")
    @GetMapping(value = "/getNewOrgCode")
    public String getNewOrgCode(String parentOrgCode) {
    	//1.查看父级下有多少机构 然后再最大机构上加1
    	List<HtBoaInOrg> listHtBoaInOrg=htBoaInOrgService.findByParentOrgCode(parentOrgCode);
    	int size = 0;
    	if(listHtBoaInOrg==null || listHtBoaInOrg.isEmpty()) {
    		size = 1;
    	}else {
    		size = listHtBoaInOrg.size()+1;
    	}
        return String.format("%s%02d", parentOrgCode, size);
    }
    
    
    /**
     * 导出
     */
    @PostMapping(value = "/exportOrgExcel")  
    @ResponseBody  
    public void exportOrgExcel(HttpServletResponse response){  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssms");  
        String dateStr = sdf.format(new Date());  
        // 指定下载的文件名  
        response.setHeader("Content-Disposition", "attachment;filename=" +dateStr+".xlsx"); 
        System.out.println(dateStr);
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");  
        response.setHeader("Pragma", "no-cache");  
        response.setHeader("Cache-Control", "no-cache");  
        response.setDateHeader("Expires", 0);  
        XSSFWorkbook workbook=null;  
        try {  
            //导出Excel对象  
            workbook = htBoaInOrgService.exportOrgExcel();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        OutputStream output;
        try {
            output = response.getOutputStream();
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(output);
            bufferedOutPut.flush();
            workbook.write(bufferedOutPut);
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 导入
     *
     * @param request
     * @param response
     */
    @PostMapping(value = "/importOrgExcel")
    public Result importOrgExcel(HttpServletRequest request, HttpServletResponse response, @RequestHeader("userId") String userId) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            List<MultipartFile> fileList = multipartRequest.getFiles("file");
            if (fileList.isEmpty()) {
                throw new Exception("文件不存在！");
            }
            MultipartFile file = fileList.get(0);
            if (file == null || file.isEmpty()) {
                throw new Exception("文件不存在！");
            }
            InputStream in = file.getInputStream();
            htBoaInOrgService.importOrgExcel(in, file, userId);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.buildFail();
        }
        return Result.buildSuccess();
    }
}
