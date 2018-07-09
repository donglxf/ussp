/**
 * UC数据接口 对外开放
 */
package com.ht.ussp.uc.app.feignserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInBusinessOrg;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.model.BoaInOrgInfo;
import com.ht.ussp.uc.app.model.BoaInPositionInfo;
import com.ht.ussp.uc.app.model.BoaInRoleInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.SelfBoaInUserInfo;
import com.ht.ussp.uc.app.service.DingDingService;
import com.ht.ussp.uc.app.service.HtBoaInOrgBusinessService;
import com.ht.ussp.uc.app.service.HtBoaInOrgService;
import com.ht.ussp.uc.app.service.HtBoaInPositionUserService;
import com.ht.ussp.uc.app.service.HtBoaInUserAppService;
import com.ht.ussp.uc.app.service.HtBoaInUserRoleService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;
import com.ht.ussp.uc.app.vo.DataUserOrgVo;
import com.ht.ussp.uc.app.vo.DdUserVo;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping(value = "/datas")
@Log4j2
public class UcDataResource {
	@Autowired
	private HtBoaInUserRoleService htBoaInUserRoleService;
	
	@Autowired
	private HtBoaInUserService htBoaInUserService;
	
	@Autowired
	private HtBoaInOrgService htBoaInOrgService;
	
	@Autowired
	private HtBoaInUserAppService htBoaInUserAppService;
	
	@Autowired
	private HtBoaInOrgBusinessService htBoaInOrgBusinessService;

	@Autowired
	private HtBoaInPositionUserService htBoaInPositionUserService;
	
	@Autowired
	private DingDingService dingDingService;
	

