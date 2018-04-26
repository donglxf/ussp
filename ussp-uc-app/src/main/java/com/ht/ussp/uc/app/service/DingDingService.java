package com.ht.ussp.uc.app.service;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.minlog.Log;
import com.ht.ussp.common.Constants;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.DdDept;
import com.ht.ussp.uc.app.domain.DdDeptOperator;
import com.ht.ussp.uc.app.domain.DdUser;
import com.ht.ussp.uc.app.domain.DdUserOperator;
import com.ht.ussp.uc.app.domain.HtBoaInContrast;
import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.domain.HtBoaInPosition;
import com.ht.ussp.uc.app.domain.HtBoaInPositionUser;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.feignclients.EipClient;
import com.ht.ussp.uc.app.repository.DdDeptOperatorRepository;
import com.ht.ussp.uc.app.repository.DdDeptRepository;
import com.ht.ussp.uc.app.repository.DdDeptUserRepository;
import com.ht.ussp.uc.app.repository.DdUserOperatorRepository;
import com.ht.ussp.uc.app.resource.EchoResouce;
import com.ht.ussp.uc.app.vo.eip.DDGetTokenReqDto;
import com.ht.ussp.util.EncryptUtil;
import com.ht.ussp.util.HttpClientUtil;

@Service
public class DingDingService {
    private static final Logger log = LoggerFactory.getLogger(DingDingService.class);

	@Autowired
	private DdDeptRepository ddDeptRepository;

	@Autowired
	private DdDeptUserRepository ddDeptUserRepository;

	@Autowired
	private DdDeptOperatorRepository ddDeptOperatorRepository;

	@Autowired
	private DdUserOperatorRepository ddUserOperatorRepository;

	@Autowired
	private EipClient eipClient;

	@Autowired
	private HtBoaInOrgService htBoaInOrgService;
	
	@Autowired
	private HtBoaInUserService htBoaInUserService;
	
	@Autowired
	private HtBoaInLoginService htBoaInLoginService;
	
	@Autowired
    private HtBoaInContrastService htBoaInContrastService;
	
	@Autowired
	private HtBoaInPositionService htBoaInPositionService;
	
	@Autowired
	private HtBoaInPositionUserService htBoaInPositionUserService;

	/**
	 * 钉钉与生产数据对照
	 */
	public List<DdUser> getDDUserList() {
		return ddDeptUserRepository.findAll();
	}
	
	public List<DdDept> getDdDeptList() {
		return ddDeptRepository.findAll();
	}
	
	public List<DdDept> findDDByParentId(String parentId) {
		return ddDeptRepository.findByParentId(parentId);
	}
	
