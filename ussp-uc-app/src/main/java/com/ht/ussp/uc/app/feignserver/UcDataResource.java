/**
 * UC数据接口 对外开放
 */
package com.ht.ussp.uc.app.feignserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.model.BoaInPositionInfo;
import com.ht.ussp.uc.app.model.BoaInRoleInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.service.HtBoaInOrgService;
import com.ht.ussp.uc.app.service.HtBoaInPositionUserService;
import com.ht.ussp.uc.app.service.HtBoaInUserRoleService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;
import com.ht.ussp.uc.app.vo.DataUserOrgVo;

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
	private HtBoaInPositionUserService htBoaInPositionUserService;

	// 根据用户id,手机号，用户姓名，email获取用户机构信息
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "获取指定用户所属机构信息",notes="通过userid,email,mobile,jobnumber查询")
	@PostMapping(value = { "/user/getUserOrg" })
	public Result getUserOrg(@RequestParam("keyword") String keyword) {
		HtBoaInUser htBoaInUser = htBoaInUserService.findByUserIdOrEmailOrMobileOrJobNumber(keyword,keyword,keyword,keyword);
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
			return Result.buildFail();
		}
	}
	
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "获取所有下级机构信息 ",notes="下级多节点")
	@PostMapping(value = { "/org/getAllSubOrgInfo" })
	public Result getAllSubOrgInfo(@RequestParam("orgCode") String orgCode) { 
		List<HtBoaInOrg>  listSubHtBoaInOrg =  htBoaInOrgService.getAllSubOrgInfo(orgCode);
		if(listSubHtBoaInOrg!=null&&!listSubHtBoaInOrg.isEmpty()) {
			listSubHtBoaInOrg = listSubHtBoaInOrg.stream().filter(list -> list.getDelFlag()==0).collect(Collectors.toList());
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
		HtBoaInUser htBoaInUser = htBoaInUserService.findByUserIdOrEmailOrMobileOrJobNumber(keyword,keyword,keyword,keyword);
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
		HtBoaInUser htBoaInUser = htBoaInUserService.findByUserIdOrEmailOrMobileOrJobNumber(keyword,keyword,keyword,keyword);
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
}