	// 根据用户id,手机号，用户姓名，email获取用户机构信息
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "获取指定用户所属机构信息",notes="通过userid,email,mobile,jobnumber查询")
	@PostMapping(value = { "/user/getUserOrg" })
	public Result getUserOrg(@RequestParam("keyword") String keyword) {
		HtBoaInUser htBoaInUser = null;
		List<HtBoaInUser> htBoaInUserList = htBoaInUserService.findByUserIdOrEmailOrMobileOrJobNumber(keyword,keyword,keyword,keyword);
		if(htBoaInUserList!=null&&!htBoaInUserList.isEmpty()) {
			htBoaInUser = htBoaInUserList.get(0);
		}
		if(htBoaInUser!=null) {
			DataUserOrgVo dataUserOrgVo = new DataUserOrgVo();
			dataUserOrgVo.setEmail(htBoaInUser.getEmail());
			dataUserOrgVo.setIdNo(htBoaInUser.getIdNo());
			dataUserOrgVo.setJobNumber(htBoaInUser.getJobNumber());
			dataUserOrgVo.setMobile(htBoaInUser.getMobile());
			dataUserOrgVo.setOrgCode(htBoaInUser.getOrgCode());
			dataUserOrgVo.setStatus(htBoaInUser.getStatus());
			dataUserOrgVo.setUserId(htBoaInUser.getUserId());
			dataUserOrgVo.setUserName(htBoaInUser.getUserName());
			List<HtBoaInOrg> listHtBoaInOrg = htBoaInOrgService.findByOrgCode(htBoaInUser.getOrgCode());
			if(listHtBoaInOrg!=null&&!listHtBoaInOrg.isEmpty()) {
				dataUserOrgVo.setHtBoaInOrg(listHtBoaInOrg.get(0));
				if(listHtBoaInOrg.get(0)!=null&&StringUtils.isEmpty(listHtBoaInOrg.get(0).getParentOrgCode())) {
					List<HtBoaInOrg> listParentHtBoaInOrg = htBoaInOrgService.findByOrgCode(htBoaInUser.getOrgCode());
					if(listHtBoaInOrg!=null&&!listHtBoaInOrg.isEmpty()) {
						dataUserOrgVo.setParentHtBoaInOrg(listParentHtBoaInOrg.get(0));
					}
				}
			}
			List<HtBoaInOrg>  listSubHtBoaInOrg =  htBoaInOrgService.findByParentOrgCode(htBoaInUser.getOrgCode());
			dataUserOrgVo.setSubHtBoaInOrgList(listSubHtBoaInOrg);
			return Result.buildSuccess(dataUserOrgVo);
		}else {
			return Result.buildSuccess(null);
		}
	}
	
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "获取所有下级机构信息 ",notes="下级多节点,orgCode:为空则查询所有机构信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "orgCode", paramType = "query",dataType = "String", required = true, value = "查询指定orgCode下所有子节点，若传空字符串则查询所有机构信息"),
		@ApiImplicitParam(name = "busiType", paramType = "query",dataType = "String", required = true, value = "传值1或者2 (1-UC行政机构  2-业务机构)") ,
		@ApiImplicitParam(name = "keyword", paramType = "query",dataType = "String", required = true, value = "当keyword不为空 orgCode为空，则在所有机构中模糊查询机构名称，机构编码;orgCode不为空则查询所有下级机构") })
	@PostMapping(value = { "/org/getOrgInfo" })
	public Result getOrgInfo(@RequestParam("orgCode") String orgCode,@RequestParam("busiType") String busiType,@RequestParam("keyword") String keyword) { 
		List<BoaInOrgInfo>  listSubHtBoaInOrg = null;
		keyword = "null".equals(keyword)?"":keyword;
		if("1".equals(busiType)) {//行政机构 2业务机构
			if(StringUtils.isNotEmpty(orgCode)&&!"null".equals(orgCode)) {
				listSubHtBoaInOrg = htBoaInOrgService.getSubOrgInfo(orgCode);
				if(listSubHtBoaInOrg!=null&&!listSubHtBoaInOrg.isEmpty()) {
					listSubHtBoaInOrg = listSubHtBoaInOrg.stream().filter(list -> list.getDelFlag()==0).collect(Collectors.toList());
				}
			}else {
				listSubHtBoaInOrg = htBoaInOrgService.findAllSubOrgInfo(keyword);
			}
		}else {	
			if(StringUtils.isNotEmpty(orgCode)&&!"null".equals(orgCode)) {
				listSubHtBoaInOrg = htBoaInOrgBusinessService.getSubOrgInfo(orgCode);
				if(listSubHtBoaInOrg!=null&&!listSubHtBoaInOrg.isEmpty()) {
					listSubHtBoaInOrg = listSubHtBoaInOrg.stream().filter(list -> list.getDelFlag()==0).collect(Collectors.toList());
				}
			}else {
				listSubHtBoaInOrg = htBoaInOrgBusinessService.findAllSubOrgInfo(keyword);
			}
		}
		
		return Result.buildSuccess(listSubHtBoaInOrg);
	}
	
	// 根据用户id,手机号，用户姓名，email获取用户岗位信息
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "获取指定用户岗位信息 ",notes="通过userid,email,mobile,jobnumber查询")
	@PostMapping(value = { "/user/getUserPosition" })
	public Result getUserPosition(@RequestParam("keyword") String keyword) {
		PageResult result = new PageResult();
		PageConf pageConf = new PageConf();
		pageConf.setPage(0);
		pageConf.setSize(500);
		Map<String, String> query = new HashMap<>();
		HtBoaInUser htBoaInUser = null;
		List<HtBoaInUser> htBoaInUserList = htBoaInUserService.findByUserIdOrEmailOrMobileOrJobNumber(keyword,keyword,keyword,keyword);
		if(htBoaInUserList!=null&&!htBoaInUserList.isEmpty()) {
			htBoaInUser = htBoaInUserList.get(0);
		}
		if(htBoaInUser!=null) {
			query.put("userId", htBoaInUser.getUserId());
			result = htBoaInPositionUserService.listPositionUserByPage(pageConf, query);
		}
		List<BoaInPositionInfo> listBoaInPositionInfo =null;
		if(result!=null) {
			listBoaInPositionInfo = (List<BoaInPositionInfo>) result.getData();
		}
		return Result.buildSuccess(listBoaInPositionInfo);
	}

	// 根据用户id,手机号，用户姓名，email获取用户角色信息
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "获取指定用户角色信息",notes="通过userid,email,mobile,jobnumber查询")
	@PostMapping(value = { "/user/getUserRole" })
	public Result getUserRole(@RequestParam("keyword") String keyword) {
		PageResult result = new PageResult();
		PageConf pageConf = new PageConf();
		pageConf.setPage(0);
		pageConf.setSize(500);
		Map<String, String> query = new HashMap<>();
		HtBoaInUser htBoaInUser = null;
		List<HtBoaInUser> htBoaInUserList = htBoaInUserService.findByUserIdOrEmailOrMobileOrJobNumber(keyword,keyword,keyword,keyword);
		if(htBoaInUserList!=null&&!htBoaInUserList.isEmpty()) {
			htBoaInUser = htBoaInUserList.get(0);
		}
		if(htBoaInUser!=null) {
			query.put("userId", htBoaInUser.getUserId());
			result = htBoaInUserRoleService.listUserRoleByPage(pageConf, query);
		}
		List<BoaInRoleInfo> listBoaInRoleInfo =null;
		if(result!=null) {
			listBoaInRoleInfo = (List<BoaInRoleInfo>) result.getData();
		}
		return Result.buildSuccess(listBoaInRoleInfo);
	}
	
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "获取业务机构", notes = "orgLevel:20 公司层级  40 片区层级  60 分公司层级  80 部门层级  100 小组层级;busiOrgCode:传空则查询所有指定orgLevel业务机构信息,如果不为空则查询下级指定的orgLevel")
	@PostMapping(value = { "/getBusiOrgList" }, produces = { "application/json" })
	public Result getBusiOrgList(String orgLevel, String busiOrgCode) { 
		List<HtBoaInBusinessOrg> listHtBoaInOrg = null;
		orgLevel = StringUtils.isEmpty(orgLevel) ? "0" : orgLevel;
		if (StringUtils.isNotEmpty(busiOrgCode) || "null".equals(busiOrgCode)) { // 不为空则，查询下级
			listHtBoaInOrg = htBoaInOrgBusinessService.findByParentOrgCodeAndOrgLevel(busiOrgCode, Integer.parseInt(orgLevel));
		} else {
			listHtBoaInOrg = htBoaInOrgBusinessService.findByOrgLevel(Integer.parseInt(orgLevel));
		}
		return Result.buildSuccess(listHtBoaInOrg);
	} 
	
	@ApiOperation(value = "获取钉钉用户数据", notes = "提供给运维")
	@GetMapping(value = { "/getDdUserList" }, produces = { "application/json" })
	public List<DdUserVo> getDdUserList(){
		return dingDingService.getDdUserList();
	}
	
	//获取对应系统下所有用户信息  almsUsersSnchronized
	@SuppressWarnings("rawtypes")
	@PostMapping(value = { "/getUserInfoForApp" }, produces = { "application/json" })
	public Result getUserInfoForApp(String appCode) { 
		List<SelfBoaInUserInfo> listSelfBoaInUserInfo = htBoaInUserAppService.getUserInfoForApp(appCode);
		for(SelfBoaInUserInfo selfBoaInUserInfo : listSelfBoaInUserInfo) {
			//获取用户角色
			selfBoaInUserInfo.setRoleCodes(htBoaInUserRoleService.getAllRoleCodes(selfBoaInUserInfo.getUserId()));
			//获取用户岗位
			selfBoaInUserInfo.setPositionCodes(htBoaInPositionUserService.queryRoleCodes(selfBoaInUserInfo.getUserId()));
		}
		return Result.buildSuccess(listSelfBoaInUserInfo);
	} 
}