	public DdDept findByDDParentOrgCode(String parentId) {
		return ddDeptRepository.findByDeptId(parentId);
	}
	@Transactional
	public Result getDD() {
		String getTokenUrl="https://oapi.dingtalk.com/gettoken?corpid=dingb9594db0ecde853a35c2f4657eb6378f&corpsecret=g445dW-0hpWTXkgJGDjty01q_xulKO9EDlX_XV4AOeZaciJDsEBW14xzXNdFPxF1";
		String getAuthUrl ="https://oapi.dingtalk.com/auth/scopes";
		String getDeptList = "https://oapi.dingtalk.com/department/list";
	    String getDeptUser = "https://oapi.dingtalk.com/user/list";
	    try {
	    	DDGetTokenReqDto ddGetTokenReqDto = new DDGetTokenReqDto();
	    	ddGetTokenReqDto.setCorpid("dingb9594db0ecde853a35c2f4657eb6378f");
	    	ddGetTokenReqDto.setCorpsecret("g445dW-0hpWTXkgJGDjty01q_xulKO9EDlX_XV4AOeZaciJDsEBW14xzXNdFPxF1");;
	    	//eipClient.getDDToken(ddGetTokenReqDto);
			//1.获取token
			String token = (String) JSONObject.parseObject(HttpClientUtil.getInstance().doGet(getTokenUrl, null)).get("access_token");
			log.debug("token=====>"+token);
			//2.获取授权部门
			String authDeptResBody = HttpClientUtil.getInstance().doGet(getAuthUrl+"?access_token="+token, null);
			log.debug("authDeptResBody=====>"+authDeptResBody);
			JSONObject jsonAuthDept = JSONObject.parseObject(authDeptResBody);
			log.debug("jsonAuthDept=====>"+jsonAuthDept);
	        JSONArray jsonAuthDepts = jsonAuthDept.getJSONObject("auth_org_scopes").getJSONArray("authed_dept");
	        log.debug("jsonAuthDepts=====>"+jsonAuthDepts);
	        String authDept = jsonAuthDepts.getString(0);
			//3.获取部门列表
			String listDeptResBody = HttpClientUtil.getInstance().doGet(getDeptList+"?access_token="+token+"&id="+authDept, null);
			JSONObject jsonDeptRes = JSONObject.parseObject(listDeptResBody);
			log.debug("jsonDeptRes=====>"+jsonDeptRes);
			JSONArray jsonDepts = jsonDeptRes.getJSONArray("department");
			List<DdDept> listDdDept = new ArrayList<DdDept>();
			DdDept ddDeptTop = new DdDept();
			ddDeptTop.setDeptId(authDept);
			ddDeptTop.setDeptName("鸿特信息");
			ddDeptTop.setCreatDatetime(new Date());
			ddDeptTop.setLevel(1);
			listDdDept.add(ddDeptTop);
			List<DdUser> listDdDeptUser = new ArrayList<DdUser>();
			if(jsonDepts!=null) {
				for(int i = 0;i<jsonDepts.size();i++) {
					JSONObject depts = (JSONObject) jsonDepts.get(i);
					DdDept ddDept = new DdDept();
					ddDept.setDeptId(depts.getString("id"));
					ddDept.setDeptName(new String(depts.getString("name").getBytes(),"utf-8"));
					ddDept.setParentId(depts.getString("parentid"));
					ddDept.setCreatDatetime(new Date());
					ddDept.setOrders(depts.getString("order"));
					listDdDept.add(ddDept);
					// ddDeptRepository.saveAndFlush(ddDept);
					//4.获取部门成员列表  ---需要循环调用添加
		    		String listDeptUserResBody = HttpClientUtil.getInstance().doGet(getDeptUser+"?access_token="+token+"&department_id="+depts.get("id"), null);
		    		JSONObject jsonUserRes = JSONObject.parseObject(listDeptUserResBody);
		    		JSONArray jsonDeptUsers = jsonUserRes.getJSONArray("userlist");
		    		if(jsonDeptUsers!=null) {
		    			for(int j=0;j<jsonDeptUsers.size();j++) {
			    			JSONObject deptUsers = (JSONObject) jsonDeptUsers.get(j);
			    			DdUser ddUser = new DdUser();
			    			ddUser.setUserId(deptUsers.getString("userid"));
			    			ddUser.setUserName(new String(deptUsers.getString("name").getBytes(),"utf-8"));
			    			ddUser.setDeptId(depts.get("id")+"");
			    			ddUser.setEmail(deptUsers.getString("email"));
			    			ddUser.setMobile(deptUsers.getString("mobile"));
			    			ddUser.setJobNumber(deptUsers.getString("jobnumber"));
			    			ddUser.setPosition(deptUsers.getString("position"));
			    			ddUser.setCreatDatetime(new Date());
			    			listDdDeptUser.add(ddUser); 
			    			//ddDeptUserRepository.saveAndFlush(ddUser);
			    		}
		    		}
				}
				ddDeptRepository.deleteAll();
				ddDeptRepository.save(listDdDept);
				ddDeptUserRepository.deleteAll();
				ddDeptUserRepository.save(listDdDeptUser);
			}
			log.debug("下载完成！");
			return Result.buildSuccess();
		} catch (Exception e) {
			log.debug("下载错误！==> "+e.getMessage());
			e.printStackTrace();
			return Result.buildFail("9999",e.getMessage());
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public Result dealErrorData() { 
		List<HtBoaInUser> htBoaInUserList= htBoaInUserService.findAll();//所有UC用户
		List<DdUser> listDdUser = getDDUserList(); //获取钉钉所有用户数据
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
		log.debug("矫正历史错误数据完成！");
        return Result.buildSuccess();
	}
	
	@SuppressWarnings("rawtypes")
	public Result dealData() {
		try {
			//1.获取钉钉用户 、机构保存到中间表
			List<DdDept> updateDDOrgList = new ArrayList<DdDept>(); //保存需要操作的钉钉机构
			List<DdDept>  ddOrgList = new ArrayList<DdDept>(); //保存需要添加的机构级别
			ddOrgList = getDdDeptList(); // 钉钉机构列表
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
	    		ddOrgList = saveOrUpdateDDDpet(updateDDOrgList); //保存需要添加的机构级别

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
	        		if(delOrg.getDelFlag()==0 && "2".equals(delOrg.getDataSource()+"")) { //已经删除的不在处理
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
	        	saveDeptOperator(ddDeptOperatorList);
	    	}
			///////////////用户数据整理///////////////
			List<DdUser> listDdUser = getDDUserList(); //获取钉钉所有用户数据
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
	    	
	    	for(DdUser ddUser : listDdUser) {//得到钉钉删除的用户
	    		List<HtBoaInContrast> sameUserList  = listHtBoaInContrastUser.stream().filter(ucUser -> ddUser.getUserId().equals(ucUser.getDdBusinessId())).collect(Collectors.toList());
	    		ddDelUserList.removeAll(sameUserList);
	    	}
	    	
	    	for(HtBoaInContrast delHtBoaInContrast : ddDelUserList) {
	    		HtBoaInUser delUser =  htBoaInUserList.stream().filter(user -> delHtBoaInContrast.getUcBusinessId().equals(user.getUserId())).findFirst().get();
	    		if(delUser.getDelFlag()==0 && "0".equals(delUser.getStatus())&& "2".equals(delUser.getDataSource()+"")) { //已经删除(离职)的不在处理
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
	    	saveUserOperator(ddUserOperatorList);
	    	log.debug("数据整理完成！");
	    	return Result.buildSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.buildFail("9999", e.getMessage());
		}
	}
	
	@SuppressWarnings("rawtypes")
	public Result convertOrg() {
		try {
	    	//对钉钉用户数据进行转换  a.将钉钉机构转换成UC机构 b.将新的机构赋值给用户
	    	//a。将钉钉机构转换成UC机构  如果UC已经存在的机构 则不再重新生成UC的orgCode
	    	DdDeptOperator u = new DdDeptOperator();
	    	u.setOperatorType(1);
	    	List<DdDeptOperator> addDeptList =  getDdDeptOperatorOrderByLevel(u);
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
					if("D01".equals(parentOrg.getOrgCode())) {
						newOrg.setOrgType("20");//中心
					}
			        newOrg.setRootOrgCode("D01");
	                newOrg.setOrgPath(parentOrg.getOrgPath()+"/"+newOrgCode);
	                newOrg.setOrgNameCn(ddDeptOperator.getDeptName());
	                newOrg.setDataSource(Constants.USER_DATASOURCE_2);
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
	    	List<DdDeptOperator> delDeptList =  getDdDeptOperatorOrderByLevel(u);
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
	    			updateDeptOperator(synDeptList);
	    		}
	    	}
	    	
	        ////机构信息更新
	    	u.setOperatorType(3);
	    	List<DdDeptOperator> updateDeptList =  getDdDeptOperatorOrderByLevel(u);
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
	    					if("D01".equals(listHtBoaInOrg.get(0).getParentOrgCode())) {
	    						updateOrg.setOrgType("20");//中心
	    					}
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
	    			updateDeptOperator(synDeptList);
	    		}
	    	}
	    	log.debug("机构同步转换完成！");
	    	return Result.buildSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.buildFail("9999", e.getMessage());
		}
        
    }
	
