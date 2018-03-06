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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ht.ussp.common.Constants;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.*;
import com.ht.ussp.uc.app.model.BoaInOrgInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInBmUserService;
import com.ht.ussp.uc.app.service.HtBoaInContrastService;
import com.ht.ussp.uc.app.service.HtBoaInLoginService;
import com.ht.ussp.uc.app.service.HtBoaInOrgService;
import com.ht.ussp.uc.app.service.HtBoaInPositionService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;
import com.ht.ussp.uc.app.vo.PageVo;
import com.ht.ussp.util.EncryptUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private HtBoaInContrastService htBoaInContrastService;
    
    @Autowired
    private HtBoaInLoginService htBoaInLoginService;
    
    @Autowired
    private HtBoaInBmUserService htBoaInBmUserService;
    

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
        u.setParentOrgCode(boaInOrgInfo.getParentOrgCode());
        u.setRootOrgCode(boaInOrgInfo.getRootOrgCode());
        u.setOrgPath(boaInOrgInfo.getOrgPath());
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
    public Result stop(long id, String status, @RequestHeader("userId") String userId) {
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

    @PostMapping(value = "/dataConver")
    public void importOrgExcel() {
        //List<HtBoaInOrg> orgList = htBoaInOrgService.findAll();
       List<HtBoaInUser> userList = htBoaInUserService.findAll();
        List<DdDeptUser> listDdDeptUser =  htBoaInOrgService.getDDUserList();

        List<DdDept> orgList = htBoaInOrgService.getDdDeptList();
        
        List<HtBoaInBmUser> listHtBoaInBmUser = htBoaInBmUserService.getHtBoaInBmUserList();
        List<HtBoaInContrast> listHtBoaInContrast = htBoaInContrastService.getHtBoaInContrastList();
        List<HtBoaInOrg> newOrgList = converOrg(orgList, "1", "D", "");

        List<HtBoaInContrast> htBoaInContrasts = new ArrayList<>();
        HtBoaInContrast htBoaInContrast;

        List<HtBoaInUser> newUserList = new ArrayList<>();
        HtBoaInUser u;

        List<HtBoaInLogin> loginList = new ArrayList<>();
        HtBoaInLogin l;

        List<String> userIdList = new ArrayList<>();
        for (DdDeptUser ddDeptUser : listDdDeptUser) {
           /* HtBoaInOrg o = newOrgList.stream().filter(org -> org.getRemark().equals(ddDeptUser.getOrgCode())).findFirst().get();
            u = new HtBoaInUser();
            int isOrgUser = ddDeptUser.getJobNumber()==null?0:1;
            String userId = generatorUserId(userIdList, o.getRootOrgCode(), 2, isOrgUser, ddDeptUser.getJobNumber());
            userIdList.add(userId);
            u.setUserId(userId);
            u.setUserName(ddDeptUser.getUserName());
            u.setOrgCode(o.getOrgCode());
            u.setRootOrgCode(o.getRootOrgCode());
            u.setOrgPath(o.getOrgPath());
            u.setEmail(ddDeptUser.getEmail());
            u.setMobile(ddDeptUser.getMobile());
            u.setJobNumber(ddDeptUser.getJobNumber());
            u.setIsOrgUser(isOrgUser);
            u.setDelFlag(Constants.DEL_0);
            u.setCreateOperator("自动同步");
            u.setUpdateOperator("自动同步");
            u.setDataSource(2);

            System.out.printf("%-30s%-20s%-20s%-20s%-20s%-20s\n", ddDeptUser.getUserId(), u.getUserId(), u.getJobNumber(), ddDeptUser.getOrgCode(), u.getOrgCode(), u.getUserName());


            newUserList.add(u);

            l = new HtBoaInLogin();
            l.setLoginId(UUID.randomUUID().toString().replace("-", ""));
            l.setUserId(userId);
            l.setRootOrgCode(u.getRootOrgCode());
            l.setCreateOperator("自动同步");
            l.setUpdateOperator("自动同步");
            l.setStatus("0");
            l.setFailedCount(0);
            l.setDelFlag(0);
            loginList.add(l);
 

            htBoaInContrast = new HtBoaInContrast();
            htBoaInContrast.setType("20");
            htBoaInContrast.setUcBusinessId(userId);
            htBoaInContrast.setDdBusinessId(ddDeptUser.getUserId());
           // htBoaInContrast.setBmBusinessId(user.getUserId());
            htBoaInContrast.setContrast("自动对照");
            htBoaInContrasts.add(htBoaInContrast);*/
        	
        	//对照信贷系统用
        	List<HtBoaInBmUser> htBoaInBmUsers1 = listHtBoaInBmUser.stream().filter(bmuser -> bmuser.getMobile()!=null).collect(Collectors.toList());
        	Optional<HtBoaInBmUser> htBoaInBmUserOptional = htBoaInBmUsers1.stream().filter(bmuser -> bmuser.getMobile().equals(ddDeptUser.getMobile())).findFirst();
        	HtBoaInBmUser htBoaInBmUser = null;
        	if(htBoaInBmUserOptional.isPresent()) {
        		htBoaInBmUser = htBoaInBmUserOptional.get();
        	}
        	if(htBoaInBmUser!=null) {
        		HtBoaInUser htBoaInUser = new HtBoaInUser();
        		List<HtBoaInUser> userList1 = userList.stream().filter(user -> user.getMobile()!=null).collect(Collectors.toList());
        		Optional<HtBoaInUser> listHtBoaInUser = userList1.stream().filter(user -> user.getMobile().equals(ddDeptUser.getMobile())).findFirst();
        		if(listHtBoaInUser.isPresent()) {
        			htBoaInUser = listHtBoaInUser.get();
        		}
        		if(htBoaInUser!=null) {
        			String userid=htBoaInUser.getUserId();
        			List<HtBoaInContrast> listHtBoaInContrast2 = listHtBoaInContrast.stream().filter(bmuser -> "20".equals(bmuser.getType())).collect(Collectors.toList());
            		Optional<HtBoaInContrast> htBoaInContrastOptional =  listHtBoaInContrast2.stream().filter(bmuser -> bmuser.getUcBusinessId().equals(userid)).findFirst();
            		
            		if(htBoaInContrastOptional.isPresent()) {
            			htBoaInContrast = htBoaInContrastOptional.get();
            			if(htBoaInContrast!=null) {
                    		htBoaInContrast.setBmBusinessId(htBoaInBmUser.getUserId());
                    		htBoaInContrasts.add(htBoaInContrast);
                    	}
            		}
        		}
        	}
        }

       /* for (HtBoaInOrg org : newOrgList) {  
            System.out.printf("%-20s%-20s%-20s%-80s%-10s%-15s%-40s\n", org.getOrgCode(), org.getParentOrgCode(), org.getRootOrgCode(), org.getOrgPath(), org.getSequence(), org.getRemark(), org.getOrgNameCn());
            htBoaInContrast = new HtBoaInContrast();
            htBoaInContrast.setType("10");
            htBoaInContrast.setUcBusinessId(org.getOrgCode());
            htBoaInContrast.setDdBusinessId(org.getRemark());
            htBoaInContrast.setContrast("自动对照");
            org.setRemark(null);
            htBoaInContrasts.add(htBoaInContrast);
        }*/

       /* htBoaInUserService.add(newUserList);
        htBoaInLoginService.add(loginList);*/
  //        htBoaInOrgService.add(newOrgList);//保存转换后的数据
          htBoaInContrastService.add(htBoaInContrasts);//保存对照关系
     }

    /**
     * 生成用户编码<br>
     * 编码规则：<br>
     * 有工号：根机构编码+1+数据来源+是否是机构用户+工号的数字部分（不包含“HX-”）<br>
     * 无工号：根机构编码+0+数据来源+是否是机构用户+5位纯数字随机数 <br>
     *
     * @param userIds     用户编码集，用于判断用户编码是否重复
     * @param rootOrgCode 根机构编码
     * @param isOrgUser   是否是机构用户
     * @param dataSource  数据来源
     * @param jobNumber   工号
     * @return 用户编码
     * @author 谭荣巧
     * @Date 2018/3/5 0:57
     */
    private String generatorUserId(List<String> userIds, String rootOrgCode, int isOrgUser, int dataSource, String jobNumber) {
        String userId;
        String oc = rootOrgCode.replace("D", "");
        if (jobNumber != null && jobNumber.contains("HX-")) {
            userId = String.format("%s%s%s%s%s", oc, 1, dataSource, isOrgUser, jobNumber.replace("HX-", ""));
            if (userIds.contains(userId)) {
                userId = String.format("%s%s%s%s%s", oc, 1, dataSource, isOrgUser, generateNumber(5));
            }
        } else {
            userId = String.format("%s%s%s%s%s", oc, 0, dataSource, isOrgUser, generateNumber(5));
            if (userIds.contains(userId)) {
                userId = String.format("%s%s%s%s%s", oc, 0, dataSource, isOrgUser, generateNumber(5));
            }
        }

        return userId;
    }

    private String generateNumber(int length) {
        String no = "";
        //初始化备选数组
        int[] defaultNums = new int[10];
        for (int i = 0; i < defaultNums.length; i++) {
            defaultNums[i] = i;
        }

        Random random = new Random();
        int[] nums = new int[length];
        //默认数组中可以选择的部分长度
        int canBeUsed = defaultNums.length;
        //填充目标数组
        for (int i = 0; i < nums.length; i++) {
            //将随机选取的数字存入目标数组
            int index = random.nextInt(canBeUsed);
            nums[i] = defaultNums[index];
            canBeUsed--;
        }
        if (nums.length > 0) {
            for (int i = 0; i < nums.length; i++) {
                no += nums[i];
            }
        }
        return no;
    }

    private List<HtBoaInOrg> converOrg(List<DdDept> orgList, String orgCode, String newOrgCode, String path) {
        List<HtBoaInOrg> newOrgList = new ArrayList<>();
        int i = 0;
        HtBoaInOrg newOrg;
        for (DdDept ddDept : orgList) {
            if (orgCode.equals(ddDept.getParentId())) {
                newOrg = new HtBoaInOrg();
                String orgCodeTemp = String.format("%s%02d", newOrgCode, (++i));
                String newPath;
                if (StringUtils.isEmpty(path)) {
                    newPath = orgCodeTemp;
                } else {
                    newPath = String.format("%s/%s", path, orgCodeTemp);
                    newOrg.setParentOrgCode(newOrgCode);
                }
                newOrg.setRootOrgCode(newPath.split("/")[0]);
                newOrg.setOrgCode(orgCodeTemp);
                newOrg.setOrgPath(newPath);
                newOrg.setOrgNameCn(ddDept.getDeptName());
                newOrg.setCreateOperator("");
                newOrg.setUpdateOperator("");
                newOrg.setDataSource(3);
                newOrg.setSequence(i);
                newOrg.setRemark(ddDept.getDeptId());//把钉钉组织机构id存入该字段
                newOrgList.add(newOrg);
                //System.out.printf("%-20s%-15s%-15s%-80s%-40s\n", newOrgCode, org.getOrgCode(), orgCode, newPath, org.getOrgNameCn());
                newOrgList.addAll(converOrg(orgList, ddDept.getDeptId(), orgCodeTemp, newPath));
            }
        }
        return newOrgList;
    }
}
