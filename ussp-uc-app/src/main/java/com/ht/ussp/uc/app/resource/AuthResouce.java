package com.ht.ussp.uc.app.resource;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ht.ussp.uc.app.common.Constants;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.model.SysStatus;
import com.ht.ussp.uc.app.service.HtBoaInResourceService;
import com.ht.ussp.uc.app.service.HtBoaInRoleResService;
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

	/**
	 * 
	 * @Title: saveResourcesToRedis 
	 * @Description: 保存所有资源到REDIS 
	 * @return ResponseModal
	 * @throws
	 */
	@PostMapping(value = "/saveResources")
	@ApiOperation(value = "获取并保存用户资源")
	public ResponseModal saveResourcesToRedis(@RequestBody UserVo userVo, @RequestParam(value = "roleCodes", required = true) List<String> roleCodes) {
		ResponseModal rm = new ResponseModal();
		if (null == userVo || LogicUtil.isNullOrEmpty(userVo.getUserId()) || LogicUtil.isNullOrEmpty(userVo.getApp())) {
			rm.setSysStatus(SysStatus.MAILPARAM_ERROR);
			return rm;
		}
		String userId = userVo.getUserId();
		String app = userVo.getApp();
		String controller = userVo.getController();

	    List<String> res_types = new ArrayList<String>();
	    
	    //定义权限分组名称LIST 
		List<ResVo> module_res=new ArrayList<ResVo>();
		List<ResVo> menu_res=new ArrayList<ResVo>();
		List<ResVo> button_res=new ArrayList<ResVo>();
		List<ResVo> api_res=new ArrayList<ResVo>();
		
		//定义存储到LIST中的资源KEY值
		StringBuffer module_key=new StringBuffer();
		StringBuffer menu_key=new StringBuffer();
		StringBuffer button_key=new StringBuffer();
		StringBuffer api_key=new StringBuffer();
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
			if (res.size() > 0) {
				for(int i=0;i<res.size();i++){
					if(Constants.RES_TYPE_MODULE.equals(res.get(i).getResType())) {
						//保存模块资源
						module_res.add(res.get(i));
						log.info(module_res);
					}else if(Constants.RES_TYPE_VIEW.equals(res.get(i).getResType())|| Constants.RES_TYPE_GROUP.equals(res.get(i).getResType())){
						//保存菜单资源
						menu_res.add(res.get(i));
					}else if(Constants.RES_TYPE_BUTTON.equals(res.get(i).getResType())
							|| Constants.RES_TYPE_TAB.equals(res.get(i).getResType())) {
						//保存按钮资源
						button_res.add(res.get(i));
						
					}else if(Constants.RES_TYPE_API.equals(res.get(i).getResType())) {
						//保存API
						api_res.add(res.get(i));
					}else {
						log.info("REDIS连接异常");
						rm.setSysStatus(SysStatus.ERROR);
					}
				}
		}
		}

		// 非管理员权限操作
		if ("N".equals(controller)) {
			List<String> res_code = htBoaInRoleResService.queryResByCode(roleCodes);
			if (res_code.size() > 0) {
				List<ResVo> res = htBoaInResourceService.queryResForN(res_code, res_types, app);
				if (res.size() > 0) {
						for(int i=0;i<res.size();i++){
							if(Constants.RES_TYPE_MODULE.equals(res.get(i).getResType())) {
								//保存模块资源
								module_res.add(res.get(i));
								log.info(module_res);
							}else if(Constants.RES_TYPE_VIEW.equals(res.get(i).getResType())|| Constants.RES_TYPE_GROUP.equals(res.get(i).getResType())){
								//保存菜单资源
								menu_res.add(res.get(i));
							}else if(Constants.RES_TYPE_BUTTON.equals(res.get(i).getResType())
									|| Constants.RES_TYPE_TAB.equals(res.get(i).getResType())) {
								//保存按钮资源
								button_res.add(res.get(i));
								
							}else if(Constants.RES_TYPE_API.equals(res.get(i).getResType())) {
								//保存API
								api_res.add(res.get(i));
							}else {
								log.info("REDIS连接异常");
								rm.setSysStatus(SysStatus.ERROR);
							}
						}
				}

			}
			//登录需要重新获取资源，保存到REDIS
			if(module_res!=null&&module_res.size()>0) {
				redis.delete(module_key.toString());
				redis.opsForList().leftPushAll(module_key.toString(), FastJsonUtil.objectToJson(module_res));
			}
			
			if(menu_res!=null&&menu_res.size()>0) {
				redis.delete(menu_key.toString());
				redis.opsForList().leftPushAll(menu_key.toString(), FastJsonUtil.objectToJson(menu_res));
			}
			
			if(button_res!=null&&button_res.size()>0) {
				redis.delete(button_key.toString());
				redis.opsForList().leftPushAll(button_key.toString(), FastJsonUtil.objectToJson(button_res));
			}
			
			if(api_res!=null&&api_res.size()>0) {
				redis.delete(api_key.toString());
				redis.opsForList().leftPushAll(api_key.toString(), FastJsonUtil.objectToJson(api_res));
			}
			
		}
		rm.setSysStatus(SysStatus.SUCCESS);

		return rm;

	}

	
	@GetMapping(value = "/IsHasAuth")
	@ApiOperation(value = "验证资源")
	public Boolean IsHasAuth(@RequestParam("key") String key, @RequestParam("url") String url) {
		Boolean flag=false;
		if(LogicUtil.isNullOrEmpty(key)||LogicUtil.isNullOrEmpty(url)) {
			return flag;
		}
		try {
		List<String> apiValues=redis.opsForList().range(key, 0, -1);
		JSONArray json=JSONArray.parseArray(apiValues.get(0));
		
		if(json.size()>0) {
			for(int i=0;i<json.size();i++) {
				JSONObject job = json.getJSONObject(i);
				if(url.equals(job.get("resContent"))) {
				log.info("isHasAuth:"+url.equals(job.get("resContent")));
				flag=true;
				return flag;
				}
			}
			
		}
		}catch(Exception e) {
			e.printStackTrace();
			return flag;
		}
		return flag;
	};
	
	
	
	/**
	 * 
	 * @Title: queryApi 
	 * @Description: 查询拥有的API权限 
	 * @return ResponseModal
	 * @throws
	 */
	@GetMapping(value = "/queryApi")
	@ApiOperation(value = "查询API")
	public ResponseModal queryApi(UserVo userVo) {
		ResponseModal rm = new ResponseModal();
		if (null == userVo || LogicUtil.isNullOrEmpty(userVo.getUserId()) || LogicUtil.isNullOrEmpty(userVo.getApp())) {
			rm.setSysStatus(SysStatus.PARAM_ERROR);
			return rm;
		}
		String userId = userVo.getUserId();
		String app = userVo.getApp();
		String controller = userVo.getController();
		if ("Y".equals(controller)) {

		}
		return rm;
	}

	/**
	 * 
	 * @Title: querybuttonAndTab 
	 * @Description: 查询拥有的按钮权限 
	 * @return ResponseModal
	 * @throws
	 */
	@GetMapping(value = "/querybuttonAndTab")
	@ApiOperation(value = "查询按钮")
	public ResponseModal querybuttonAndTab(UserVo userVo) {
		ResponseModal rm = new ResponseModal();
		if (null == userVo || LogicUtil.isNullOrEmpty(userVo.getUserId()) || LogicUtil.isNullOrEmpty(userVo.getApp())) {
			rm.setSysStatus(SysStatus.MAILPARAM_ERROR);
			return rm;
		}
		String userId = userVo.getUserId();
		String app = userVo.getApp();
		String controller = userVo.getController();
		if ("Y".equals(controller)) {

		}
		return rm;
	}

	/**
	 * 
	 * @Title: queryModule 
	 * @Description: 查询拥有的模块权限 
	 * @return ResponseModal
	 * @throws
	 */
	@GetMapping(value = "/queryModule")
	@ApiOperation(value = "查询模块")
	public ResponseModal queryModule(UserVo userVo) {
		ResponseModal rm = new ResponseModal();
		if (null == userVo || LogicUtil.isNullOrEmpty(userVo.getUserId()) || LogicUtil.isNullOrEmpty(userVo.getApp())) {
			rm.setSysStatus(SysStatus.MAILPARAM_ERROR);
			return rm;
		}
		String userId = userVo.getUserId();
		String app = userVo.getApp();
		String controller = userVo.getController();
		if ("Y".equals(controller)) {

		}
		return rm;
	}

	/**
	 * 
	 * @Title: queryViewAndGroup 
	 * @Description: 查询拥有的菜单权限 
	 * @return ResponseModal
	 * @throws
	 */
	@GetMapping(value = "/queryViewAndGroup")
	@ApiOperation(value = "查询菜单")
	public ResponseModal queryViewAndGroup(UserVo userVo) {
		ResponseModal rm = new ResponseModal();
		if (null == userVo || LogicUtil.isNullOrEmpty(userVo.getUserId()) || LogicUtil.isNullOrEmpty(userVo.getApp())) {
			rm.setSysStatus(SysStatus.MAILPARAM_ERROR);
			return rm;
		}
		String userId = userVo.getUserId();
		String app = userVo.getApp();
		String controller = userVo.getController();
		if ("Y".equals(controller)) {

		}
		return rm;
	}

}
