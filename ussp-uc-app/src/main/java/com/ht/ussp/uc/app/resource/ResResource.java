/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: ResResource.java
 * Author:   谭荣巧
 * Date:     2018/1/18 21:40
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.resource;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ht.ussp.bean.LoginUserInfoHelper;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInResource;
import com.ht.ussp.uc.app.domain.HtBoaInRole;
import com.ht.ussp.uc.app.domain.HtBoaInRoleRes;
import com.ht.ussp.uc.app.feignclients.DssClient;
import com.ht.ussp.uc.app.service.HtBoaInAppService;
import com.ht.ussp.uc.app.service.HtBoaInResourceService;
import com.ht.ussp.uc.app.service.HtBoaInRoleResService;
import com.ht.ussp.uc.app.service.HtBoaInRoleService;
import com.ht.ussp.uc.app.vo.AnalysisResModel;
import com.ht.ussp.uc.app.vo.AppAndResourceVo;
import com.ht.ussp.uc.app.vo.PageVo;
import com.ht.ussp.uc.app.vo.RelevanceApiVo;
import com.ht.ussp.uc.app.vo.ResourcePageVo;
import com.ht.ussp.util.JsonUtil;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

/**
 * 资源操作类<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/1/18 21:40
 */
@RestController
@RequestMapping(value = "/resource")
@Log4j2
public class ResResource {


    @Autowired
    private LoginUserInfoHelper loginUserInfoHelper;
    @Autowired
    private HtBoaInResourceService htBoaInResourceService;
    @Autowired
    private HtBoaInAppService htBoaInAppService;
    
    @Autowired
    private HtBoaInRoleResService htBoaInRoleResService;
    @Autowired
    private HtBoaInRoleService htBoaInRoleService;
    @Autowired
    private DssClient dssClient;
    

    @PostMapping("/app/load")
    public List<AppAndResourceVo> loadAppAndResourceTreeData() {
        return htBoaInAppService.loadAppAndResourceVoList();
    }

    /**
     * 资源分页查询<br>
     *
     * @param page 分页参数对象
     * @return 结果对象
     * @author 谭荣巧
     * @Date 2018/1/12 9:01
     */
    @ApiOperation(value = "自信信息分页查询")
    @PostMapping(value = "/page/load", produces = {"application/json"})
    public PageResult loadListByPage(ResourcePageVo page) {
        return htBoaInResourceService.getPage(page.getPageRequest(), page.getApp(), page.getParentCode(), page.getResType(), page.getKeyWord());
    }

    /**
     * 获取资源编码<br>
     *
     * @param parent 父资源编码
     * @param type   资源类型
     * @return
     * @author 谭荣巧
     * @Date 2018/1/30 20:26
     */
    @GetMapping(value = "/rescode/load")
    public String loadResCode(@RequestParam("app") String app, @RequestParam("parent") String parent, @RequestParam("type") String type) {
        return htBoaInResourceService.createResourceCode(app, parent, type);
    }
    
    /**
     * 系统菜单组
     *
     */
    @GetMapping(value = "/getMenuGroup")
    public Result getMenuGroup(@RequestParam("app") String app, @RequestParam("resType") String resType) {
    	List<HtBoaInResource> listHtBoaInResource = htBoaInResourceService.findByAppAndResType(app,resType);
    	//List<HtBoaInResource> newListHtBoaInResource = listHtBoaInResource.stream().filter(res -> "0".equals(res.getStatus())).collect(Collectors.toList());
        return Result.buildSuccess(listHtBoaInResource);
    }