	/**
	 * P+中心机构编码+1（数据来源 1：uc  2钉钉   3信贷）+序号
       1.先根据机构进行拼接岗位编码  （通过名称+机构code进行确认唯一）   机构到中心机构就可以了
	 * @return
	 */
	public Result  convertPosition() {
		try {
			List<DdUser> listDdUser = getDDUserList(); //获取钉钉所有用户数据
			List<HtBoaInContrast> htBoaInContrastListORG=htBoaInContrastService.getHtBoaInContrastListByType("10");//获取所有关联的机构
			List<HtBoaInOrg> htBoaInOrgList = htBoaInOrgService.findAll();
			
			StringBuffer sbf = new StringBuffer();
			for(DdUser ddUser : listDdUser) {
				sbf.setLength(0);
				String positionName = ddUser.getPosition();
				if(StringUtils.isEmpty(positionName)) {
					continue;
				}
				String parentOrgCode="";//所属中心
				if(StringUtils.isNotEmpty(ddUser.getDeptId())) {
					HtBoaInContrast htBoaInContrastOrg = htBoaInContrastListORG.stream().filter(org -> ddUser.getDeptId().equals(org.getDdBusinessId())).findFirst().get();
					HtBoaInOrg o = htBoaInOrgList.stream().filter(org -> org.getOrgCode().equals(htBoaInContrastOrg.getUcBusinessId())).findFirst().get();
					if(StringUtils.isNotEmpty(o.getOrgCode())) {
						sbf.append("P");
						if(o.getOrgCode().length()>5) {//获取中心机构编码
							parentOrgCode = o.getOrgCode().substring(0, 5);
						}else {
							parentOrgCode = o.getOrgCode();
						}
						sbf.append(parentOrgCode);
						sbf.append("2");
						HtBoaInPosition htBoaInPosition = null;
						List<HtBoaInPosition> listHtBoaInPosition2 = htBoaInPositionService.findByPositionNameCnAndParentOrgCode(positionName,parentOrgCode); //查看是否已经添加，已经添加则不在添加
						if(listHtBoaInPosition2==null||listHtBoaInPosition2.isEmpty()) {
							htBoaInPosition = new HtBoaInPosition();
							htBoaInPosition.setCreatedDatetime(new Date());
							htBoaInPosition.setCreateOperator("钉钉同步");
							htBoaInPosition.setDelFlag(Constants.DEL_0);
							htBoaInPosition.setLastModifiedDatetime(new Date());
							Integer count = htBoaInPositionService.getMaxPositionCode(parentOrgCode);
							htBoaInPosition.setPositionCode(String.format("%s%03d", sbf.toString(), count));
							htBoaInPosition.setSequence(count);
							htBoaInPosition.setPositionNameCn(positionName);
							htBoaInPosition.setStatus(Constants.STATUS_0);
							htBoaInPosition.setUpdateOperator("钉钉同步");
							htBoaInPosition.setParentOrgCode(parentOrgCode); //所属中心
							htBoaInPositionService.add(htBoaInPosition);
						} 
					}
				}
			}
			log.debug("岗位同步转换完成！");
			return Result.buildSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.buildFail("9999", e.getMessage());
		}
	}
    /**
     * 用户转换
     * @param page
     * @return
     */
    public Result  convertUser() {
    	try {
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
        	List<DdUserOperator> addDeptList =  getDdUserOperator(ddUserOperator);
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
           			 if(StringUtils.isNotEmpty(ddAddUser.getJobNumber())) {
           				//htBoaInUser = htBoaInUserList.stream().filter(ucUser ->ddAddUser.getEmail().equals(ucUser.getEmail())).findFirst().get();  //htBoaInUserService.findByEmail(ddAddUser.getEmail());
           				Optional<HtBoaInUser> htBoaInUserOptional = htBoaInUserList.stream().filter(ucUser ->ddAddUser.getJobNumber().equals(ucUser.getJobNumber())).findFirst();
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
        				HtBoaInContrast htBoaInContrastOrg = htBoaInContrastListORG.stream().filter(org -> ddAddUser.getDeptId().equals(org.getDdBusinessId())).findFirst().get();
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
                l.setStatus(Constants.USER_STATUS_1);
                l.setPassword(EncryptUtil.passwordEncrypt("123456"));
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
        	List<DdUserOperator> delUserList =  getDdUserOperator(ddUserOperator);
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
        	List<DdUserOperator> updateUserList =  getDdUserOperator(ddUserOperator);
        	for (DdUserOperator ddUpdateUser : updateUserList) {
        		HtBoaInUser u = null; 
        		try {
        			HtBoaInContrast htBoaInContrastUser = htBoaInContrastListUser.stream().filter(user -> ddUpdateUser.getUserId().equals(user.getDdBusinessId())).findFirst().get();
        			Optional<HtBoaInUser> htBoaInUserOptional = htBoaInUserList.stream().filter(ucUser ->ucUser.getUserId().equals(htBoaInContrastUser.getUcBusinessId())).findFirst();
        			if (htBoaInUserOptional != null && htBoaInUserOptional.isPresent()) {
        				try {
        					u = htBoaInUserOptional.get();
        				} catch (Exception e) {
        					e.printStackTrace();
        				}
        			}
    			} catch (Exception e) {
    				e.printStackTrace();
    				continue;
    			}
        		//HtBoaInUser u = htBoaInUserService.findByUserId(htBoaInContrastUser.getUcBusinessId());
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
    			updateUserOperator(synDeptList);
    		}
    		log.debug("用户同步转换完成！");
        	return Result.buildSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.buildFail("9999", e.getMessage());
		}
    }
    
    public Result convertUserPosition() {
    	try {
        	List<HtBoaInPosition> listHtBoaInPosition = htBoaInPositionService.findAll();
        	List<DdUser> listDdUser = getDDUserList(); //获取钉钉所有用户数据
        	List<HtBoaInContrast> htBoaInContrastListUser=htBoaInContrastService.getHtBoaInContrastListByType("20");//获取所有关联的用户
        	List<HtBoaInPositionUser> listHtBoaInPositionUsers = htBoaInPositionUserService.findAll(); //已经存在的关联关系
        	List<HtBoaInPositionUser> listHtBoaInPositionUser = new ArrayList<HtBoaInPositionUser>();
        	for(DdUser ddUser : listDdUser) {
        		HtBoaInContrast htBoaInContrast = null; 
        		try {
        			Optional<HtBoaInContrast> optionalHtBoaInContrastUser = htBoaInContrastListUser.stream().filter(user -> ddUser.getUserId().equals(user.getDdBusinessId())).findFirst();
        			if (optionalHtBoaInContrastUser != null && optionalHtBoaInContrastUser.isPresent()) {
        				htBoaInContrast = optionalHtBoaInContrastUser.get();
        			}
        			if(htBoaInContrast!=null) {
        				if(!StringUtils.isEmpty(htBoaInContrast.getUcBusinessId())) {
            				if(StringUtils.isEmpty(htBoaInContrast.getUcBusinessId())) {
            					continue;
            				}
            				HtBoaInUser  htBoaInUser = htBoaInUserService.findByUserId(htBoaInContrast.getUcBusinessId());
            				if(htBoaInUser==null) {
            					continue;
            				}
            				String parentOrgCode="";
            				if(htBoaInUser.getOrgCode().length()>5) {//获取中心机构编码
        						parentOrgCode = htBoaInUser.getOrgCode().substring(0, 5);
        					}else {
        						parentOrgCode = htBoaInUser.getOrgCode();
        					}
        					HtBoaInPositionUser htBoaInPositionUser = new HtBoaInPositionUser();
             				htBoaInPositionUser.setUpdateOperator("钉钉同步");
             				htBoaInPositionUser.setDelFlag(Constants.DEL_0);
             				htBoaInPositionUser.setCreateOperator("钉钉同步");
             				htBoaInPositionUser.setCreatedDatetime(new Date());
             				htBoaInPositionUser.setLastModifiedDatetime(new Date());
             				htBoaInPositionUser.setUserId(htBoaInContrast.getUcBusinessId());
            				String orgCode_p = parentOrgCode;
            				HtBoaInPosition htBoaInPosition = null;
        					Optional<HtBoaInPosition> optionalHtBoaInPosition = listHtBoaInPosition.stream().filter(posi -> posi.getPositionNameCn().equals(ddUser.getPosition()) &&orgCode_p.equals(posi.getParentOrgCode()) ).findFirst();
        					if(optionalHtBoaInPosition!=null && optionalHtBoaInPosition.isPresent()) {
        						htBoaInPosition = optionalHtBoaInPosition.get();
        					}
        					if(htBoaInPosition!=null) {
        						HtBoaInPosition htBoaInPositionTemp = htBoaInPosition;
        						HtBoaInPositionUser htBoaInPositionUserTemp = null;
        						htBoaInPositionUser.setPositionCode(htBoaInPosition.getPositionCode());
        						//查看是否已经存在，存在则不需要再添加
        						Optional<HtBoaInPositionUser> optionalHtBoaInPositionUsers = listHtBoaInPositionUsers.stream().filter(pu -> htBoaInUser.getUserId().equals(pu.getUserId()) && htBoaInPositionTemp.getPositionCode().equals(pu.getPositionCode())).findFirst();
            					if(optionalHtBoaInPositionUsers!=null && optionalHtBoaInPositionUsers.isPresent()) {
            						htBoaInPositionUserTemp = optionalHtBoaInPositionUsers.get();
            					}
            					if(htBoaInPositionUserTemp==null) {
            						listHtBoaInPositionUser.add(htBoaInPositionUser);
            					}
        					}
        				}
        			}
    			} catch (Exception e) {
    				e.printStackTrace();
    				continue;
    			}
        	}
        	if(listHtBoaInPositionUser!=null&&!listHtBoaInPositionUser.isEmpty()) {
        		htBoaInPositionUserService.add(listHtBoaInPositionUser);
        	}
        	log.debug("用户岗位关联转换转换完成！");
        	return Result.buildSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.buildFail("9999", e.getMessage());
		}
	}
    
	private String getDDOrgPath(DdDept ddDept) {
		try {
			if("58800327".equals(ddDept.getParentId())) {
				return  ddDept.getDeptName();
			} else {
				DdDept ddDeptP = findByDeptId(ddDept.getParentId());
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
	
	public DdDept findByDeptId(String deptId) {
		return ddDeptRepository.findByDeptId(deptId);
	}
	
	public List<DdDept> saveOrUpdateDDDpet(List<DdDept> updateDDOrgList) {
		return ddDeptRepository.save(updateDDOrgList);
	}
	
	public List<DdDeptOperator>  saveDeptOperator(List<DdDeptOperator> ddDeptOperatorList) {
		ddDeptOperatorRepository.deleteAll();
		return ddDeptOperatorRepository.save(ddDeptOperatorList);
	}
	
	public List<DdUserOperator> saveUserOperator(List<DdUserOperator> ddUserOperatorList) {
		ddUserOperatorRepository.deleteAll();
		return ddUserOperatorRepository.save(ddUserOperatorList);
	}
	
	public List<DdDeptOperator>  updateDeptOperator(List<DdDeptOperator> ddDeptOperatorList) {
		return ddDeptOperatorRepository.save(ddDeptOperatorList);
	}
	
	public List<DdUserOperator>  updateUserOperator(List<DdUserOperator> ddUserOperatorList) {
		return ddUserOperatorRepository.save(ddUserOperatorList);
	}
	
	
	public DdDeptOperator saveDeptOperator(DdDeptOperator ddDeptOperator) {
		return ddDeptOperatorRepository.saveAndFlush(ddDeptOperator);
	}
	
	public List<DdDeptOperator> getDdDeptOperatorOrderByLevel(DdDeptOperator u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<DdDeptOperator> ex = Example.of(u, matcher);
		return ddDeptOperatorRepository.findAll(ex, new Sort(new Order("level")));
	}
	
	public List<DdUserOperator> getDdUserOperator(DdUserOperator u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<DdUserOperator> ex = Example.of(u, matcher);
		return ddUserOperatorRepository.findAll(ex);
	}
	
    public static void main(String[] args) {
    	String getTokenUrl="https://oapi.dingtalk.com/gettoken?corpid=dingb9594db0ecde853a35c2f4657eb6378f&corpsecret=g445dW-0hpWTXkgJGDjty01q_xulKO9EDlX_XV4AOeZaciJDsEBW14xzXNdFPxF1";
    	String getAuthUrl ="https://oapi.dingtalk.com/auth/scopes";
    	String getDeptList = "https://oapi.dingtalk.com/department/list";
    	String getDeptUser = "https://oapi.dingtalk.com/user/list";

    	try {
    		//1.获取token
    		String token = (String) JSONObject.parseObject(HttpClientUtil.getInstance().doGet(getTokenUrl, null)).get("access_token");
    		
    		//2.获取授权部门
    		String authDeptResBody = HttpClientUtil.getInstance().doGet(getAuthUrl+"?access_token="+token, null);
    		JSONObject jsonAuthDept = JSONObject.parseObject(authDeptResBody);
	        JSONArray jsonAuthDepts = jsonAuthDept.getJSONObject("auth_org_scopes").getJSONArray("authed_dept");
	        String authDept = jsonAuthDepts.getString(0);
    		System.out.println(authDept);
    		//3.获取部门列表
    		String listDeptResBody = HttpClientUtil.getInstance().doGet(getDeptList+"?access_token="+token+"&id="+authDept, null);
    		System.out.println(listDeptResBody);
    		JSONObject jsonDeptRes = JSONObject.parseObject(listDeptResBody);
    		System.out.println(jsonDeptRes);
    		JSONArray jsonDepts = jsonDeptRes.getJSONArray("department");
    		/*System.out.println("部门总数："+jsonDepts.size());
    		for(int i = 0;i<jsonDepts.size();i++) {
    			JSONObject depts = (JSONObject) jsonDepts.get(i);
    			//4.获取部门成员列表  ---需要循环调用添加
        		String listDeptUserResBody = HttpClientUtil.getInstance().doGet(getDeptUser+"?access_token="+token+"&department_id="+depts.get("id"), null);
        		System.out.println("id==>> "+depts.get("id")+listDeptUserResBody);
    		}*/
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
