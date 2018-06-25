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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.common.Constants;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInBusinessOrg;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.domain.HtBoaInUserExt;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInOrgBusinessService;
import com.ht.ussp.uc.app.service.HtBoaInUserBusinessService;
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
@RequestMapping(value = "/orgbusiness")
@Log4j2
public class OrgBusinessResource {
    @Autowired
    private HtBoaInOrgBusinessService htBoaInOrgBusinessService;
    
    @Autowired
    private HtBoaInUserBusinessService htBoaInUserBusinessService;

    @Autowired
    private HtBoaInUserService htBoaInUserService;

    @PostMapping(value = "/tree", produces = {"application/json"})
    public List<HtBoaInBusinessOrg> getOrgTreeList(String parenOrgCode) {
        return htBoaInOrgBusinessService.getOrgTreeList(parenOrgCode);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @ApiOperation(value = "对内：机构记录查询", notes = "列出所有机构记录列表信息")
    @PostMapping(value = {"/list"}, produces = {"application/json"})
    public PageResult<HtBoaInBusinessOrg> list(PageVo page) {
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
        result =   htBoaInOrgBusinessService.findAllByPage(pageConf, page.getQuery());
        el = System.currentTimeMillis();
        log.debug(logEnd, "page: " + page, msg, el, el - sl);
        
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
        return result;
    }

    @SuppressWarnings({"unused", "rawtypes"})
    @ApiOperation(value = "对内：新增/编辑机构记录")
    @PostMapping(value = {"/add"}, produces = {"application/json"})
    public Result add(@RequestBody HtBoaInBusinessOrg htBoaInBusinessOrg, @RequestHeader("userId") String userId) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "机构记录查询：org/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "boaInOrgInfo: " + htBoaInBusinessOrg, sl);
        HtBoaInBusinessOrg u = null;
        List<HtBoaInBusinessOrg> listHtBoaInBusinessOrg = htBoaInOrgBusinessService.findByOrgCode(htBoaInBusinessOrg.getBusinessOrgCode());
        if(listHtBoaInBusinessOrg!=null&&!listHtBoaInBusinessOrg.isEmpty()) {
        	u = htBoaInBusinessOrg;
        	/*u.setBusinessOrgName(htBoaInBusinessOrg.getBusinessOrgName());
        	u.setActivityCode(htBoaInBusinessOrg.getActivityCode());
        	u.setApprovalCode(htBoaInBusinessOrg.getApprovalCode());
        	u.setOrgLevel(htBoaInBusinessOrg.getOrgLevel());
        	u.setBranchCode(htBoaInBusinessOrg.getBranchCode());
        	u.setBusinessGroup(htBoaInBusinessOrg.getBusinessGroup());
        	u.setCity(htBoaInBusinessOrg.getCity());
        	u.setCounty(htBoaInBusinessOrg.getCounty());
        	u.setDistrictCode(htBoaInBusinessOrg.getDistrictCode());
        	u.setFinanceCode(htBoaInBusinessOrg.getFinanceCode());
        	u.setParentOrgCode(StringUtils.isEmpty(htBoaInBusinessOrg.getParentOrgCode())?null:htBoaInBusinessOrg.getParentOrgCode());
        	u.setSequence(htBoaInBusinessOrg.getSequence());*/
        }
        if(u==null) {
           u = htBoaInBusinessOrg;
           u.setCreatedDatetime(new Date());
        } 
        u.setUpdateDatetime(new Date());
        u.setJpaVersion(0);
        u.setStatus(0);
        u.setCreateOperator(userId);
        u.setDataSource(Constants.USER_DATASOURCE_1);
        u=htBoaInOrgBusinessService.add(u);
        el = System.currentTimeMillis();
        log.debug(logEnd, "boaInOrgInfo: " + u, msg, el, el - sl);
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
        HtBoaInBusinessOrg u = htBoaInOrgBusinessService.findById(id);
        if(u==null) {
        	return Result.buildFail("9999", "没有相关信息");
        }
        //机构下有机构，有人员， 有岗位都不可以删除
 		List<HtBoaInBusinessOrg> listHtBoaInOrg = htBoaInOrgBusinessService.findByParentOrgCode(u.getBusinessOrgCode());
		if(listHtBoaInOrg!=null&&!listHtBoaInOrg.isEmpty()) {
			return Result.buildFail("该机构下存在子机构，不可删除！", "该机构下存在子机构，不可删除！");
		}
		List<HtBoaInUserExt>  listHtBoaInUserExt = htBoaInUserBusinessService.getUserBusiListByBusiOrgCode(u.getBusinessOrgCode());
		if (listHtBoaInUserExt!=null&&!listHtBoaInUserExt.isEmpty()) {
			return Result.buildFail("该机构下存在用户，不可删除！", "该机构下存在用户，不可删除！");
		}
		/*HtBoaInPosition htBoaInPosition = new HtBoaInPosition();
		//htBoaInPosition.setParentOrgCode(u.getOrgCode());
		List<HtBoaInPosition> listHtBoaInPosition = htBoaInPositionService.findAll(htBoaInPosition);
		if (!listHtBoaInPosition.isEmpty()) {
			return Result.buildFail("该机构下存在可用岗位，不可删除！", "该机构下存在可用岗位，不可删除！");
		}*/
		
        //u.setDelFlag(Constants.DEL_1);
       /* u.setUpdateOperator(userId);
        u.setLastModifiedDatetime(new Date());
        htBoaInOrgBusinessService.update(u);*/
		
		htBoaInOrgBusinessService.delete(u);
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
        HtBoaInBusinessOrg u = htBoaInOrgBusinessService.findById(id);
        u.setUpdateDatetime(new Date());
        u.setUpdateOperator(userId);
        htBoaInOrgBusinessService.update(u);
        el = System.currentTimeMillis();
        log.debug(logEnd, "codes: " + id, msg, el, el - sl);
        return Result.buildSuccess();
    }
    
