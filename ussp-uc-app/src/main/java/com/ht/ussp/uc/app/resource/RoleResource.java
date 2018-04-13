package com.ht.ussp.uc.app.resource;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInRole;
import com.ht.ussp.uc.app.domain.HtBoaInUserApp;
import com.ht.ussp.uc.app.model.BoaInRoleInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInPositionRoleService;
import com.ht.ussp.uc.app.service.HtBoaInRoleResService;
import com.ht.ussp.uc.app.service.HtBoaInRoleService;
import com.ht.ussp.uc.app.service.HtBoaInUserAppService;
import com.ht.ussp.uc.app.service.HtBoaInUserRoleService;
import com.ht.ussp.uc.app.vo.PageVo;

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
@RequestMapping(value = "/role")
public class RoleResource {

    private static final Logger log = LoggerFactory.getLogger(RoleResource.class);

    @Autowired
    private HtBoaInRoleService htBoaInRoleService;
    
    @Autowired
    private HtBoaInUserRoleService htBoaInUserRoleService;
    
    @Autowired
    private HtBoaInPositionRoleService htBoaInPositionRoleService;
    
    @Autowired
    private HtBoaInRoleResService htBoaInRoleResService;
    
    @Autowired
    private HtBoaInUserAppService htBoaInUserAppService;

    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ApiOperation(value = "对内：角色记录查询", notes = "列出所有角色记录列表信息")
    @RequestMapping(value = {"/in/list" }, method = RequestMethod.POST)
    public PageResult<BoaInRoleInfo> list(PageVo page) {
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
        log.debug(logStart, "pageConf: " + pageConf, sl);
        Page<BoaInRoleInfo> pageData = (Page<BoaInRoleInfo>) htBoaInRoleService.findAllByPage(pageConf,page.getQuery());
        el = System.currentTimeMillis();
        log.debug(logEnd, "pageConf: " + pageConf, msg, el, el - sl);
        if (pageData != null) {
            result.count(pageData.getTotalElements()).data(pageData.getContent());
        }
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
        return result;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ApiOperation(value = "获取用户对应的系统角色")
    @RequestMapping(value = {"/userRolelist" }, method = RequestMethod.POST)
    public PageResult<BoaInRoleInfo> userRolelist(PageVo page) {
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
        log.debug(logStart, "pageConf: " + pageConf, sl);
        Map<String, String> query = page.getQuery();
        String userId = "";
        String keyWord = "";
        if (query != null && query.size() > 0 && query.get("userId") != null) {
        	userId = query.get("userId");
        }
        if (query != null && query.size() > 0 && query.get("keyWord") != null) {
        	keyWord = "%"+query.get("keyWord")+"%";
        }else {
        	keyWord = "%%";
        }
       
        
        //1.根据用户id查询对应关联的系统 然后查找对应的角色
        List<HtBoaInUserApp>  listHtBoaInUserApp = htBoaInUserAppService.getUserAppListByUserId(userId);
        List<String> appList = new ArrayList<String>();
        for(HtBoaInUserApp u:listHtBoaInUserApp) {
        	if(StringUtils.isNotBlank(u.getApp())) {
        		appList.add(u.getApp());
        	}
        }
        Page<BoaInRoleInfo> pageData = null;
        if(appList.isEmpty()) {
        	 pageData = (Page<BoaInRoleInfo>) htBoaInRoleService.findAllByPage(pageConf,page.getQuery());
        }else {
        	Object obj= htBoaInRoleService.findAllRoleByAppPage(pageConf,appList,keyWord);
        	if(obj!=null) {
        		pageData = (Page<BoaInRoleInfo>) obj;
        	}
        }
        el = System.currentTimeMillis();
        log.debug(logEnd, "pageConf: " + pageConf, msg, el, el - sl);
        if (pageData != null) {
            result.count(pageData.getTotalElements()).data(pageData.getContent());
        }
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
        return result;
    }
    
    @SuppressWarnings({ "unused", "rawtypes" })
	@ApiOperation(value = "对内：新增/编辑角色记录", notes = "提交角色基础信息新增/编辑角色")
    @RequestMapping(value = { "/in/add" }, method = RequestMethod.POST)
    public Result add(@RequestBody BoaInRoleInfo boaInRoleInfo,@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "boaInRoleInfo: " + boaInRoleInfo, sl);
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
        u.setApp(boaInRoleInfo.getApp());
        if(boaInRoleInfo.getId()>0) {
        	u.setId(boaInRoleInfo.getId());
        	u.setUpdateOperator(userId);
        	u = htBoaInRoleService.update(u);
        } else {
        	u.setCreatedDatetime(new Date());
        	u.setStatus("0");
            u.setCreateOperator(userId);
            u = htBoaInRoleService.add(u);
        }
        el = System.currentTimeMillis();
        log.debug(logEnd, "boaInRoleInfo: " + boaInRoleInfo, msg, el, el - sl);
        return Result.buildSuccess();
       // return new ResponseModal(200, msg, u);
    }
    
