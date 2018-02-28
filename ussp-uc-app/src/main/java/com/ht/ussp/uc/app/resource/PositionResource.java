package com.ht.ussp.uc.app.resource;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.ht.ussp.uc.app.domain.HtBoaInPosition;
import com.ht.ussp.uc.app.domain.HtBoaInPositionUser;
import com.ht.ussp.uc.app.model.BoaInPositionInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInPositionRoleService;
import com.ht.ussp.uc.app.service.HtBoaInPositionService;
import com.ht.ussp.uc.app.service.HtBoaInPositionUserService;
import com.ht.ussp.uc.app.vo.PageVo;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * @ClassName: HtBoaInPositionResource
 * @Description: 岗位信息
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月11日 上午10:46:23
 */

@RestController
@RequestMapping(value = "/position")
public class PositionResource {

	private static final Logger log = LoggerFactory.getLogger(PositionResource.class);

	@Autowired
	private HtBoaInPositionService htBoaInPositionService;
	
	@Autowired
	private HtBoaInPositionRoleService htBoaInPositionRoleService;
	
	@Autowired
	private HtBoaInPositionUserService htBoaInPositionUserService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value = "对内：岗位记录查询", notes = "列出所有岗位记录列表信息")
	@PostMapping(value = { "/in/list" }, produces = { "application/json" })
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
		log.debug(logStart, "page: " + page, sl);
		Page<BoaInPositionInfo> pageData = (Page<BoaInPositionInfo>) htBoaInPositionService.findAllByPage(pageConf, page.getQuery());
		el = System.currentTimeMillis();
		log.debug(logEnd, "page: " + page, msg, el, el - sl);
		if (pageData != null) {
			result.count(pageData.getTotalElements()).data(pageData.getContent());
		}
		result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
		return result;
	}

	@SuppressWarnings({ "unused", "rawtypes" })
	@ApiOperation(value = "对内：新增/编辑岗位记录", notes = "提交岗位基础信息新增/编辑岗位")
	@PostMapping(value = { "/in/add" }, produces = { "application/json" })
	public Result add(@RequestBody BoaInPositionInfo boaInPositionInfo,@RequestHeader("userId") String userId) {
		long sl = System.currentTimeMillis(), el = 0L;
		ResponseModal r = null;
		String msg = "成功";
		String logHead = "岗位记录查询：position/in/add param-> {}";
		String logStart = logHead + " | START:{}";
		String logEnd = logHead + " {} | END:{}, COST:{}";
		log.debug(logStart, "boaInPositionInfo: " + boaInPositionInfo, sl);
		HtBoaInPosition u = null;
		if (boaInPositionInfo.getId() > 0) {
			u = htBoaInPositionService.findById(boaInPositionInfo.getId());
			if (u == null) {
				u = new HtBoaInPosition();
			}
		} else {
			u = new HtBoaInPosition();
		}
		u.setLastModifiedDatetime(new Date());
		u.setOrgPath(boaInPositionInfo.getOrgPath());
		u.setParentOrgCode(boaInPositionInfo.getPOrgCode());
		u.setPositionName(boaInPositionInfo.getPositionName());
		u.setPositionNameCn(boaInPositionInfo.getPositionNameCn() == null ? boaInPositionInfo.getPositionName() : boaInPositionInfo.getPositionNameCn());
		u.setRootOrgCode(boaInPositionInfo.getROrgCode());
		u.setSequence(boaInPositionInfo.getSequence() == null ? 0 : boaInPositionInfo.getSequence());
		u.setPositionCode(boaInPositionInfo.getPositionCode());
		u.setStatus(Constants.STATUS_0);
		if (boaInPositionInfo.getId() > 0) {
			u.setId(boaInPositionInfo.getId());
			u.setUpdateOperator(userId);
			u = htBoaInPositionService.update(u);
		} else {
			u.setCreatedDatetime(new Date());
			u.setCreateOperator(userId);
			u = htBoaInPositionService.add(u);
		}

		el = System.currentTimeMillis();
		log.debug(logEnd, "boaInPositionInfo: " + boaInPositionInfo, msg, el, el - sl);
		return Result.buildSuccess();
	}

	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "对内：物理删除岗位记录")
	@PostMapping(value = { "/in/deleteTrunc" }, produces = { "application/json" })
	public Result deleteTrunc(long id) {
		long sl = System.currentTimeMillis(), el = 0L;
		String msg = "成功";
		String logHead = "岗位记录删除：position/in/delete param-> {}";
		String logStart = logHead + " | START:{}";
		String logEnd = logHead + " {} | END:{}, COST:{}";
		log.debug(logStart, "codes: " + id, sl);
		HtBoaInPosition u = htBoaInPositionService.findById(id);
		// 1.岗位下有角色，有人员则不可以删除
		List<String> listPositionCode = new ArrayList<String>();
		listPositionCode.add(u.getPositionCode());
		List<String> listRole = htBoaInPositionRoleService.queryRoleCodesByPosition(listPositionCode);
		if (!listRole.isEmpty()) {
			return Result.buildFail("该岗位已关联角色，不可删除！", "该岗位已关联角色，不可删除！");
		}
		HtBoaInPositionUser htBoaInPositionUser = new HtBoaInPositionUser();
		htBoaInPositionUser.setPositionCode(u.getPositionCode());
		List<HtBoaInPositionUser> listHtBoaInPositionUser = htBoaInPositionUserService.findAll(htBoaInPositionUser);
		if (!listHtBoaInPositionUser.isEmpty()) {
			return Result.buildFail("该岗位存在用户，不可删除！", "该岗位存在用户，不可删除！");
		}
		htBoaInPositionService.delete(id);
		el = System.currentTimeMillis();
		log.debug(logEnd, "codes: " + id, msg, el, el - sl);
		return Result.buildSuccess();
	}

	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "对内：删除标记岗位记录")
	@PostMapping(value = { "/in/delete" }, produces = { "application/json" })
	public Result delete(long id,@RequestHeader("userId") String userId) {
		long sl = System.currentTimeMillis(), el = 0L;
		String msg = "成功";
		String logHead = "岗位记录删除：position/in/delete param-> {}";
		String logStart = logHead + " | START:{}";
		String logEnd = logHead + " {} | END:{}, COST:{}";
		log.debug(logStart, "codes: " + id, sl);
		HtBoaInPosition u = htBoaInPositionService.findById(id);
		//1.岗位下有角色，有人员则不可以删除
		List<String> listPositionCode = new ArrayList<String>();
		listPositionCode.add(u.getPositionCode());
 		List<String> listRole = htBoaInPositionRoleService.queryRoleCodesByPosition(listPositionCode);
		if(!listRole.isEmpty()) {
			return Result.buildFail("该岗位已关联角色，不可删除！", "该岗位已关联角色，不可删除！");
		}
		HtBoaInPositionUser htBoaInPositionUser= new HtBoaInPositionUser();
		htBoaInPositionUser.setPositionCode(u.getPositionCode());
		List<HtBoaInPositionUser> listHtBoaInPositionUser  = htBoaInPositionUserService.findAll(htBoaInPositionUser);
		if(!listHtBoaInPositionUser.isEmpty()) {
			return Result.buildFail("该岗位存在用户，不可删除！", "该岗位存在用户，不可删除！");
		}
		u.setDelFlag(Constants.DEL_1);
		u.setUpdateOperator(userId);
		u.setLastModifiedDatetime(new Date());
		//htBoaInPositionService.update(u);
		el = System.currentTimeMillis();
		log.debug(logEnd, "codes: " + id, msg, el, el - sl);
		return Result.buildSuccess();
	}

	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "对内：禁用/启用岗位")
	@PostMapping(value = { "/in/stop" }, produces = { "application/json" })
	public Result stop(long id, String status,@RequestHeader("userId") String userId) {
		long sl = System.currentTimeMillis(), el = 0L;
		String msg = "成功";
		String logHead = "岗位记录删除：position/in/delete param-> {}";
		String logStart = logHead + " | START:{}";
		String logEnd = logHead + " {} | END:{}, COST:{}";
		log.debug(logStart, "codes: " + id, sl);
		HtBoaInPosition u = htBoaInPositionService.findById(id);
		u.setStatus(status);
		u.setLastModifiedDatetime(new Date());
		u.setUpdateOperator(userId);
		htBoaInPositionService.update(u);
		el = System.currentTimeMillis();
		log.debug(logEnd, "codes: " + id, msg, el, el - sl);
		return Result.buildSuccess();
	}

	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "对内：岗位编码是否可用  true：可用  false：不可用")
	@PostMapping(value = { "/isExistPositionCode" }, produces = { "application/json" })
	public Result isExistPositionCode(String positionCode) {
		List<HtBoaInPosition> listHtBoaInPosition = htBoaInPositionService.findByPositionCode(positionCode);
		if (listHtBoaInPosition.isEmpty()) {
			return Result.buildSuccess();
		} else {
			return Result.buildFail();
		}
	}
	
	
	/**
     * 导出
     */
    @PostMapping(value = "/exportPositionExcel")  
    @ResponseBody  
    public void exportPositionExcel(HttpServletResponse response){  
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
            workbook = htBoaInPositionService.exportPositionExcel();  
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
	@PostMapping(value = "/importPositionExcel")
	public Result importPositionExcel(HttpServletRequest request, HttpServletResponse response,@RequestHeader("userId") String userId) {
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
			htBoaInPositionService.importPositionExcel(in, file,userId);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.buildFail();
		}
		return Result.buildSuccess();
	}

}