    @SuppressWarnings("rawtypes")
   	@ApiOperation(value = "对内：机构编码是否可用  true：可用  false：不可用")
    @PostMapping(value = {"/isExistOrgCode" }, produces = {"application/json"} )
    public Result isExistOrgCode( String orgCode) {
       List<HtBoaInBusinessOrg> listHtBoaInOrg = htBoaInOrgBusinessService.findByOrgCode(orgCode);
       if(listHtBoaInOrg.isEmpty()) {
    	   return Result.buildSuccess();
       }else {
    	   return Result.buildFail();
       }
    }
    
    @ApiOperation(value = "根据机构编码查询机构信息")
    @GetMapping(value = "/getOrgInfoByCode")
    public HtBoaInBusinessOrg getOrgInfoByCode(String orgCode) {
    	List<HtBoaInBusinessOrg> listHtBoaInOrg = htBoaInOrgBusinessService.findByOrgCode(orgCode);
    	if(listHtBoaInOrg==null||listHtBoaInOrg.isEmpty()) {
    		return null;
    	} 
        return listHtBoaInOrg.get(0);
    }
        
    @ApiOperation(value = "根据机构编码查询下级机构信息")
    @GetMapping(value = "/getSubOrgInfoByCode")
    public List<HtBoaInBusinessOrg> getSubOrgInfoByCode(String parentOrgCode) {
        return htBoaInOrgBusinessService.findByParentOrgCode(parentOrgCode);
    }
    
    @ApiOperation(value = "根据时间获取指定时间机构信息")
    @GetMapping(value = "/getOrgListByTime")
    public List<HtBoaInBusinessOrg> getOrgListByTime(@RequestParam("startTime")String startTime, @RequestParam("endTime")String endTime) {
        return htBoaInOrgBusinessService.getOrgListByTime(startTime,endTime);
    }
    
    /**
     * 获取用户所属机构类型信息 (20 公司层级  40 片区层级  60 分公司层级  80 部门层级  100 小组层级)
     *  资源类型枚举值：OrgTypeEnum.ORG_TYPE_COMPANY.getReturnCode()
     * @return
     */
    @ApiOperation(value = "获取用户所属机构类型信息")
    @GetMapping(value = "/getOrgInfoByOrgType")
    public HtBoaInBusinessOrg getOrgInfoByOrgType(@RequestParam("orgCode")String orgCode,@RequestParam("orgType") String orgType) {
        return htBoaInOrgBusinessService.getOrgInfoByOrgType(orgCode,orgType);
    }
 
