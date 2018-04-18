package com.ht.ussp.uc.app.resource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.esotericsoftware.minlog.Log;
import com.ht.ussp.common.Constants;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.DdDept;
import com.ht.ussp.uc.app.domain.DdDeptOperator;
import com.ht.ussp.uc.app.domain.DdUser;
import com.ht.ussp.uc.app.domain.DdUserOperator;
import com.ht.ussp.uc.app.domain.HtBoaInBmOrg;
import com.ht.ussp.uc.app.domain.HtBoaInBmUser;
import com.ht.ussp.uc.app.domain.HtBoaInContrast;
import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.service.DingDingService;
import com.ht.ussp.uc.app.service.HtBoaInBmOrgService;
import com.ht.ussp.uc.app.service.HtBoaInBmUserService;
import com.ht.ussp.uc.app.service.HtBoaInContrastService;
import com.ht.ussp.uc.app.service.HtBoaInLoginService;
import com.ht.ussp.uc.app.service.HtBoaInOrgService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;
import com.ht.ussp.uc.app.service.SynDataService;
import com.ht.ussp.uc.app.vo.PageVo;
import com.ht.ussp.uc.app.vo.UserContrastVo;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

 
@RestController
@RequestMapping(value = "/syndata")
@Log4j2
public class SynDataResource {
    @Autowired
    private HtBoaInOrgService htBoaInOrgService;
    @Autowired
    private HtBoaInUserService htBoaInUserService;
    @Autowired
    private HtBoaInContrastService htBoaInContrastService;
    @Autowired
    private HtBoaInBmUserService htBoaInBmUserService;
    @Autowired
    private SynDataService synDataService;
    @Autowired
    private HtBoaInLoginService htBoaInLoginService;
    @Autowired
    private HtBoaInBmOrgService htBoaInBmOrgService;
    @Autowired
    private DingDingService dingDingService;
    
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
		List<HtBoaInUser> htBoaInUserList= htBoaInUserService.findAll();//所有UC用户
		List<DdUser> listDdUser = dingDingService.getDDUserList(); //获取钉钉所有用户数据
		List<HtBoaInContrast> listHtBoaInContrastUser = htBoaInContrastService.getHtBoaInContrastListByType("20");//获取UC已存在数据
		List<HtBoaInContrast> updateList = new ArrayList<HtBoaInContrast>();
    	
		for (DdUser ddUser : listDdUser) {
			if (StringUtils.isNotEmpty(ddUser.getMobile())) {
				Optional<HtBoaInUser> htBoaInUserOptional  = htBoaInUserList.stream().filter(ucUser -> ddUser.getMobile().equals(ucUser.getMobile())).findFirst();
				if (htBoaInUserOptional != null && htBoaInUserOptional.isPresent()) {
					HtBoaInUser htBoaInUser = htBoaInUserOptional.get();
					if(htBoaInUser==null) {
						continue;
					}else {
						Optional<HtBoaInContrast> htBoaInContrastOptional = listHtBoaInContrastUser.stream().filter(ucUser ->htBoaInUser.getUserId().equals(ucUser.getUcBusinessId())).findFirst();
	       				if(htBoaInContrastOptional!=null &&htBoaInContrastOptional.isPresent()) {
	       					try {
	       						HtBoaInContrast htBoaInContrast = htBoaInContrastOptional.get(); 
	       						htBoaInContrast.setDdBusinessId(ddUser.getUserId());
	       						updateList.add(htBoaInContrast);
							} catch (Exception e) {
								e.printStackTrace();
							}
	       					
	       				}
					}

				}
			}
		}
		if(updateList!=null && !updateList.isEmpty()) {
			htBoaInContrastService.add(updateList);
		}
		
