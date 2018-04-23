package com.ht.ussp.uc.app.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInBmOrg;
import com.ht.ussp.uc.app.domain.HtBoaInBmUser;
import com.ht.ussp.uc.app.domain.HtBoaInContrast;
import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.service.DingDingService;
import com.ht.ussp.uc.app.service.HtBoaInBmOrgService;
import com.ht.ussp.uc.app.service.HtBoaInBmUserService;
import com.ht.ussp.uc.app.service.HtBoaInContrastService;
import com.ht.ussp.uc.app.service.HtBoaInLoginService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;
import com.ht.ussp.uc.app.service.SynDataService;
import com.ht.ussp.uc.app.vo.BmUserVo;
import com.ht.ussp.uc.app.vo.PageVo;
import com.ht.ussp.uc.app.vo.UserContrastVo;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

 
@RestController
@RequestMapping(value = "/syndata")
@Log4j2
public class SynDataResource {
   
    @Autowired
    private HtBoaInContrastService htBoaInContrastService;
    @Autowired
    private HtBoaInBmUserService htBoaInBmUserService;
    @Autowired
    private SynDataService synDataService;
    @Autowired
    private HtBoaInBmOrgService htBoaInBmOrgService;
    @Autowired
    private DingDingService dingDingService;
    @Autowired
    private HtBoaInUserService htBoaInUserService;
    @Autowired
    private HtBoaInLoginService htBoaInLoginService;
    
    
    /**
     * 获取钉钉数据（部门，人员）
     * @param page
     * @return
     */
	@SuppressWarnings("rawtypes")
	@ApiOperation("01-下载钉钉数据（机构，用户）")
	@RequestMapping(value = {"/downDingDingData" }, method = RequestMethod.POST)
    public Result getDownDD() {
    	dingDingService.getDD();
    	log.debug("下载完成！");
        return Result.buildSuccess();
    }
    
	/**
	 * 矫正历史错误数据
	 * @return
	 */
	@ApiOperation("02-矫正历史错误数据")
	@RequestMapping(value = {"/dealErrorData" }, method = RequestMethod.POST)
	public Result dealErrorData() { 
		dingDingService.dealErrorData();
    	log.debug("矫正历史错误数据完成！");
        return Result.buildSuccess();
	}
	
	/**
	 * 数据整理
	 * @return
	 */
	@ApiOperation("03-钉钉数据整理（机构，用户）")
	@RequestMapping(value = {"/dealData" }, method = RequestMethod.POST)
	public Result dealData() {
		dingDingService.dealData();
    	log.debug("数据整理完成！");
        return Result.buildSuccess();
	}
    
	/**
     * 机构转换
     * @param page
     * @return
     */
    @SuppressWarnings({ "rawtypes" })
    @ApiOperation("04-将钉钉机构转换为UC基础数据")
	@RequestMapping(value = {"/convertOrg" }, method = RequestMethod.POST)
    public Result convertOrg() {
    	dingDingService.convertOrg();
    	log.debug("机构同步转换完成！");
        return Result.buildSuccess();
    }
    
