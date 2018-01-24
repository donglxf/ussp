package com.ht.ussp.uc.app.resource;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInPosition;
import com.ht.ussp.uc.app.model.BoaInPositionInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInPositionService;
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
		log.info(logStart, "page: " + page, sl);
		// Object o = htBoaInPositionService.findAllByPage(pageConf);
		Page<BoaInPositionInfo> pageData = (Page<BoaInPositionInfo>) htBoaInPositionService.findAllByPage(pageConf,
				page.getQuery());
		el = System.currentTimeMillis();
		log.info(logEnd, "page: " + page, msg, el, el - sl);
		// return new ResponseModal(200, msg, o);
		if (pageData != null) {
			result.count(pageData.getTotalElements()).data(pageData.getContent());
		}
		result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
		return result;
	}

	@SuppressWarnings({ "unused", "rawtypes" })
	@ApiOperation(value = "对内：新增/编辑岗位记录", notes = "提交岗位基础信息新增/编辑岗位")
	@PostMapping(value = { "/in/add" }, produces = { "application/json" })
	public Result add(@RequestBody BoaInPositionInfo boaInPositionInfo) {
		long sl = System.currentTimeMillis(), el = 0L;
		ResponseModal r = null;
		String msg = "成功";
		String logHead = "岗位记录查询：position/in/add param-> {}";
		String logStart = logHead + " | START:{}";
		String logEnd = logHead + " {} | END:{}, COST:{}";
		log.info(logStart, "boaInPositionInfo: " + boaInPositionInfo, sl);
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
		u.setPositionNameCn(boaInPositionInfo.getPositionNameCn() == null ? boaInPositionInfo.getPositionName()
				: boaInPositionInfo.getPositionNameCn());
		u.setRootOrgCode(boaInPositionInfo.getROrgCode());
		u.setSequence(boaInPositionInfo.getSequence() == null ? 0 : boaInPositionInfo.getSequence());
		u.setPositionCode(boaInPositionInfo.getPositionCode());
		u.setStatus("0");
		if (boaInPositionInfo.getId() > 0) {
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
	}

	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "对内：物理删除岗位记录", notes = "提交岗位编号，可批量删除")
	@PostMapping(value = { "/in/deleteTrunc" }, produces = { "application/json" })
	public Result deleteTrunc(int id) {
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

	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "对内：删除标记岗位记录", notes = "提交岗位编号，可批量删除")
	@PostMapping(value = { "/in/delete" }, produces = { "application/json" })
	public Result delete(long id) {
		long sl = System.currentTimeMillis(), el = 0L;
		String msg = "成功";
		String logHead = "岗位记录删除：position/in/delete param-> {}";
		String logStart = logHead + " | START:{}";
		String logEnd = logHead + " {} | END:{}, COST:{}";
		log.info(logStart, "codes: " + id, sl);
		HtBoaInPosition u = htBoaInPositionService.findById(id);
		u.setDelFlag(1);
		u.setUpdateOperator("del");
		u.setLastModifiedDatetime(new Date());
		htBoaInPositionService.update(u);
		el = System.currentTimeMillis();
		log.info(logEnd, "codes: " + id, msg, el, el - sl);
		return Result.buildSuccess();
	}

	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "对内：禁用/启用岗位")
	@PostMapping(value = { "/in/stop" }, produces = { "application/json" })
	public Result stop(long id, String status) {
		long sl = System.currentTimeMillis(), el = 0L;
		String msg = "成功";
		String logHead = "岗位记录删除：position/in/delete param-> {}";
		String logStart = logHead + " | START:{}";
		String logEnd = logHead + " {} | END:{}, COST:{}";
		log.info(logStart, "codes: " + id, sl);
		HtBoaInPosition u = htBoaInPositionService.findById(id);
		u.setStatus(status);
		u.setLastModifiedDatetime(new Date());
		htBoaInPositionService.update(u);
		el = System.currentTimeMillis();
		log.info(logEnd, "codes: " + id, msg, el, el - sl);
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

}