    @ApiOperation(value = "产生新的orgcode")
    @GetMapping(value = "/getNewOrgCode")
    public String getNewOrgCode(String parentOrgCode) {
    	//1.查看父级下有多少机构 然后再最大机构上加1
    	List<HtBoaInBusinessOrg> listHtBoaInOrg=htBoaInOrgBusinessService.findByParentOrgCode(parentOrgCode);
    	int size = 0;
    	if(listHtBoaInOrg==null || listHtBoaInOrg.isEmpty()) {
    		size = 1;
    	}else {
    		size = listHtBoaInOrg.size()+1;
    	}
        return String.format("%s%02d", parentOrgCode, size);
    }
    
    
    @SuppressWarnings("rawtypes")
   	@ApiOperation(value = "获取业务机构",notes="orgLevel:20 公司层级  40 片区层级  60 分公司层级  80 部门层级  100 小组层级;busiOrgCode:传空则查询所有指定orgLevel业务机构信息,如果不为空则查询下级指定的orgLevel")
    @PostMapping(value = {"/getBusiOrgList" }, produces = {"application/json"} )
    public Result getBusiOrgList(String orgLevel,String busiOrgCode) {
      List<HtBoaInBusinessOrg> listHtBoaInOrg = null;
      orgLevel = StringUtils.isEmpty(orgLevel)?"0":orgLevel;
      if(StringUtils.isNotEmpty(busiOrgCode)||"null".equals(busiOrgCode)) { //不为空则，查询下级
    	 listHtBoaInOrg = htBoaInOrgBusinessService.findByParentOrgCodeAndOrgLevel(busiOrgCode,Integer.parseInt(orgLevel));
       }else {
    	 listHtBoaInOrg = htBoaInOrgBusinessService.findByOrgLevel(Integer.parseInt(orgLevel));
       }
       return Result.buildSuccess(listHtBoaInOrg);
    }
    
    @SuppressWarnings("rawtypes")
   	@ApiOperation(value = "获取业务机构下所有人",notes="")
    @PostMapping(value = {"/getBusiOrgUserList" }, produces = {"application/json"} )
    public Result getBusiOrgUserList(String branch) {
       return Result.buildSuccess(null);
    }
    
    @SuppressWarnings("rawtypes")
   	@ApiOperation(value = "根据userId获取用户信息",notes="返回所属分公司")
    @PostMapping(value = {"/getUserInfoByUserId" }, produces = {"application/json"} )
    public Result getUserInfoByUserId(String userId) {
    	HtBoaInUser htBoaInUser = htBoaInUserService.findByUserId(userId);
       return Result.buildSuccess(htBoaInUser);
    }
    
    @SuppressWarnings("rawtypes")
   	@ApiOperation(value = "信贷机构转换UC机构",notes="将信贷机构转换为UC对应机构信息")
    @PostMapping(value = {"/convertBmOrg" }, produces = {"application/json"} )
    public Result convertBmOrg( ) {
       return htBoaInOrgBusinessService.convertBmOrg( );
    }
    
    @SuppressWarnings("rawtypes")
   	@ApiOperation(value = "分公司 业务片区转换",notes="分公司 业务片区转换")
    @PostMapping(value = {"/convertBmBranch" }, produces = {"application/json"} )
    public Result convertBmBranch( ) {
       htBoaInOrgBusinessService.convertBmBranch( );
       return Result.buildSuccess();
    }
    
    @SuppressWarnings("rawtypes")
   	@ApiOperation(value = "uc用户匹配业务机构",notes="匹配信贷业务机构")
    @PostMapping(value = {"/convertUserBmOrg" }, produces = {"application/json"} )
    public Result convertUserBmOrg(String state) {
       htBoaInUserService.convertUserBmOrg(state);
       return Result.buildSuccess();
    }
    
}
