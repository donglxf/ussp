package com.ht.ussp.uc.app.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ht.ussp.client.dto.ApiInfoDto;
import com.ht.ussp.client.dto.ApiResourceDto;
import com.ht.ussp.uc.app.common.Constants;
import com.ht.ussp.uc.app.domain.HtBoaInResource;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.model.SysStatus;
import com.ht.ussp.uc.app.service.HtBoaInResourceService;
import com.ht.ussp.uc.app.service.HtBoaInRoleResService;
import com.ht.ussp.uc.app.service.HtBoaInUserAppService;
import com.ht.ussp.uc.app.service.HtBoaInUserRoleService;
import com.ht.ussp.uc.app.util.FastJsonUtil;
import com.ht.ussp.uc.app.util.LogicUtil;
import com.ht.ussp.uc.app.vo.ResVo;
import com.ht.ussp.uc.app.vo.UserVo;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

/**
 * 
 * @ClassName: RoleResouce
 * @Description: 与资源相关
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月13日 下午6:22:39
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/res")
@Log4j2
public class AuthResouce {

	@Autowired
	protected RedisTemplate<String, String> redis;
	
	@Autowired
	private HtBoaInResourceService htBoaInResourceService;

    @Autowired
    private HtBoaInRoleResService htBoaInRoleResService;

	@Autowired
	private HtBoaInUserAppService htBoaInUserAppService;
	
	@Autowired
	private HtBoaInUserRoleService htBoaInUserRoleService;

	
	/**
	 * 
	 * @Title: saveResourcesToRedis 
	 * @Description: 保存所有资源到REDIS  
	 * @return ResponseModal
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年1月18日 下午10:27:37
	 */
	@PostMapping(value = "/saveResources")
	@ApiOperation(value = "获取并保存用户资源")
	public ResponseModal saveResourcesToRedis(@RequestBody UserVo userVo,
			@RequestParam(value = "roleCodes", required = true) List<String> roleCodes) {
		ResponseModal rm = new ResponseModal();
		if (null == userVo || LogicUtil.isNullOrEmpty(userVo.getUserId()) || LogicUtil.isNullOrEmpty(userVo.getApp())) {
			rm.setSysStatus(SysStatus.MAILPARAM_ERROR);
			return rm;
		}
		String userId = userVo.getUserId();
		String app = userVo.getApp();
		String controller = userVo.getController();

		List<String> res_types = new ArrayList<String>();

		// 定义权限分组名称LIST
		List<ResVo> module_res = new ArrayList<ResVo>();
		List<ResVo> menu_res = new ArrayList<ResVo>();
		List<ResVo> button_res = new ArrayList<ResVo>();
		List<ResVo> api_res = new ArrayList<ResVo>();

		// 定义存储到LIST中的资源KEY值
		StringBuffer module_key = new StringBuffer();
		StringBuffer menu_key = new StringBuffer();
		StringBuffer button_key = new StringBuffer();
		StringBuffer api_key = new StringBuffer();
		module_key.append(userId).append(":").append("app").append(":").append("module");
		menu_key.append(userId).append(":").append("app").append(":").append("menu");
		button_key.append(userId).append(":").append("app").append(":").append("button");
		api_key.append(userId).append(":").append("app").append(":").append("api");

		// 所有资源类型
		res_types.add(Constants.RES_TYPE_BUTTON);
		res_types.add(Constants.RES_TYPE_GROUP);
		res_types.add(Constants.RES_TYPE_VIEW);
		res_types.add(Constants.RES_TYPE_MODULE);
		res_types.add(Constants.RES_TYPE_TAB);
		res_types.add(Constants.RES_TYPE_API);
		// 管理员资源权限操作
		if ("Y".equals(controller)) {
			List<ResVo> res = htBoaInResourceService.queryResForY(res_types, app);
			addToList(res, module_res, menu_res, button_res, api_res);
		}

		// 非管理员权限操作
		if ("N".equals(controller)) {
			List<String> res_code = htBoaInRoleResService.queryResByCode(roleCodes);

			if (res_code.size() > 0) {
				List<ResVo> res = htBoaInResourceService.queryResForN(res_code, res_types, app);

				addToList(res, module_res, menu_res, button_res, api_res);
			}
		}
		// api权限不能为空
		if (api_res.isEmpty()) {
			rm.setSysStatus(SysStatus.API_NOT_NULL);
			return rm;
		}
		// 登录需要重新获取资源，保存到REDIS
		if (module_res != null && module_res.size() > 0) {
			redis.delete(module_key.toString());
			redis.opsForList().leftPushAll(module_key.toString(), FastJsonUtil.objectToJson(module_res));
		}

		if (menu_res != null && menu_res.size() > 0) {
			redis.delete(menu_key.toString());
			redis.opsForList().leftPushAll(menu_key.toString(), FastJsonUtil.objectToJson(menu_res));
		}

		if (button_res != null && button_res.size() > 0) {
			redis.delete(button_key.toString());
			redis.opsForList().leftPushAll(button_key.toString(), FastJsonUtil.objectToJson(button_res));
		}

		if (api_res != null && api_res.size() > 0) {
			redis.delete(api_key.toString());
			redis.opsForList().leftPushAll(api_key.toString(), FastJsonUtil.objectToJson(api_res));
		}

		rm.setSysStatus(SysStatus.SUCCESS);

		return rm;

	}

	/**
	 * 
	 * @Title: IsHasAuth 
	 * @Description: 验证是否有资源权限
	 * @return Boolean
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年1月18日 下午10:54:08
	 */
	@GetMapping(value = "/IsHasAuth")
	@ApiOperation(value = "验证资源")
	public Boolean IsHasAuth(@RequestParam("key") String key, @RequestParam("url") String url) {
		Boolean flag = false;
		if (LogicUtil.isNullOrEmpty(key) || LogicUtil.isNullOrEmpty(url)) {
			return flag;
		}
		try {
			List<String> apiValues = redis.opsForList().range(key, 0, -1);
			JSONArray json = JSONArray.parseArray(apiValues.get(0));

			if (json.size() > 0) {
				for (int i = 0; i < json.size(); i++) {
					JSONObject job = json.getJSONObject(i);
					if (url.equals(job.get("resContent"))) {
						log.info("isHasAuth:" + url.equals(job.get("resContent")));
						flag = true;
						return flag;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return flag;
		}
		return flag;
	};

	/**
	 * 
	 * @Title: addToList 
	 * @Description: 将资源分组添加到List中
	 * @return void
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年1月18日 下午10:27:07
	 */
	public void addToList(List<ResVo> res, List<ResVo> module_res, List<ResVo> menu_res, List<ResVo> button_res,
			List<ResVo> api_res) {
		if (res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				if (Constants.RES_TYPE_MODULE.equals(res.get(i).getResType())) {
					// 保存模块资源
					module_res.add(res.get(i));
					log.info(module_res);
				} else if (Constants.RES_TYPE_VIEW.equals(res.get(i).getResType())
						|| Constants.RES_TYPE_GROUP.equals(res.get(i).getResType())) {
					// 保存菜单资源
					menu_res.add(res.get(i));
				} else if (Constants.RES_TYPE_BUTTON.equals(res.get(i).getResType())
						|| Constants.RES_TYPE_TAB.equals(res.get(i).getResType())) {
					// 保存按钮资源
					button_res.add(res.get(i));

				} else if (Constants.RES_TYPE_API.equals(res.get(i).getResType())) {
					// 保存API
					api_res.add(res.get(i));
				}
			}
		}
	};

	
	/**
	 * 
	 * @Title: queryResource 
	 * @Description: 分组查找资源：可查找菜单、分组、按钮资源 
	 * @return ResponseModal
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年1月18日 下午10:54:43
	 */

	@PostMapping(value = "/queryResource")
	@ApiOperation(value = "查找资源")
	public ResponseModal queryResource(@RequestHeader("app") String app, @RequestHeader("userId") String userId,
			@RequestParam("resourceName") String resourceName) {
		ResponseModal rm = new ResponseModal();
		StringBuffer key = new StringBuffer();
		if (LogicUtil.isNullOrEmpty(resourceName) || LogicUtil.isNullOrEmpty(app) || LogicUtil.isNullOrEmpty(userId)) {
			rm.setSysStatus(SysStatus.PARAM_ERROR);
			return rm;
		}
		if ("module".equals(resourceName) || "menu".equals(resourceName) || "button".equals(resourceName)) {
			key.append(userId).append(":").append("app").append(":").append(resourceName);
			try {
				List<String> resourceValues = redis.opsForList().range(key.toString(), 0, -1);
				if (!resourceValues.isEmpty()&&!resourceValues.get(0).isEmpty()) {
					JSONArray json = JSONArray.parseArray(resourceValues.get(0));
					rm.setSysStatus(SysStatus.SUCCESS);
					rm.setResult(json);
				} else {
					UserVo userVo = new UserVo();
					List<String> res_types = new ArrayList<String>();
					// 到数据库查，用户查系统，查是否管理员，分类查角色编码，查资源，保存资源，返回资源
					String controller = htBoaInUserAppService.findUserAndAppInfo(userId, app);
					if (!LogicUtil.isNullOrEmpty(controller)) {
						userVo.setController(controller);
					} else {
						log.info("用户来源不正确！");
						rm.setSysStatus(SysStatus.USER_NOT_MATCH_APP);
						return rm;
					}
					
					//查找所需资源
					if("module".equals(resourceName) ) {
						res_types.add(Constants.RES_TYPE_MODULE);
					}
					if("menu".equals(resourceName) ) {
						res_types.add(Constants.RES_TYPE_VIEW);
						res_types.add(Constants.RES_TYPE_GROUP);
					}

					if("button".equals(resourceName) ) {
						res_types.add(Constants.RES_TYPE_BUTTON);
						res_types.add(Constants.RES_TYPE_TAB);
					}
						List<String> allRoleCodes=htBoaInUserRoleService.getAllRoleCodes(userId);
						
						List<String> res_code = htBoaInRoleResService.queryResByCode(allRoleCodes);
						
						
						if (res_code.size() > 0) {
							List<ResVo> res = htBoaInResourceService.queryResForN(res_code, res_types, app);
								if(res.isEmpty()) {
									rm.setSysStatus(SysStatus.NO_RESULT);
									return rm;
								}			
							redis.opsForList().leftPushAll(key.toString(), FastJsonUtil.objectToJson(res));
							rm.setResult(res);
							rm.setSysStatus(SysStatus.SUCCESS);
							return rm;
					}
						
						
				} 
			} catch (Exception e) {
				e.printStackTrace();
				rm.setSysStatus(SysStatus.ERROR);
				return rm;
			}
			return rm;
		} else {
			rm.setSysStatus(SysStatus.PARAM_ERROR);
			return rm;
		}
	}


	/**
	 * 接收API资源，并存入数据库<br>
	 *
	 * @param apiResourceDto API资源信息
	 * @author 谭荣巧
	 * @Date 2018/1/18 21:41
	 */
    @ApiOperation(value = "同步API资源（内部接口）")
    @PostMapping("/api/aynch")
    @Transactional
    public void resourceApiAynch(@RequestBody ApiResourceDto apiResourceDto) {
        if (apiResourceDto != null && apiResourceDto.getApiInfoList() != null && apiResourceDto.getApiInfoList().size() > 0) {
            int num = 0;
            List<HtBoaInResource> oldResList = htBoaInResourceService.getByApp(apiResourceDto.getApp());
            List<HtBoaInResource> tempList;
            HtBoaInResource oldRes;
            List<ApiInfoDto> apiDtoList = apiResourceDto.getApiInfoList();
            List<HtBoaInResource> newResList = new ArrayList<>();
            for (ApiInfoDto api : apiDtoList) {
                tempList = oldResList.stream().filter(demo -> api.getMethod().equals(demo.getRemark())).distinct().collect(Collectors.toList());
                if (tempList != null && tempList.size() > 0) {
                    oldRes = tempList.get(0);
                    if ((oldRes.getResNameCn() != null && !oldRes.getResNameCn().equals(api.getApiDescribe()))
                            || (oldRes.getResContent() != null && !oldRes.getResContent().equals(api.getMapping()))) {
                        oldRes.setResNameCn(api.getApiDescribe());
                        oldRes.setResContent(api.getMapping());
                        newResList.add(oldRes);
                    }
                } else {
                    HtBoaInResource re = new HtBoaInResource();
                    re.setResCode("api_" + System.currentTimeMillis() + (num++));
                    re.setResType("api");
                    re.setResContent(api.getMapping());
                    re.setRemark(api.getMethod());
                    re.setResNameCn(api.getApiDescribe());
                    re.setApp(apiResourceDto.getApp());
                    re.setCreateOperator("自动创建");
                    re.setUpdateOperator("自动创建");
                    re.setSequence(1);
                    re.setStatus("0");
                    re.setDelFlag(0);
                    newResList.add(re);
                }
            }
            htBoaInResourceService.save(newResList);
        }
    }
}