    /**
     * 用户转换
     * @param page
     * @return
     */
    @SuppressWarnings({ "rawtypes"})
    @ApiOperation("05-将钉钉用户转换为UC用户基础数据")
    @RequestMapping(value = {"/convertUser" }, method = RequestMethod.POST)
    public Result  convertUser() { 
    	dingDingService.convertUser();
		log.debug("用户同步转换完成！");
    	return Result.buildSuccess();
    }
    
    
	@ApiOperation("信贷用户转换为UC用户")
	@RequestMapping(value = {"/convertBmUser" }, method = RequestMethod.POST)
	public Result convertBmUser() { 
		//1.获取所有信贷用户  2.通过手机号,email验证关联 信贷用户与UC用户  4.如果信贷用户在UC不存在，则在UC创建一个,挂靠到未知机构
		List<HtBoaInBmUser> listHtBoaInBmUser=htBoaInBmUserService.getHtBoaInBmUserListByStatus("1");
		List<HtBoaInUser> listHtBoaInUser=htBoaInUserService.findAll();
		List<HtBoaInLogin> listHtBoaInLogin =htBoaInLoginService.findAll();
		List<HtBoaInContrast> listHtBoaInContrast =htBoaInContrastService.getHtBoaInContrastListByType("20");
		for(HtBoaInBmUser htBoaInBmUser : listHtBoaInBmUser) {
			BmUserVo bmUserVo = new BmUserVo();
			bmUserVo.setBmOrgCode(htBoaInBmUser.getOrgCode());
			bmUserVo.setBmOrgName("");
			bmUserVo.setEmail(htBoaInBmUser.getEmail());
			bmUserVo.setJobNumber(htBoaInBmUser.getJobNumber());
			bmUserVo.setStatus("0");
			bmUserVo.setUserName(htBoaInBmUser.getUserName());
			bmUserVo.setBmUserId(htBoaInBmUser.getUserId());
			bmUserVo.setMobile(htBoaInBmUser.getMobile());
			htBoaInUserService.saveBmUserInfo(bmUserVo, listHtBoaInUser, listHtBoaInLogin, listHtBoaInBmUser, listHtBoaInContrast);
		}
    	log.debug("信贷用户转换为UC用户完成！");
        return Result.buildSuccess();
	}
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value = "获取关联的用户信息，信贷用户信息")
    @PostMapping(value = "/queryAllUserContrast")
    public PageResult<UserContrastVo> queryAllUserContrast(PageVo page) {
    	PageResult result = new PageResult();
    	PageConf pageConf = new PageConf();
    	pageConf.setPage(page.getPage());
    	pageConf.setSearch(page.getKeyWord());
    	pageConf.setSize(page.getLimit());
    	List list = new ArrayList<>();
    	list.add("orgCode");
    	pageConf.setSortNames(list);
    	result = synDataService.findAllByPage(pageConf);
    	return result;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "获取信贷用户信息")
    @PostMapping(value = "/queryBmUser")
    public PageResult<UserContrastVo> queryBmUser(PageVo page) {
    	PageResult result = new PageResult();
    	PageConf pageConf = new PageConf();
    	pageConf.setPage(page.getPage());
    	pageConf.setSearch(page.getKeyWord());
    	pageConf.setSize(page.getLimit());
    	result = (PageResult) htBoaInBmUserService.findAllByPage(pageConf);
    	List<UserContrastVo> listUserContrastVo= new ArrayList<UserContrastVo>();
    	if(result!=null) {
    		List<HtBoaInBmUser> listData = (List<HtBoaInBmUser>) result.getData();
    		if(listData!=null && !listData.isEmpty()) {
    			List<HtBoaInContrast> listHtBoaInContrast = htBoaInContrastService.getHtBoaInContrastListByType("20");
    			List<HtBoaInBmOrg> bmOrgList =htBoaInBmOrgService.getHtBoaInBmOrgList();
    			for(HtBoaInBmUser htBoaInBmUser : listData) {
    				UserContrastVo userContrastVo = new UserContrastVo();
                	userContrastVo.setBmUserId(htBoaInBmUser.getUserId());
                	userContrastVo.setBmUserName(htBoaInBmUser.getUserName());
                	userContrastVo.setBmOrgCode(htBoaInBmUser.getOrgCode());
                	userContrastVo.setBmEmail(htBoaInBmUser.getEmail());
                	userContrastVo.setBmMobile(htBoaInBmUser.getMobile());
                	userContrastVo.setBmJobNumber(htBoaInBmUser.getJobNumber());
                	List<HtBoaInContrast> listorgcontrast = listHtBoaInContrast.stream().filter(hc -> htBoaInBmUser.getUserId().equals(hc.getBmBusinessId())).collect(Collectors.toList());
    			    if(listorgcontrast!=null && !listorgcontrast.isEmpty()) {
    			    	HtBoaInContrast htBoaInContrast = listorgcontrast.get(0);
    			    	userContrastVo.setUserId(htBoaInContrast.getUcBusinessId());
    			    }
    			    List<HtBoaInBmOrg> listorgbm = bmOrgList.stream().filter(hc -> hc.getOrgCode().equals(htBoaInBmUser.getOrgCode())).collect(Collectors.toList());
	               	 if(listorgbm!=null && !listorgbm.isEmpty()) {
	               		 HtBoaInBmOrg  o = listorgbm.get(0);
	               		 userContrastVo.setBmOrgName(o.getOrgNameCn());
	               	 }
    				listUserContrastVo.add(userContrastVo);
    			}
    		}
    		result.setData(listUserContrastVo);
    	}
    	return result;
    }
    
    @SuppressWarnings({ "rawtypes"  })
    @ApiOperation(value = "解除关联")
    @PostMapping(value = "/removeBmUser")
    public Result removeBmUser(long id) {
    	synDataService.removeBmUserById(id);
    	return Result.buildSuccess();
    }
    
    @SuppressWarnings({ "rawtypes"  })
    @ApiOperation(value = "关联信贷用户信息")
    @PostMapping(value = "/addBmUser")
    public Result addBmUser(String uc_userId,String bm_userId,@RequestHeader("userId") String userId) {
    	HtBoaInContrast htBoaInContrast =null;
    	if(StringUtils.isNotEmpty(uc_userId)&&StringUtils.isNotEmpty(bm_userId)) {
    		htBoaInContrast = synDataService.findByUcBusinessIdAndType(uc_userId, "20");
        	if(htBoaInContrast==null) {
        		htBoaInContrast = new HtBoaInContrast();
        	} 
        	htBoaInContrast.setUcBusinessId(uc_userId);
        	htBoaInContrast.setBmBusinessId(bm_userId);
        	htBoaInContrast.setType("20");
        	htBoaInContrast.setContrast(userId);
        	htBoaInContrast.setStatus("0");
        	synDataService.addBmUser(htBoaInContrast);
    	}
    	return Result.buildSuccess();
    }

}