    @SuppressWarnings({ "unused", "rawtypes" })
	@ApiOperation(value = "对内：禁用/启用角色", notes = "禁用/启用角色")
    @RequestMapping(value = { "/in/stop" }, method = RequestMethod.POST)
    public Result stop( Long id, String status,@RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        //log.debug(logStart, "boaInRoleInfo: " + boaInRoleInfo, sl);
        HtBoaInRole u = null;
        if(id>0) {
        	u = htBoaInRoleService.findById(id);
        }else {
        	return Result.buildFail();
        }
        if(u==null) {
    		return Result.buildFail();
    	}
        u.setLastModifiedDatetime(new Date());
        u.setStatus(status);
        u.setUpdateOperator(userId);
        u = htBoaInRoleService.update(u);
        
        el = System.currentTimeMillis();
        log.debug(logEnd, "boaInRoleInfo: " + u, msg, el, el - sl);
        return Result.buildSuccess();
       // return new ResponseModal(200, msg, u);
    }
    
    @SuppressWarnings("rawtypes")
	@ApiOperation(value = "对内：物理删除角色记录", notes = "提交角色编号，可批量删除")
    @ApiImplicitParam(name = "codes", value = "角色编号集", required = true, dataType = "Codes")
    @RequestMapping(value = {"/in/deleteTrunc" }, method = RequestMethod.POST)
    public Result deleteTrunc(int id) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "角色记录删除：role/in/delete param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "codes: " + id, sl);
        htBoaInRoleService.delete(id);
        el = System.currentTimeMillis();
        log.debug(logEnd, "codes: " + id, msg, el, el - sl);
        return Result.buildSuccess();
        //return new ResponseModal(200, msg);
    }

 
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "删除角色记录")
	@RequestMapping(value = { "/in/delete" }, method = RequestMethod.POST)
	@Transactional
	public Result delete(long id) {
		long sl = System.currentTimeMillis(), el = 0L;
		String msg = "成功";
		String logHead = "删除角色记录：position/in/delete param-> {}";
		String logStart = logHead + " | START:{}";
		String logEnd = logHead + " {} | END:{}, COST:{}";
		log.debug(logStart, "codes: " + id, sl);
		HtBoaInRole u = htBoaInRoleService.findById(id);
		// 删除角色对应的人员关系，岗位角色关系，角色资源关系
		htBoaInUserRoleService.deleteByRoleCode(u.getRoleCode());

		htBoaInPositionRoleService.deleteByRoleCode(u.getRoleCode());

		htBoaInRoleResService.deleteByRoleCode(u.getRoleCode());

		/*
		 * u.setDelFlag(Constants.DEL_1); u.setStatus(Constants.STATUS_1);
		 * u.setUpdateOperator("del"); u.setLastModifiedDatetime(new Date());
		 * htBoaInRoleService.update(u);
		 */
		htBoaInRoleService.delete(id);
		el = System.currentTimeMillis();
		log.debug(logEnd, "codes: " + id, msg, el, el - sl);
		return Result.buildSuccess();

	}
     
    @SuppressWarnings("rawtypes")
    @ApiOperation(value = "对内：角色编码是否可用  true：可用  false：不可用")
    @RequestMapping(value = {"/isExistRoleCode" }, method = RequestMethod.POST)
    public Result isExistRoleCode( String roleCode) {
        List<HtBoaInRole> listHtBoaInRole = htBoaInRoleService.findByRoleCode(roleCode);
        if(listHtBoaInRole.isEmpty()) {
        	 return Result.buildSuccess();
        }else {
        	return Result.buildFail();
        }
     }
    
    /**
     * 导出
     */
    @PostMapping(value = "/exportRoleExcel")  
    @ResponseBody  
    public void exportRoleExcel(HttpServletResponse response){  
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
            workbook = htBoaInRoleService.exportRoleExcel();  
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
	 * @param request
	 * @param response
	 */
	@PostMapping(value = "/importRoleExcel")
	public Result importRoleExcel(HttpServletRequest request, HttpServletResponse response,@RequestHeader("userId") String userId) {
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
			htBoaInRoleService.importRoleExcel(in, file,userId);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.buildFail();
		}
		return Result.buildSuccess();
	}
}