    /**
     * 新增资源信息<br>
     *
     * @param resource 资源信息
     * @return com.ht.ussp.core.Result
     * @author 谭荣巧
     * @Date 2018/1/14 12:08
     */
    @PostMapping(value = "/add")
    public Result addAsync(@RequestBody HtBoaInResource resource, @RequestHeader("userId") String userId) {
        if (resource != null) {
            switch (resource.getResType()) {
                case "menu":
                    if (StringUtils.isEmpty(resource.getResContent())) {
                        resource.setResType("group");
                    } else {
                        resource.setResType("view");
                    }
                    break;
                case "btn":
                    if (StringUtils.isEmpty(resource.getResCode())) {
                        resource.setResCode(htBoaInResourceService.createResourceCode(resource.getApp(), resource.getResParent(), "btn"));
                    }
                    break;
            }
            resource.setStatus("0");
            resource.setResParent(StringUtils.isEmpty(resource.getResParent())?null:resource.getResParent());
            resource.setCreateOperator(userId);
            resource.setUpdateOperator(userId);
            resource.setDelFlag(0);
            resource.setJpaVersion(0);
            resource.setSequence((StringUtils.isEmpty(resource.getSequence())?0:resource.getSequence()));
            resource = htBoaInResourceService.save(resource);
            if (resource != null && !StringUtils.isEmpty(resource.getId())) {
            	try {
            		if("api".equals(resource.getResType())) {
            			List<HtBoaInResource> newList = new ArrayList<HtBoaInResource>();
            			newList.add(resource);
            			htBoaInResourceService.bindParentRole(resource.getResParent(), newList, userId);
            		}
    			} catch (Exception e) {
    			}
                return Result.buildSuccess();
            }
        }
        return Result.buildFail();
    }

    @PostMapping("/delete")
    public Result delAsync(Long id) {
        HtBoaInResource resource = htBoaInResourceService.getOne(id);
        if (resource != null) {
            List<HtBoaInResource> resourceList = htBoaInResourceService.getByResParent(resource.getResCode());
            if (resourceList != null && resourceList.size() > 0) {
                return new Result().returnCode("10000").codeDesc("该资源存在下级资源，请先删除下级资源 。");
            }
            //如果是api 若api是挂靠的最后一个菜单 则删除关联关系即可
            if("api".equals(resource.getResType())) {
            	List<HtBoaInResource> resourceList2 = htBoaInResourceService.findByResNameCnAndResContentAndAppAndRemark(resource.getResNameCn(), resource.getResContent(), resource.getApp(),resource.getRemark());
                if(resourceList2!=null&&resourceList2.size()==1) {
                	HtBoaInResource resourceDel = resourceList2.get(0);
                	resourceDel.setResParent("");
                	htBoaInResourceService.save(resourceDel);
                }else {
                	htBoaInResourceService.delete(id);
                }
            }else {
            	htBoaInResourceService.delete(id);
            }
        }
        return Result.buildSuccess();
    }

    @PostMapping("/view")
    public Result viewAsync(Long id) {
        HtBoaInResource resource = htBoaInResourceService.getOne(id);
        if (resource != null) {
            return Result.buildSuccess(resource);
        }
        return Result.buildFail();
    }

    @PostMapping("/update")
    public Result updateAsync(@RequestBody HtBoaInResource resource, @RequestHeader("userId") String userId) {
        if (resource != null) {
        	List<HtBoaInResource> listHtBoaInResource = htBoaInResourceService.findByResCodeAndApp(resource.getResCode(), resource.getApp());
        	HtBoaInResource htBoaInResource = null;
        	if(listHtBoaInResource!=null && !listHtBoaInResource.isEmpty()) {
        		htBoaInResource = listHtBoaInResource.get(0);
        	}
            if ("menu".equals(resource.getResType()) && StringUtils.isEmpty(resource.getResContent())) {
                resource.setResType("group");
            } else if ("menu".equals(resource.getResType())) {
                resource.setResType("view");
            }
            if(htBoaInResource!=null) { //如果是菜单组则不能移动
        		if("group".equals(resource.getResType())) {
        			resource.setResParent(htBoaInResource.getResParent());
        		}
        	}
            resource.setResParent(StringUtils.isEmpty(resource.getResParent())?null:resource.getResParent());
            if(StringUtils.isEmpty(resource.getResCode())) {
            	 return Result.buildFail();
            } 
            resource.setUpdateOperator(userId);
            resource.setLastModifiedDatetime(new Date());
            
            if(!resource.getResCode().equals(resource.getResParent())) {
            	resource = htBoaInResourceService.save(resource);
            }
            if (resource != null) {
                return Result.buildSuccess();
            }
        }
        return Result.buildFail();
    }

    /**
     * 菜单关联API资源<br>
     *
     * @param relevanceApiVo 关联的api资源对象
     * @return 请求结果
     * @author 谭荣巧
     * @Date 2018/1/20 15:22
     */
    @PostMapping("/relevance")
    public Result relevance(@RequestBody RelevanceApiVo relevanceApiVo) {
        boolean isRelevance = htBoaInResourceService.relevance(relevanceApiVo.getApp(), relevanceApiVo.getParentCode(), relevanceApiVo.getResourceList(), loginUserInfoHelper.getUserId());
        if (isRelevance) {
            return Result.buildSuccess();
        }
        return Result.buildFail();
    }