        return Result.buildSuccess();
	}
	
	/**
	 * 数据整理
	 * @return
	 */
	@ApiOperation("03-钉钉数据整理（机构，用户）")
	@RequestMapping(value = {"/dealData" }, method = RequestMethod.POST)
	public Result dealData() {
		//1.获取钉钉用户 、机构保存到中间表
		List<DdDept> updateDDOrgList = new ArrayList<DdDept>(); //保存需要操作的钉钉机构
		List<DdDept>  ddOrgList = new ArrayList<DdDept>(); //保存需要添加的机构级别
		ddOrgList = dingDingService.getDdDeptList(); // 钉钉机构列表
		for (DdDept ddDept : ddOrgList) {// 组装新增的机构等级
			String path = getDDOrgPath(ddDept);
			Integer level = 0;
			if (path != null) {
				// 根据指定的字符构建正则
				Pattern pattern = Pattern.compile("/");
				// 构建字符串和正则的匹配
				Matcher matcher = pattern.matcher(path);
				int count = 0;
				// 循环依次往下匹配
				while (matcher.find()) { // 如果匹配,则数量+1
					count++;
				}
				level = count + 2;
			}
			if (StringUtils.isEmpty(ddDept.getParentId())) {
				level = 1;
			}
			ddDept.setLevel(level);
			updateDDOrgList.add(ddDept);
		}
		List<HtBoaInOrg> htBoaInOrgList = htBoaInOrgService.findAll();
    	List<HtBoaInUser> htBoaInUserList= htBoaInUserService.findAll();//所有UC用户
		
		///////////////机构数据整理///////////////
		if(updateDDOrgList!=null && !updateDDOrgList.isEmpty()) {
    		ddOrgList = dingDingService.saveOrUpdateDDDpet(updateDDOrgList); //保存需要添加的机构级别

    		List<HtBoaInContrast> listHtBoaInContrastOrg = htBoaInContrastService.getHtBoaInContrastListByType("10");
        	
        	List<DdDept> ddAddOrgList = new ArrayList<DdDept>();   //钉钉多增加出来的机构
        	ddAddOrgList.addAll(ddOrgList);
        	List<HtBoaInContrast> ddDelOrgList = new ArrayList<HtBoaInContrast>();  //UC已经存在的机构
        	ddDelOrgList.addAll(listHtBoaInContrastOrg);
        	List<DdDeptOperator> ddDeptOperatorList = new ArrayList<DdDeptOperator>();   //记录操作中间表
        	
        	List<DdDept> ddDeptOperatorListUpdate = new ArrayList<DdDept>();   //需要更新的机构

        	//增量步骤：1、对比已经存在的机构 如果钉钉有多出的机构 则新增 如果UC多出了机构则要为无效
        	for(HtBoaInContrast htBoaInContrast : listHtBoaInContrastOrg) { //钉钉多出了机构
        		List<DdDept> sameOrgList = ddOrgList.stream().filter(ddOrg -> htBoaInContrast.getDdBusinessId().equals(ddOrg.getDeptId())).collect(Collectors.toList());
        		ddDeptOperatorListUpdate.addAll(sameOrgList);
        		ddAddOrgList.removeAll(sameOrgList);//得到钉钉新增的机构
        	}
        	
        	for(DdDept ddDept : ddAddOrgList) {
        		DdDeptOperator addDDDeptOperator = new DdDeptOperator();
        		addDDDeptOperator.setDeptId(ddDept.getDeptId());
        		addDDDeptOperator.setDeptName(ddDept.getDeptName());
        		addDDDeptOperator.setLevel(ddDept.getLevel());
        		addDDDeptOperator.setOrders(ddDept.getOrders());
        		addDDDeptOperator.setParentId(ddDept.getParentId());
        		addDDDeptOperator.setCreatDatetime(new Date());
        		addDDDeptOperator.setOperatorType(1);
        		addDDDeptOperator.setSynFlag("0");
        		ddDeptOperatorList.add(addDDDeptOperator);
        	}
        	

        	for(DdDept ddDept : ddOrgList) {//得到钉钉删除的机构
        		List<HtBoaInContrast> sameOrgList  = listHtBoaInContrastOrg.stream().filter(ucOrg -> ddDept.getDeptId().equals(ucOrg.getDdBusinessId())).collect(Collectors.toList());
        		ddDelOrgList.removeAll(sameOrgList);
        	}
        	for(HtBoaInContrast delHtBoaInContrast : ddDelOrgList) {
        		HtBoaInOrg delOrg =  htBoaInOrgList.stream().filter(org -> delHtBoaInContrast.getUcBusinessId().equals(org.getOrgCode())).findFirst().get();
        		if(delOrg.getDelFlag()==0) { //已经删除的不在处理
        			DdDeptOperator addDDDeptOperator = new DdDeptOperator();
            		addDDDeptOperator.setDeptId(delHtBoaInContrast.getUcBusinessId()); //保存uc待删除的org_code
            		addDDDeptOperator.setCreatDatetime(new Date());
            		addDDDeptOperator.setOperatorType(2);
            		addDDDeptOperator.setSynFlag("0");
            		ddDeptOperatorList.add(addDDDeptOperator);
        		}
        	}
        	for(DdDept ddDept : ddDeptOperatorListUpdate) {
        		DdDeptOperator addDDDeptOperator = new DdDeptOperator();
        		addDDDeptOperator.setDeptId(ddDept.getDeptId());
        		addDDDeptOperator.setDeptName(ddDept.getDeptName());
        		addDDDeptOperator.setLevel(ddDept.getLevel());
        		addDDDeptOperator.setOrders(ddDept.getOrders());
        		addDDDeptOperator.setParentId(ddDept.getParentId());
        		addDDDeptOperator.setCreatDatetime(new Date());
        		addDDDeptOperator.setOperatorType(3);
        		addDDDeptOperator.setSynFlag("0");
        		ddDeptOperatorList.add(addDDDeptOperator);
        	}
        	dingDingService.saveDeptOperator(ddDeptOperatorList);
    	}
		///////////////用户数据整理///////////////
		List<DdUser> listDdUser = dingDingService.getDDUserList(); //获取钉钉所有用户数据
		List<HtBoaInContrast> listHtBoaInContrastUser = htBoaInContrastService.getHtBoaInContrastListByType("20");//获取UC已存在数据
		
		List<DdUser> ddAddUserList = new ArrayList<DdUser>(); //钉钉多增加出来的用户
		ddAddUserList.addAll(listDdUser);
		List<HtBoaInContrast> ddDelUserList = new ArrayList<HtBoaInContrast>();  //UC已经存在的用户
		ddDelUserList.addAll(listHtBoaInContrastUser);
		
    	List<DdUserOperator> ddUserOperatorList = new ArrayList<DdUserOperator>();   //记录操作中间表
    	List<DdUser> ddUserOperatorListUpdate = new ArrayList<DdUser>();   //需要更新的用户
    	
		//增量步骤：1、对比已经存在的机构  如果钉钉有多出的用户 则新增 如果UC多出了y用户则要为无效
    	for(HtBoaInContrast htBoaInContrast : listHtBoaInContrastUser) { //钉钉多出了用户
    		List<DdUser> sameUserList = listDdUser.stream().filter(ddUsers -> ddUsers.getUserId().equals(htBoaInContrast.getDdBusinessId())).collect(Collectors.toList());
    		ddUserOperatorListUpdate.addAll(sameUserList);
    		ddAddUserList.removeAll(sameUserList);//得到钉钉新增的机构
    	}
    	
    	for(DdUser ddUser : ddAddUserList) {
    		DdUserOperator ddUserOperator = new DdUserOperator();
    		ddUserOperator.setUserId(ddUser.getUserId());
    		ddUserOperator.setDeptId(ddUser.getDeptId());
    		ddUserOperator.setUserName(ddUser.getUserName());
    		ddUserOperator.setEmail(ddUser.getEmail());
    		ddUserOperator.setIdNo(ddUser.getIdNo());
    		ddUserOperator.setMobile(ddUser.getMobile());
    		ddUserOperator.setJobNumber(ddUser.getJobNumber());
    		ddUserOperator.setPosition(ddUser.getPosition());
    		ddUserOperator.setCreatDatetime(new Date());
    		ddUserOperator.setOperatorType(1);
    		ddUserOperator.setSynFlag("0");
    		ddUserOperatorList.add(ddUserOperator);
    	}
    	
    	for(DdUser ddUser : listDdUser) {//得到钉钉删除的机构
    		List<HtBoaInContrast> sameUserList  = listHtBoaInContrastUser.stream().filter(ucUser -> ddUser.getUserId().equals(ucUser.getDdBusinessId())).collect(Collectors.toList());
    		ddDelUserList.removeAll(sameUserList);
    	}
    	
    	for(HtBoaInContrast delHtBoaInContrast : ddDelUserList) {
    		HtBoaInUser delUser =  htBoaInUserList.stream().filter(user -> delHtBoaInContrast.getUcBusinessId().equals(user.getUserId())).findFirst().get();
    		if(delUser.getDelFlag()==0) { //已经删除的不在处理
    			DdUserOperator ddUserOperator = new DdUserOperator();
        		ddUserOperator.setUserId(delHtBoaInContrast.getUcBusinessId());
        		ddUserOperator.setCreatDatetime(new Date());
        		ddUserOperator.setOperatorType(2);
        		ddUserOperator.setSynFlag("0");
        		ddUserOperatorList.add(ddUserOperator);
    		}
    	}
    	
    	for(DdUser ddUser : ddUserOperatorListUpdate) {
    		DdUserOperator ddUserOperator = new DdUserOperator();
    		ddUserOperator.setUserId(ddUser.getUserId());
    		ddUserOperator.setDeptId(ddUser.getDeptId());
    		ddUserOperator.setUserName(ddUser.getUserName());
    		ddUserOperator.setEmail(ddUser.getEmail());
    		ddUserOperator.setIdNo(ddUser.getIdNo());
    		ddUserOperator.setMobile(ddUser.getMobile());
    		ddUserOperator.setJobNumber(ddUser.getJobNumber());
    		ddUserOperator.setPosition(ddUser.getPosition());
    		ddUserOperator.setCreatDatetime(new Date());
    		ddUserOperator.setOperatorType(3);
    		ddUserOperator.setSynFlag("0");
    		ddUserOperatorList.add(ddUserOperator);
    	}
    	dingDingService.saveUserOperator(ddUserOperatorList);
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
    	//对钉钉用户数据进行转换  a.将钉钉机构转换成UC机构 b.将新的机构赋值给用户
    	//a。将钉钉机构转换成UC机构  如果UC已经存在的机构 则不再重新生成UC的orgCode
    	DdDeptOperator u = new DdDeptOperator();
    	u.setOperatorType(1);
    	List<DdDeptOperator> addDeptList =  dingDingService.getDdDeptOperatorOrderByLevel(u);
    	List<DdDeptOperator> synDeptList =  new ArrayList<DdDeptOperator>();
    	if(addDeptList!=null&&!addDeptList.isEmpty()) {
    		for(int i = 0;i<addDeptList.size();i++) {
    			String newOrgCode ="";
    			DdDeptOperator ddDeptOperator = addDeptList.get(i);
    			//判断当前机构是否已经添加过 添加则不再添加
    			List<HtBoaInContrast> listContrastDept = htBoaInContrastService.getHtBoaInContrastListByDdBusinessId(ddDeptOperator.getDeptId(), "10");
    			if(listContrastDept!=null&&!listContrastDept.isEmpty()) {//判断当前机构是否已经添加过 添加则不再添加
    				continue;
    			}
    			
    			//获取orgCode 1.找到上级的UC机构，获取到maxCode
    			List<HtBoaInContrast> listContrastParent = htBoaInContrastService.getHtBoaInContrastListByDdBusinessId(ddDeptOperator.getParentId(), "10");
    			HtBoaInOrg parentOrg = null;
    			if(listContrastParent!=null&&!listContrastParent.isEmpty()) {
    				 List<HtBoaInOrg> parentOrgList = htBoaInOrgService.findByOrgCode(listContrastParent.get(0).getUcBusinessId());
    				 if(parentOrgList!=null&&!parentOrgList.isEmpty()) {
    					 parentOrg = parentOrgList.get(0);
    				 }

    				 if(parentOrg==null){
    					 parentOrg = new HtBoaInOrg();
    				 }
    				  String maxOrgCode = htBoaInOrgService.getMaxOrgCode(listContrastParent.get(0).getUcBusinessId());
			          if (StringUtils.isNotEmpty(maxOrgCode)) {
			                try { 
			                	BigDecimal indexs = new BigDecimal(maxOrgCode.replace("D0", "").replaceAll("[^0-9]", ""));
			                    newOrgCode = "D0"+(indexs.add(new BigDecimal("1")) );
			                } catch (Exception e) {
			                	e.printStackTrace();
			                	continue;
			                }
			           }else {
			        	   newOrgCode = parentOrg.getOrgCode()+"01";
			           }
    			}else {
    				if("58800327".equals(ddDeptOperator.getDeptId())) {
    					parentOrg = new HtBoaInOrg();
    					newOrgCode = "D01";
    				}else {
    					continue;
    				}
    				
    			}
    			HtBoaInOrg newOrg = null;
				List<HtBoaInOrg> listHtBoaInOrg = htBoaInOrgService.findByOrgCode(newOrgCode);
				if(listHtBoaInOrg!=null&&!listHtBoaInOrg.isEmpty()) {
					newOrg = listHtBoaInOrg.get(0);
				}else {
					newOrg = new HtBoaInOrg();
				}
				newOrg.setOrgCode(newOrgCode);
				newOrg.setParentOrgCode(parentOrg.getOrgCode());
		        newOrg.setRootOrgCode("D01");
                newOrg.setOrgPath(parentOrg.getOrgPath()+"/"+newOrgCode);
                newOrg.setOrgNameCn(ddDeptOperator.getDeptName());
                newOrg.setDataSource(3);
                newOrg.setSequence(0);
                if(newOrgCode.length()>2&&!"D01".equals(newOrgCode)) {
                	newOrg.setSequence(Integer.parseInt(newOrgCode.substring(newOrgCode.length()-2, newOrgCode.length())));
                }
                newOrg.setRemark(ddDeptOperator.getDeptId());//把钉钉组织机构id存入该字段
                htBoaInOrgService.add(newOrg);
                
                //添加机构与钉钉的关联
                HtBoaInContrast htBoaInContrast = htBoaInContrastService.getHtBoaInContrastListByUcBusinessId(newOrgCode, "10");
                if(htBoaInContrast==null) {
                	htBoaInContrast = new HtBoaInContrast();
                	htBoaInContrast.setType("10");
                    htBoaInContrast.setUcBusinessId(newOrgCode);
                    htBoaInContrast.setDdBusinessId(ddDeptOperator.getDeptId());
                    htBoaInContrast.setContrast("自动对照");
                    htBoaInContrast.setContrastDatetime(new Date());
                }else {
                	htBoaInContrast.setUcBusinessId(newOrgCode);
                    htBoaInContrast.setDdBusinessId(ddDeptOperator.getDeptId());
                    htBoaInContrast.setContrastDatetime(new Date());
                }
                
                htBoaInContrastService.add(htBoaInContrast);
                //更新同步完成标记
                ddDeptOperator.setSynFlag("1");
                synDeptList.add(ddDeptOperator);
    		}
    	}
    	
        ////标记删除机构
    	u.setOperatorType(2);
    	List<DdDeptOperator> delDeptList =  dingDingService.getDdDeptOperatorOrderByLevel(u);
    	List<HtBoaInOrg> delOrgList = new ArrayList<HtBoaInOrg>();
    	if(delDeptList!=null&&!delDeptList.isEmpty()) {
    		for(int i = 0;i<delDeptList.size();i++) {
    			DdDeptOperator ddDeptOperator = delDeptList.get(i);
    			//List<HtBoaInContrast> listContrast = htBoaInContrastService.getHtBoaInContrastListByDdBusinessId(ddDeptOperator.getDeptId(), "10");
    			if(ddDeptOperator!=null) {
    				List<HtBoaInOrg> listHtBoaInOrg = htBoaInOrgService.findByOrgCode(ddDeptOperator.getDeptId());
    				if(listHtBoaInOrg!=null&&!listHtBoaInOrg.isEmpty()) {
    					HtBoaInOrg delOrg = listHtBoaInOrg.get(0);
    					delOrg.setId(listHtBoaInOrg.get(0).getId());
    					delOrg.setDelFlag(Constants.DEL_1);
    					delOrg.setLastModifiedDatetime(new Date());
    					delOrgList.add(delOrg);
    				}
    				 //更新同步完成标记
                    ddDeptOperator.setSynFlag("1");
                    synDeptList.add(ddDeptOperator);
    			}
    		}
    		if(delOrgList!=null&&!delOrgList.isEmpty()) {
    			htBoaInOrgService.add(delOrgList);
    		}
    		if(synDeptList!=null&&!synDeptList.isEmpty()) {
    			dingDingService.updateDeptOperator(synDeptList);
    		}
    	}
    	
        ////机构信息更新
    	u.setOperatorType(3);
    	List<DdDeptOperator> updateDeptList =  dingDingService.getDdDeptOperatorOrderByLevel(u);
    	List<HtBoaInOrg> updateOrgList = new ArrayList<HtBoaInOrg>();
    	if(updateDeptList!=null&&!updateDeptList.isEmpty()) {
    		for(int i = 0;i<updateDeptList.size();i++) {
    			DdDeptOperator ddDeptOperator = updateDeptList.get(i);
    			List<HtBoaInContrast> listContrast = htBoaInContrastService.getHtBoaInContrastListByDdBusinessId(ddDeptOperator.getDeptId(), "10");
    			if(listContrast!=null&&!listContrast.isEmpty()) {
    				List<HtBoaInOrg> listHtBoaInOrg = htBoaInOrgService.findByOrgCode(listContrast.get(0).getUcBusinessId());
    				if(listHtBoaInOrg!=null&&!listHtBoaInOrg.isEmpty()) {
    					HtBoaInOrg updateOrg = listHtBoaInOrg.get(0);
    					updateOrg.setId(listHtBoaInOrg.get(0).getId());
    					updateOrg.setOrgName(ddDeptOperator.getDeptName());
    					updateOrg.setOrgNameCn(ddDeptOperator.getDeptName());
    					updateOrg.setLastModifiedDatetime(new Date());
    					updateOrgList.add(updateOrg);
    				}
    				 //更新同步完成标记
                    ddDeptOperator.setSynFlag("1");
                    synDeptList.add(ddDeptOperator);
                    
    			}
    		}
    		if(delOrgList!=null&&!delOrgList.isEmpty()) {
    			htBoaInOrgService.add(delOrgList);
    		}
    		if(synDeptList!=null&&!synDeptList.isEmpty()) {
    			dingDingService.updateDeptOperator(synDeptList);
    		}
    	}
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
    	//1.将钉钉用户添加到UC  将钉钉不存在的用户进行，修改为离职状态
    	List<HtBoaInOrg> htBoaInOrgList = htBoaInOrgService.findAll();
    	List<HtBoaInUser> htBoaInUserList= htBoaInUserService.findAll();//所有UC用户
    	
    	List<HtBoaInContrast> htBoaInContrastListORG=htBoaInContrastService.getHtBoaInContrastListByType("10");//获取所有关联的机构
    	List<HtBoaInContrast> htBoaInContrastListUser=htBoaInContrastService.getHtBoaInContrastListByType("20");//获取所有关联的用户
    	
    	List<HtBoaInUser> listHtBoaInUser = new ArrayList<HtBoaInUser>(); //待处理用户
    	List<HtBoaInLogin> loginList = new ArrayList<>();
    	List<HtBoaInContrast> listHtBoaInContrasts = new ArrayList<>();
    	
    	DdUserOperator ddUserOperator = new DdUserOperator();
    	ddUserOperator.setOperatorType(1);
    	List<DdUserOperator> addDeptList =  dingDingService.getDdUserOperator(ddUserOperator);
    	List<DdUserOperator> synDeptList =  new ArrayList<DdUserOperator>();
    	
    	List<String> userIdList = new ArrayList<>();
    	List<String> mobileList = new ArrayList<>();//相同的人
    	for (DdUserOperator ddAddUser : addDeptList) {
    		if(StringUtils.isEmpty(ddAddUser.getEmail())) {
    			ddAddUser.setEmail(null);
			}
    		//查看是否存在用户，如果存在用户则不用新增用户
    		HtBoaInUser htBoaInUser = null; 
	       	 if(htBoaInUser==null) {
       			 if(StringUtils.isNotEmpty(ddAddUser.getEmail())) {
       				//htBoaInUser = htBoaInUserList.stream().filter(ucUser ->ddAddUser.getEmail().equals(ucUser.getEmail())).findFirst().get();  //htBoaInUserService.findByEmail(ddAddUser.getEmail());
       				Optional<HtBoaInUser> htBoaInUserOptional = htBoaInUserList.stream().filter(ucUser ->ddAddUser.getEmail().equals(ucUser.getEmail())).findFirst();
       				if(htBoaInUserOptional!=null &&htBoaInUserOptional.isPresent()) {
       					try {
       						htBoaInUser = htBoaInUserOptional.get(); 
						} catch (Exception e) {
							e.printStackTrace();
						}
       					
       				}
       			 }
	       	 }
	       	 if(htBoaInUser==null) {
	       		 if(StringUtils.isNotEmpty(ddAddUser.getMobile())) {
	       			//htBoaInUser = htBoaInUserList.stream().filter(ucUser ->ddAddUser.getMobile().equals(ucUser.getMobile())).findFirst().get();  
	       			Optional<HtBoaInUser> htBoaInUserOptional = htBoaInUserList.stream().filter(ucUser ->ddAddUser.getMobile().equals(ucUser.getMobile())).findFirst();
	       			if(htBoaInUserOptional!=null &&htBoaInUserOptional.isPresent()) {
       					try {
       						htBoaInUser = htBoaInUserOptional.get(); 
						} catch (Exception e) {
							e.printStackTrace();
						}
       					
       				}
	       		  // htBoaInUser = htBoaInUserService.findByMobile(ddAddUser.getMobile());
	       		 }
	       	 }
	       	 if(htBoaInUser!=null) {
	       		if(StringUtils.isNotEmpty(ddAddUser.getEmail())) {
	       			htBoaInUser.setEmail(ddAddUser.getEmail());
    			}
    			if(StringUtils.isNotEmpty(ddAddUser.getMobile())) {
    				htBoaInUser.setMobile(ddAddUser.getMobile());
    			}
    			if(StringUtils.isNotEmpty(ddAddUser.getDeptId())) {
    				HtBoaInContrast htBoaInContrastOrg = htBoaInContrastListORG.stream().filter(org -> org.getDdBusinessId().equals(ddAddUser.getDeptId())).findFirst().get();
    				HtBoaInOrg o = htBoaInOrgList.stream().filter(org -> org.getOrgCode().equals(htBoaInContrastOrg.getUcBusinessId())).findFirst().get();
    				if(StringUtils.isNotEmpty(o.getOrgCode())) {
    					htBoaInUser.setOrgCode(o.getOrgCode());
    				}
    			}
    			if(StringUtils.isNotEmpty(ddAddUser.getUserName())) {
    				htBoaInUser.setUserName(ddAddUser.getUserName());
    			}
    			if(StringUtils.isNotEmpty(ddAddUser.getJobNumber())) {
    				htBoaInUser.setJobNumber(ddAddUser.getJobNumber());
    			}
    			HtBoaInUser htBoaInUser2 = htBoaInUser; 
    			HtBoaInContrast htBoaInContrastUser = htBoaInContrastListUser.stream().filter(user ->user.getUcBusinessId().equals(htBoaInUser2.getUserId())).findFirst().get();
    			htBoaInContrastUser.setDdBusinessId(ddAddUser.getUserId());
    			listHtBoaInContrasts.add(htBoaInContrastUser);
    			htBoaInUser.setStatus(Constants.USER_STATUS_0);
    			htBoaInUser.setDelFlag(Constants.DEL_0);
    			listHtBoaInUser.add(htBoaInUser);
    			userIdList.add(htBoaInUser.getUserId());
    			ddAddUser.setSynFlag("1");
                synDeptList.add(ddAddUser);
	       		 continue;
	       	 }
	       	 if(StringUtils.isNotEmpty(ddAddUser.getMobile())) {
	       		 if(mobileList.contains(ddAddUser.getMobile())) {
	       			continue;
	       		 }
	       		mobileList.add(ddAddUser.getMobile());
	       	 }
	       	HtBoaInUser u = new HtBoaInUser();
    		HtBoaInContrast htBoaInContrastOrg = htBoaInContrastListORG.stream().filter(org -> org.getDdBusinessId().equals(ddAddUser.getDeptId())).findFirst().get();
    		HtBoaInOrg o = htBoaInOrgList.stream().filter(org -> org.getOrgCode().equals(htBoaInContrastOrg.getUcBusinessId())).findFirst().get();
    		int isOrgUser = ddAddUser.getJobNumber()==null?0:1;
            String userId = generatorUserId(userIdList,o.getRootOrgCode(),  isOrgUser,2, ddAddUser.getJobNumber());
            u.setUserId(userId);
            HtBoaInUser u1 = null;// htBoaInUserService.findByUserId(userId);
            Optional<HtBoaInUser> htBoaInUserOptional = htBoaInUserList.stream().filter(ucUser ->u.getUserId().equals(ucUser.getUserId())).findFirst();
			if (htBoaInUserOptional != null && htBoaInUserOptional.isPresent()) {
				try {
					u1 = htBoaInUserOptional.get();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
            if(u1!=null) {
            	userId = generatorUserId(userIdList,o.getRootOrgCode(),  isOrgUser,3, ddAddUser.getJobNumber());
            }
            userIdList.add(userId);
            u.setUserId(userId);
            u.setUserName(ddAddUser.getUserName());
            u.setOrgCode(o.getOrgCode());
            u.setRootOrgCode(o.getRootOrgCode());
            u.setOrgPath(o.getOrgPath());
            u.setEmail(ddAddUser.getEmail());
            u.setMobile(ddAddUser.getMobile());
            u.setJobNumber(ddAddUser.getJobNumber());
            u.setIsOrgUser(isOrgUser);
            u.setDelFlag(Constants.DEL_0);
            u.setCreateOperator("自动同步");
            u.setUpdateOperator("自动同步");
            u.setDataSource(Constants.USER_DATASOURCE_2);
            u.setLastModifiedDatetime(new Date());
            u.setStatus("0");
            listHtBoaInUser.add(u);
            
            HtBoaInLogin l = new HtBoaInLogin();
            l.setLoginId(ddAddUser.getUserId());
            l.setUserId(userId);
            l.setRootOrgCode(u.getRootOrgCode());
            l.setCreateOperator("自动同步");
            l.setUpdateOperator("自动同步");
            l.setStatus(Constants.USER_STATUS_0);
            l.setFailedCount(0);
            l.setDelFlag(0);
            l.setLastModifiedDatetime(new Date());
            loginList.add(l);
            
            //添加用户与钉钉的关联
            HtBoaInContrast htBoaInContrast = new HtBoaInContrast();
            htBoaInContrast.setType("20");
            htBoaInContrast.setUcBusinessId(userId);
            htBoaInContrast.setDdBusinessId(ddAddUser.getUserId());
            htBoaInContrast.setContrast("自动对照");
            htBoaInContrast.setContrastDatetime(new Date());
            listHtBoaInContrasts.add(htBoaInContrast);
            
            ddAddUser.setSynFlag("1");
            synDeptList.add(ddAddUser);
            
    	}
    	
    	//删除用户
    	ddUserOperator.setOperatorType(2);
    	List<DdUserOperator> delUserList =  dingDingService.getDdUserOperator(ddUserOperator);
    	for (DdUserOperator dddelUser : delUserList) {
    		//HtBoaInContrast htBoaInContrastUser = htBoaInContrastListUser.stream().filter(user -> user.getUcBusinessId().equals(dddelUser.getUserId())).findFirst().get();
    		HtBoaInUser u = null;//htBoaInUserService.findByUserId(dddelUser.getUserId());
			Optional<HtBoaInUser> htBoaInUserOptional = htBoaInUserList.stream().filter(ucUser -> ucUser.getUserId().equals(dddelUser.getUserId())).findFirst();
			if (htBoaInUserOptional != null && htBoaInUserOptional.isPresent()) {
				try {
					u = htBoaInUserOptional.get();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			if (u != null) {
				//u.setDelFlag(Constants.DEL_1);
				u.setStatus(Constants.USER_STATUS_2);
				listHtBoaInUser.add(u);
				dddelUser.setSynFlag("1");
				synDeptList.add(dddelUser);

			}
    	}
    	
    	//更新用户信息
    	ddUserOperator.setOperatorType(3);
    	List<DdUserOperator> updateUserList =  dingDingService.getDdUserOperator(ddUserOperator);
    	for (DdUserOperator ddUpdateUser : updateUserList) {
			HtBoaInContrast htBoaInContrastUser = htBoaInContrastListUser.stream().filter(user -> ddUpdateUser.getUserId().equals(user.getDdBusinessId())).findFirst().get();
    		//HtBoaInUser u = htBoaInUserService.findByUserId(htBoaInContrastUser.getUcBusinessId());
    		HtBoaInUser u = null; 
            Optional<HtBoaInUser> htBoaInUserOptional = htBoaInUserList.stream().filter(ucUser ->ucUser.getUserId().equals(htBoaInContrastUser.getUcBusinessId())).findFirst();
			if (htBoaInUserOptional != null && htBoaInUserOptional.isPresent()) {
				try {
					u = htBoaInUserOptional.get();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
    		if(u!=null) {
    			if(StringUtils.isNotEmpty(ddUpdateUser.getEmail())) {
    				u.setEmail(ddUpdateUser.getEmail());
    			}
    			if(StringUtils.isNotEmpty(ddUpdateUser.getMobile())) {
    				u.setMobile(ddUpdateUser.getMobile());
    			}
    			if(StringUtils.isNotEmpty(ddUpdateUser.getDeptId())) {
    				HtBoaInContrast htBoaInContrastOrg = htBoaInContrastListORG.stream().filter(org -> org.getDdBusinessId().equals(ddUpdateUser.getDeptId())).findFirst().get();
    				HtBoaInOrg o = htBoaInOrgList.stream().filter(org -> org.getOrgCode().equals(htBoaInContrastOrg.getUcBusinessId())).findFirst().get();
    				if(StringUtils.isNotEmpty(o.getOrgCode())) {
    					u.setOrgCode(o.getOrgCode());
    				}
    			}
    			if(StringUtils.isNotEmpty(ddUpdateUser.getUserName())) {
    				u.setUserName(ddUpdateUser.getUserName());
    			}
    			if(StringUtils.isNotEmpty(ddUpdateUser.getJobNumber())) {
    				u.setJobNumber(ddUpdateUser.getJobNumber());
    			}
    			listHtBoaInUser.add(u);
    			ddUpdateUser.setSynFlag("1");
                synDeptList.add(ddUpdateUser);
    		}
    	}
    	if(listHtBoaInUser!=null&&!listHtBoaInUser.isEmpty()) {
    		htBoaInUserService.add(listHtBoaInUser);
		}
    	if(loginList!=null && !loginList.isEmpty()) {
    		htBoaInLoginService.add(loginList);
    	}
    	if(listHtBoaInContrasts!=null && !listHtBoaInContrasts.isEmpty()) {
    		htBoaInContrastService.add(listHtBoaInContrasts);
    	}
		if(synDeptList!=null&&!synDeptList.isEmpty()) {
			dingDingService.updateUserOperator(synDeptList);
		}
		log.debug("用户同步转换完成！");
    	return Result.buildSuccess();
    }
    
    
	private String getDDOrgPath(DdDept ddDept) {
		try {
			if("58800327".equals(ddDept.getParentId())) {
				return  ddDept.getDeptName();
			} else {
				DdDept ddDeptP = dingDingService.findByDeptId(ddDept.getParentId());
				return getDDOrgPath(ddDeptP)+"/"+ddDept.getDeptName();
			}
		} catch (Exception e) {
			 System.out.println(ddDept);
			 System.out.println(ddDept.getDeptId()+" lrc "+ddDept.getParentId());
		}
		return "";
	}

    /**
     * 生成用户编码<br>
     * 编码规则：<br>
     * 有工号：根机构编码+1+数据来源+是否是机构用户+工号的数字部分（不包含“HX-”）<br>
     * 无工号：根机构编码+0+数据来源+是否是机构用户+5位纯数字随机数 <br>
     *
     * @param userIds     用户编码集，用于判断用户编码是否重复
     * @param rootOrgCode 根机构编码
     * @param isOrgUser   是否是机构用户
     * @param dataSource  数据来源
     * @param jobNumber   工号
     * @return 用户编码
     * @author 谭荣巧
     * @Date 2018/3/5 0:57
     */
    private String generatorUserId(List<String> userIdList,String rootOrgCode, Integer isOrgUser, Integer dataSource, String jobNumber) {
        String userId;
        rootOrgCode = StringUtils.isEmpty(rootOrgCode)?"D01":rootOrgCode;
        String oc = rootOrgCode.replace("D", "");
        if (jobNumber != null && jobNumber.contains("HX-")) {
            userId = String.format("%s%s%s%s%s", oc, 1, dataSource, isOrgUser, jobNumber.replace("HX-", ""));
            if (userIdList.contains(userId)) {
                userId = String.format("%s%s%s%s%s", oc, 1, dataSource, isOrgUser, generateNumber(6));
            }
        } else {
            userId = String.format("%s%s%s%s%s", oc, 0, dataSource, isOrgUser, generateNumber(5));
            if (userIdList.contains(userId)) {
                userId = String.format("%s%s%s%s%s", oc, 1, dataSource, isOrgUser, generateNumber(6));
            }
        }
        return userId;
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
    
    private String generateNumber(int length) {
        String no = "";
        //初始化备选数组
        int[] defaultNums = new int[10];
        for (int i = 0; i < defaultNums.length; i++) {
            defaultNums[i] = i;
        }

        Random random = new Random();
        int[] nums = new int[length];
        //默认数组中可以选择的部分长度
        int canBeUsed = defaultNums.length;
        //填充目标数组
        for (int i = 0; i < nums.length; i++) {
            //将随机选取的数字存入目标数组
            int index = random.nextInt(canBeUsed);
            nums[i] = defaultNums[index];
            canBeUsed--;
        }
        if (nums.length > 0) {
            for (int i = 0; i < nums.length; i++) {
                no += nums[i];
            }
        }
        return no;
    }

}