    @ApiOperation(value = "API资源信息分页查询")
    @PostMapping(value = "/api/page/load", produces = {"application/json"})
    public PageResult loadApiByPage(ResourcePageVo page) {
        return htBoaInResourceService.loadApiByPage(page);
    }
    
    
    @ApiOperation(value = "API资源状态修改 状态（1，禁用，0，启用，2，隐藏）")
    @PostMapping(value = "/changeApiState", produces = {"application/json"})
    public Result changeApiState(Long id,String status) {
        htBoaInResourceService.changeApiState(id,status);
    	return Result.buildSuccess();
    }
    
   
	/**
	 * 提供外部，新增菜单
	 * 
	 * @param resource
	 * @param userId
	 * @return
	 */
    @GetMapping(value = "/addMenu")
	public Result addMenu(@RequestParam("resNameCn") String resNameCn, @RequestParam("resContent") String resContent,
			@RequestParam("fontIcon") String fontIcon, @RequestParam("resParent") String resParent,
			@RequestParam("resParentName") String resParentName, @RequestParam("roles") String[] roles,
			@RequestParam("userId") String userId, @RequestParam("app") String app) {
		HtBoaInResource resource = new HtBoaInResource();
		if (StringUtils.isEmpty(resContent)) {
			resource.setResType("group");
		} else {
			resource.setResType("view");
		}
		resource.setResNameCn(resNameCn);
		resource.setResContent(resContent);
		resource.setFontIcon(fontIcon);
		resource.setResParent(resParent);
		resource.setResParent(StringUtils.isEmpty(resource.getResParent())?null:resource.getResParent());
		resource.setApp(app);
		String resCode = htBoaInResourceService.createMenuCode(app, resParent);
		resource.setResCode(resCode);
		resource.setSequence(Integer.parseInt(resCode.substring(resCode.length()-2, resCode.length())));
		resource.setStatus("0");
		resource.setCreateOperator(userId);
		resource.setCreatedDatetime(new Date());
		resource.setDelFlag(0);
		resource.setJpaVersion(0);
		resource = htBoaInResourceService.save(resource);
		if (resource != null && !StringUtils.isEmpty(resource.getId())) {
			for(String role :roles) {
				HtBoaInRoleRes u = new HtBoaInRoleRes();
				u.setResCode(resource.getResCode());
				u.setRoleCode(role);
				u.setCreatedDatetime(new Date());
				u.setCreateOperator(userId);
				u.setDelFlag(0);
				htBoaInRoleResService.save(u);
			}
			return Result.buildSuccess(resource.getResCode());
		}
		return Result.buildFail();
	}
	/**
	 * 提供外部，修改菜单
	 * 
	 * @param resource
	 * @param userId
	 * @return
	 */
	@GetMapping("/updateMenu")
	public Result updateMenu(@RequestParam("resCode") String resCode,@RequestParam("resNameCn") String resNameCn, @RequestParam("resContent") String resContent,
			@RequestParam("fontIcon") String fontIcon, @RequestParam("roles") String[] roles,
			@RequestParam("userId") String userId, @RequestParam("app") String app) {
		if(org.apache.commons.lang3.StringUtils.isNoneEmpty(resCode)) {
			List<HtBoaInResource> listHtBoaInResource = htBoaInResourceService.findByResCodeAndApp(resCode,app);
			if(listHtBoaInResource!=null&&!listHtBoaInResource.isEmpty()) {
				HtBoaInResource resource = listHtBoaInResource.get(0);
				if (StringUtils.isEmpty(resContent)) {
					resource.setResType("group");
				} else {
					resource.setResType("view");
				}
				resource.setResNameCn(resNameCn);
				resource.setResContent(resContent);
				resource.setFontIcon(fontIcon);
				resource.setUpdateOperator(userId);
				resource.setLastModifiedDatetime(new Date());
				resource = htBoaInResourceService.save(resource);
				if (resource != null) {
					for(String role :roles) {
						List<HtBoaInRoleRes> listHtBoaInRoleRes = htBoaInRoleResService.findByResCodeAndRoleCode(resource.getResCode(),role);
						for(HtBoaInRoleRes u:listHtBoaInRoleRes) {
							htBoaInRoleResService.deleteById(u.getId());
							u.setResCode(resource.getResCode());
							u.setRoleCode(role);
							u.setCreatedDatetime(new Date());
							u.setCreateOperator(userId);
							u.setDelFlag(0);
							htBoaInRoleResService.save(u);
						}
					}
					return Result.buildSuccess(resource);
				}
			}
		}
		return Result.buildFail();
	}
	
	@ApiOperation(value = "API资源状态修改 状态（1，禁用，0，启用，2，隐藏）")
    @GetMapping(value = "/changeMenuState")
    public Result changeMenuState(@RequestParam("resCode")String resCode, @RequestParam("status")String status,@RequestParam("userId")String userId,@RequestParam("app")String app) {
		if(org.apache.commons.lang3.StringUtils.isNoneEmpty(resCode)) {
			List<HtBoaInResource> listHtBoaInResource = htBoaInResourceService.findByResCodeAndApp(resCode, app);
			if(listHtBoaInResource!=null&&!listHtBoaInResource.isEmpty()) {
				HtBoaInResource resource = listHtBoaInResource.get(0);
				if(resource!=null) {
					htBoaInResourceService.changeApiState(resource.getId(),status);
				}
			}
		}
    	return Result.buildSuccess();
    }
	
    @GetMapping(value = "/getMenus")
    public Result getMenus(@RequestParam("app")String app,@RequestParam("resType")String resType) {
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(app)) {
			resType = org.apache.commons.lang3.StringUtils.isNotEmpty(resType)?resType:"view";
			List<HtBoaInResource> listHtBoaInResource = htBoaInResourceService.findByAppAndResType(app,resType);
			return Result.buildSuccess(listHtBoaInResource);
		}
    	return Result.buildSuccess();
    }
    
    /**
     * 导入
     *
     * @param request
     * @param response
     */
    @PostMapping(value = "/importResourceExcel")
    public Result importResourceExcel(HttpServletRequest request, HttpServletResponse response, @RequestHeader("userId") String userId ,@RequestParam("app")String app) {
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
            htBoaInResourceService.importResExcel(in, file, userId,app);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.buildFail();
        }
        return Result.buildSuccess();
    }
    
    /**
     * json数据分析
     * @param request
     * @param response
     */
    @PostMapping(value = "/analysisResData")
    public Result analysisResData(@RequestHeader("userId") String userId ,@RequestParam("jsonData")String jsonData) {
        try {  
        	//JsonUtil.json2Map(jsonData);
        	Map<String, String> analysisResult = new HashMap<>();
        	String isAnais = "false"; // 是否生成了升级脚本 false没有 true生成了脚本
        	String app = "";
        	StringBuffer sbf = new  StringBuffer("");
        	StringBuffer fallbacksbf = new  StringBuffer("");
        	AnalysisResModel analysisResModel = JsonUtil.json2Obj(jsonData, AnalysisResModel.class);
        	if(analysisResModel==null) {
        		return Result.buildFail("9999","analysisResModel is null");
        	}
        	//资源表
        	if(!StringUtils.isEmpty(analysisResModel.getResource())) {
        		List<HtBoaInResource> listHtBoaInResource = JsonUtil.json2List(analysisResModel.getResource(), HtBoaInResource.class);
            	//进行数据分析，生成脚本
            	Map<String, String> analysisResData = htBoaInResourceService.analysisResData(listHtBoaInResource); //资源
            	if(analysisResData!=null) {
            		if("false".equals(isAnais)) {
            			isAnais = analysisResData.get("isAnais");
            		}
            		app = analysisResData.get("app");
            		sbf.append(analysisResData.get("resultData"));
            		fallbacksbf.append(analysisResData.get("resultDataBack"));
            	}
        	} 
        	//角色表
        	if(!StringUtils.isEmpty(analysisResModel.getRole())) {
        		List<HtBoaInRole> listHtBoaInRole = JsonUtil.json2List(analysisResModel.getRole(), HtBoaInRole.class);
            	//进行数据分析，生成脚本
            	Map<String, String> analysisRoleData = htBoaInRoleService.analysisRoleData(listHtBoaInRole); //角色
            	if(analysisRoleData!=null) {
            		if("false".equals(isAnais)) {
            			isAnais = analysisRoleData.get("isAnais");
            		}
            		app = analysisRoleData.get("app");
            		sbf.append(analysisRoleData.get("resultData"));
            		fallbacksbf.append(analysisRoleData.get("resultDataBack"));
            	}
        	}
        	//角色资源表
        	if(!StringUtils.isEmpty(analysisResModel.getRoleResource())) {
        		List<HtBoaInRoleRes> listHtBoaInRoleRes = JsonUtil.json2List(analysisResModel.getRoleResource(), HtBoaInRoleRes.class);
            	//进行数据分析，生成脚本
            	Map<String, String> analysisRoleResData = htBoaInRoleResService.analysisRoleResData(listHtBoaInRoleRes); //角色资源
            	if(analysisRoleResData!=null) {
            		if("false".equals(isAnais)) {
            			isAnais = analysisRoleResData.get("isAnais");
            		}
            		app = analysisRoleResData.get("app");
            		sbf.append(analysisRoleResData.get("resultData"));
            		fallbacksbf.append(analysisRoleResData.get("resultDataBack"));
            	}
        	}
        	if("true".equals(isAnais)) {
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        		String resultDataCode = sdf.format(new Date());
        		analysisResult.put("resultData", sbf.toString());//
        		analysisResult.put("resultDataBack", fallbacksbf.toString());// 
        		analysisResult.put("resultDataCode", resultDataCode);// 解析版本编码
        		htBoaInResourceService.saveAnaisSql(resultDataCode, app, sbf.toString(), fallbacksbf.toString());
        	}
        	return Result.buildSuccess(analysisResult);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.buildFail(e.getLocalizedMessage(),e.getMessage());
        }
    }
    
    @ApiOperation(value = "资源列表维护-查询分页", notes = "列出所有记录列表信息")
    @PostMapping(value = "/listByPage", produces = { "application/json" })
	public PageResult<List<HtBoaInResource>> listByPage(PageVo page) {
    	List<Sort.Order> orders = new ArrayList<>();
		orders.add(new Sort.Order(Sort.Direction.DESC, "resType"));
		orders.add(new Sort.Order(Sort.Direction.ASC, "resCode"));
		Sort sort = new Sort(orders);
		return htBoaInResourceService.listByPage(new PageRequest(page.getPage(), page.getLimit(),sort), page.getQuery());
	}
    
    @PostMapping("/deleteTrunc")
    public Result deleteTrunc(Long id) {
        HtBoaInResource resource = htBoaInResourceService.getOne(id);
        try {
        	htBoaInResourceService.delete(id);
		} catch (Exception e) {
			return Result.buildFail(e.getLocalizedMessage(), e.getMessage());
		}
        return Result.buildSuccess();
    }
    
    @ApiOperation(value = "根据资源编码获取资源信息", notes = "验证资源编码是否存在")
    @PostMapping("/getResByResCode")
    public Result getResByResCode(String resCode) {
        return Result.buildSuccess(htBoaInResourceService.findByResCode(resCode));
    }
    
    
    @ApiOperation(value = "数据权限规则列表", notes = "列出所有数据权限规则")
    @PostMapping(value = "/listRuleByPage", produces = { "application/json" })
	public PageResult  listRuleByPage(PageVo page) {
    	PageResult pageResult = new PageResult();
    	Map<String, Object> paramter = new HashMap<String, Object>();
    	paramter.put("keyWord", page.getKeyWord());
    	paramter.put("limit", page.getLimit());
    	paramter.put("page",page.getPage()+1 );
    	Map<String, Object> reslut = dssClient.getRuleInfos(paramter);
    	if(reslut!=null) {
    		Integer count = Integer.parseInt(reslut.get("count")==null?"0":reslut.get("count")+"");
    		pageResult.count(count).data(reslut.get("data"));
    	}
    	pageResult.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
		return pageResult;
	}
    
    @ApiOperation(value = "添加数据权限规则", notes = "添加数据权限规则")
    @PostMapping(value = "/addApiRule", produces = { "application/json" })
	public Result  addApiRule(String resCode,String num,String numName) {
    	List<HtBoaInResource>  listHtBoaInResource = htBoaInResourceService.findByResCode(resCode);
    	if(listHtBoaInResource!=null&&!listHtBoaInResource.isEmpty()) {
    		HtBoaInResource u = listHtBoaInResource.get(0);
    		u.setRuleNum(num);
    		u.setRuleNumName(numName);
    		htBoaInResourceService.save(u);
    	}
		return Result.buildSuccess();
	}
}
