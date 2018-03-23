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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.common.Constants;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.DdDept;
import com.ht.ussp.uc.app.domain.DdDeptUser;
import com.ht.ussp.uc.app.domain.HtBoaInBmUser;
import com.ht.ussp.uc.app.domain.HtBoaInContrast;
import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.service.HtBoaInBmUserService;
import com.ht.ussp.uc.app.service.HtBoaInContrastService;
import com.ht.ussp.uc.app.service.HtBoaInLoginService;
import com.ht.ussp.uc.app.service.HtBoaInOrgService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;
import com.ht.ussp.uc.app.service.SynDataService;
import com.ht.ussp.uc.app.vo.PageVo;

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
    
    /**
     * 获取钉钉用户数据
     * @param page
     * @return
     */
    @RequestMapping(value = {"/user/getDownDD2" }, method = RequestMethod.POST)
    public Result  getDownDD() {
    	//1.获取钉钉用户 、机构保存到中间表
    	//htBoaInOrgService.getDD();
        return Result.buildSuccess();
    }
    
    /**
     * 获取钉钉用户数据
     * @param page
     * @return
     */
    @RequestMapping(value = {"/user/getDownDD" }, method = RequestMethod.POST)
    public Result  convertUser() {
    	//2.对钉钉用户数据进行转换  a.将钉钉机构转换成UC机构 b.将新的机构赋值给用户
    	//a。将钉钉机构转换成UC机构  如果UC已经存在的机构 则不再重新生成UC的orgCode
    	List<DdDept> ddOrgList = htBoaInOrgService.getDdDeptList(); //钉钉机构列表
    	List<HtBoaInContrast> listHtBoaInContrastOrg = synDataService.findListByType("10");//对照列表
    	List<DdDept> ddAddOrgList = new ArrayList<DdDept>(); //已经添加的机构
    	List<HtBoaInContrast> listHtBoaInContrasts = new ArrayList<>();
    	
    	List<HtBoaInOrg> ucOrgList =htBoaInOrgService.findAll();
    	List<HtBoaInContrast> listHtBoaInContrast = htBoaInContrastService.getHtBoaInContrastListByType("10");
    	List<HtBoaInOrg> removeOrgList = new ArrayList<HtBoaInOrg>();
    	List<HtBoaInOrg> newOrgList = converOrgNewAdd(ddOrgList, "58800327", "D", "",ucOrgList,listHtBoaInContrast);
    	
    	for(HtBoaInOrg htBoaInOrg : ucOrgList) {
    		removeOrgList = newOrgList.stream().filter(ddlist -> htBoaInOrg.getOrgCode().equals(ddlist.getOrgCode())).collect(Collectors.toList());
    		newOrgList.removeAll(removeOrgList);//去除已经添加的机构
    	}
		for (HtBoaInOrg org : newOrgList) {
			HtBoaInContrast htBoaInContrast = new HtBoaInContrast();
			htBoaInContrast.setType("10");
			htBoaInContrast.setUcBusinessId(org.getOrgCode());
			htBoaInContrast.setDdBusinessId(org.getRemark());
			htBoaInContrast.setContrast("自动对照");
			org.setRemark(null);
			listHtBoaInContrasts.add(htBoaInContrast);
		}
    	 System.out.println("");
    	for(DdDept ddDept : ddOrgList) {/*
    		 if (StringUtils.isNotEmpty(ddDept.getParentId())) {
    			 HtBoaInOrg newOrg = new HtBoaInOrg();
    			 DdDept ddDeptParent = htBoaInOrgService.findByDDParentOrgCode(ddDept.getParentId());
    			 String orgCodeParent="";
    			 String orgCode = "";
    			 String newPath = "";
    			 int size = 0;
    			 if(ddDeptParent!=null) {  //一般都是存在父级，根据dd父级查找对应ucOrgcode并且，查找对应子节点个数
    				 List<HtBoaInContrast> listorg = htBoaInContrastService.getHtBoaInContrastListByDdBusinessId(ddDeptParent.getDeptId(),"10");
    				 if(listorg!=null && !listorg.isEmpty()) { //父节点原来已经存在，则在原来基础上增加机构编码
    					 orgCodeParent = listorg.get(0).getUcBusinessId();
    	    			 List<HtBoaInOrg> listHtBoaInOrgs=htBoaInOrgService.findByParentOrgCode(orgCodeParent);
    			    	 if(listHtBoaInOrgs==null || listHtBoaInOrgs.isEmpty()) { //Uc不存在，则检测dd是否存在
    			    		size = 1;
    			    	 }else {
    			    		size = listHtBoaInOrgs.size()+1;
    			    		orgCode= String.format("%s%02d", ddDept.getParentId(), size);
    				    	newOrg.setParentOrgCode(listHtBoaInOrgs.get(0).getParentOrgCode());
    				    	newPath =  listHtBoaInOrgs.get(0).getOrgPath().substring(0, listHtBoaInOrgs.get(0).getOrgPath().lastIndexOf("/"))+"/"+orgCode;
    			    	 }
    				 }else{ //父节点原来不存在，则需要重新生成机构编码
    					 continue;
    				 }
    			 }else {
    				 continue;
    			 }
    		   
                 newOrg.setRootOrgCode(newPath.split("/")[0]);
                 newOrg.setOrgCode(orgCode);
                 newOrg.setOrgPath(newPath);
                 newOrg.setOrgNameCn(ddDept.getDeptName());
                 newOrg.setCreateOperator("");
                 newOrg.setUpdateOperator("");
                 newOrg.setDataSource(3);
                 newOrg.setSequence(size);
                 newOrg.setRemark(ddDept.getDeptId());//把钉钉组织机构id存入该字段
                 htBoaInOrgService.add(newOrg);
                 
                 //添加机构与钉钉的关联
                 HtBoaInContrast htBoaInContrast = new HtBoaInContrast();
                 htBoaInContrast.setType("10");
                 htBoaInContrast.setUcBusinessId(orgCode);
                 htBoaInContrast.setDdBusinessId(ddDept.getDeptId());
                 htBoaInContrast.setContrast("自动对照");
                 listHtBoaInContrasts.add(htBoaInContrast);
             }
    	*/}

       /*  for (HtBoaInOrg org : newOrgList) {  
             System.out.printf("%-20s%-20s%-20s%-80s%-10s%-15s%-40s\n", org.getOrgCode(), org.getParentOrgCode(), org.getRootOrgCode(), org.getOrgPath(), org.getSequence(), org.getRemark(), org.getOrgNameCn());
             htBoaInContrast = new HtBoaInContrast();
             htBoaInContrast.setType("10");
             htBoaInContrast.setUcBusinessId(org.getOrgCode());
             htBoaInContrast.setDdBusinessId(org.getRemark());
             htBoaInContrast.setContrast("自动对照");
             org.setRemark(null);
             htBoaInContrasts.add(htBoaInContrast);
         }*/

         //htBoaInOrgService.add(newOrgList);//保存转换后的数据
    	
    	if(listHtBoaInContrasts!=null && !listHtBoaInContrasts.isEmpty()) {
    		htBoaInContrastService.add(listHtBoaInContrasts);
    	}
        return Result.buildSuccess();
    }
    
    public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("1");list.add("2");list.add("3");list.add("4");list.add("5");list.add("6");
		for(String a : list) {
			if("3".equals(a)) {
				list.add("7");
			}
			System.out.println(a);
		}
	}
    
    /**
     * 获取钉钉用户数据
     * @param page
     * @return
     */
    @RequestMapping(value = {"/user/convertOrg" }, method = RequestMethod.POST)
    public Result  convertOrg() { 
    	List<HtBoaInContrast> listHtBoaInContrasts = new ArrayList<>();
    	////用户
    	List<HtBoaInUser> userList = htBoaInUserService.findAll();
    	List<DdDeptUser> listDdDeptUser =  htBoaInOrgService.getDDUserList();
    	List<HtBoaInUser> newUserList = new ArrayList<>();
    	List<HtBoaInContrast> listHtBoaInContrastUser = synDataService.findListByType("20");//对照列表
    	List<DdDeptUser> ddAddUserList = new ArrayList<DdDeptUser>();
    	List<HtBoaInOrg> newOrgList = htBoaInOrgService.findAll(); //信贷机构列表
    	List<HtBoaInLogin> loginList = new ArrayList<>();
    	//已经存在的用户不处理
    	for(HtBoaInContrast htBoaInContrast : listHtBoaInContrastUser) {
    		ddAddUserList = listDdDeptUser.stream().filter(ddlist -> htBoaInContrast.getDdBusinessId().equals(ddlist.getUserId())).collect(Collectors.toList());
    	}
    	//去除已经添加的用户
    	listDdDeptUser.removeAll(ddAddUserList);
    	for (DdDeptUser ddDeptUser : listDdDeptUser) {
    		HtBoaInOrg o = newOrgList.stream().filter(org -> org.getRemark().equals(ddDeptUser.getOrgCode())).findFirst().get();
    		HtBoaInUser u = new HtBoaInUser();
            int isOrgUser = ddDeptUser.getJobNumber()==null?0:1;
            String userId = generatorUserId(o.getRootOrgCode(),  isOrgUser,2, ddDeptUser.getJobNumber());
            HtBoaInUser u1 = htBoaInUserService.findByUserId(userId);
            if(u1!=null) {
            	userId = generatorUserId(o.getRootOrgCode(),  isOrgUser,3, ddDeptUser.getJobNumber());
            }
            u.setUserId(userId);
            u.setUserName(ddDeptUser.getUserName());
            u.setOrgCode(o.getOrgCode());
            u.setRootOrgCode(o.getRootOrgCode());
            u.setOrgPath(o.getOrgPath());
            u.setEmail(ddDeptUser.getEmail());
            u.setMobile(ddDeptUser.getMobile());
            u.setJobNumber(ddDeptUser.getJobNumber());
            u.setIsOrgUser(isOrgUser);
            u.setDelFlag(Constants.DEL_0);
            u.setCreateOperator("自动同步");
            u.setUpdateOperator("自动同步");
            u.setDataSource(2);
            htBoaInUserService.add(u);
            
            HtBoaInLogin l = new HtBoaInLogin();
            l.setLoginId(UUID.randomUUID().toString().replace("-", ""));
            l.setUserId(userId);
            l.setRootOrgCode(u.getRootOrgCode());
            l.setCreateOperator("自动同步");
            l.setUpdateOperator("自动同步");
            l.setStatus("0");
            l.setFailedCount(0);
            l.setDelFlag(0);
            loginList.add(l);
            
            //添加用户与钉钉的关联
            HtBoaInContrast htBoaInContrast = new HtBoaInContrast();
            htBoaInContrast.setType("20");
            htBoaInContrast.setUcBusinessId(userId);
            htBoaInContrast.setDdBusinessId(ddDeptUser.getUserId());
            htBoaInContrast.setContrast("自动对照");
            listHtBoaInContrasts.add(htBoaInContrast);
            
    	}
    	if(loginList!=null && !loginList.isEmpty()) {
    		htBoaInLoginService.add(loginList);
    	}
    	if(listHtBoaInContrasts!=null && !listHtBoaInContrasts.isEmpty()) {
    		htBoaInContrastService.add(listHtBoaInContrasts);
    	}
    	return Result.buildSuccess();
    }
    
    @PostMapping(value = "/dataConver")
    public void dataConver() {
        //List<HtBoaInOrg> orgList = htBoaInOrgService.findAll();
       List<HtBoaInUser> userList = htBoaInUserService.findAll();
        List<DdDeptUser> listDdDeptUser =  htBoaInOrgService.getDDUserList();

        List<DdDept> orgList = htBoaInOrgService.getDdDeptList();
        
        List<HtBoaInBmUser> listHtBoaInBmUser = htBoaInBmUserService.getHtBoaInBmUserList();
        List<HtBoaInContrast> listHtBoaInContrast = htBoaInContrastService.getHtBoaInContrastList();
        List<HtBoaInOrg> newOrgList = converOrg(orgList, "1", "D", "");

        List<HtBoaInContrast> htBoaInContrasts = new ArrayList<>();
        HtBoaInContrast htBoaInContrast;

        List<HtBoaInUser> newUserList = new ArrayList<>();
        HtBoaInUser u;

        List<HtBoaInLogin> loginList = new ArrayList<>();
        HtBoaInLogin l;

        List<String> userIdList = new ArrayList<>();
        for (DdDeptUser ddDeptUser : listDdDeptUser) {
           /* HtBoaInOrg o = newOrgList.stream().filter(org -> org.getRemark().equals(ddDeptUser.getOrgCode())).findFirst().get();
            u = new HtBoaInUser();
            int isOrgUser = ddDeptUser.getJobNumber()==null?0:1;
            String userId = generatorUserId(userIdList, o.getRootOrgCode(), 2, isOrgUser, ddDeptUser.getJobNumber());
            userIdList.add(userId);
            u.setUserId(userId);
            u.setUserName(ddDeptUser.getUserName());
            u.setOrgCode(o.getOrgCode());
            u.setRootOrgCode(o.getRootOrgCode());
            u.setOrgPath(o.getOrgPath());
            u.setEmail(ddDeptUser.getEmail());
            u.setMobile(ddDeptUser.getMobile());
            u.setJobNumber(ddDeptUser.getJobNumber());
            u.setIsOrgUser(isOrgUser);
            u.setDelFlag(Constants.DEL_0);
            u.setCreateOperator("自动同步");
            u.setUpdateOperator("自动同步");
            u.setDataSource(2);

            System.out.printf("%-30s%-20s%-20s%-20s%-20s%-20s\n", ddDeptUser.getUserId(), u.getUserId(), u.getJobNumber(), ddDeptUser.getOrgCode(), u.getOrgCode(), u.getUserName());


            newUserList.add(u);

            l = new HtBoaInLogin();
            l.setLoginId(UUID.randomUUID().toString().replace("-", ""));
            l.setUserId(userId);
            l.setRootOrgCode(u.getRootOrgCode());
            l.setCreateOperator("自动同步");
            l.setUpdateOperator("自动同步");
            l.setStatus("0");
            l.setFailedCount(0);
            l.setDelFlag(0);
            loginList.add(l);
 

            htBoaInContrast = new HtBoaInContrast();
            htBoaInContrast.setType("20");
            htBoaInContrast.setUcBusinessId(userId);
            htBoaInContrast.setDdBusinessId(ddDeptUser.getUserId());
           // htBoaInContrast.setBmBusinessId(user.getUserId());
            htBoaInContrast.setContrast("自动对照");
            htBoaInContrasts.add(htBoaInContrast);*/
        	
        	//对照信贷系统用
        	List<HtBoaInBmUser> htBoaInBmUsers1 = listHtBoaInBmUser.stream().filter(bmuser -> bmuser.getMobile()!=null).collect(Collectors.toList());
        	Optional<HtBoaInBmUser> htBoaInBmUserOptional = htBoaInBmUsers1.stream().filter(bmuser -> bmuser.getMobile().equals(ddDeptUser.getMobile())).findFirst();
        	HtBoaInBmUser htBoaInBmUser = null;
        	if(htBoaInBmUserOptional.isPresent()) {
        		htBoaInBmUser = htBoaInBmUserOptional.get();
        	}
        	if(htBoaInBmUser!=null) {
        		HtBoaInUser htBoaInUser = new HtBoaInUser();
        		List<HtBoaInUser> userList1 = userList.stream().filter(user -> user.getMobile()!=null).collect(Collectors.toList());
        		Optional<HtBoaInUser> listHtBoaInUser = userList1.stream().filter(user -> user.getMobile().equals(ddDeptUser.getMobile())).findFirst();
        		if(listHtBoaInUser.isPresent()) {
        			htBoaInUser = listHtBoaInUser.get();
        		}
        		if(htBoaInUser!=null) {
        			String userid=htBoaInUser.getUserId();
        			List<HtBoaInContrast> listHtBoaInContrast2 = listHtBoaInContrast.stream().filter(bmuser -> "20".equals(bmuser.getType())).collect(Collectors.toList());
            		Optional<HtBoaInContrast> htBoaInContrastOptional =  listHtBoaInContrast2.stream().filter(bmuser -> bmuser.getUcBusinessId().equals(userid)).findFirst();
            		
            		if(htBoaInContrastOptional.isPresent()) {
            			htBoaInContrast = htBoaInContrastOptional.get();
            			if(htBoaInContrast!=null) {
                    		htBoaInContrast.setBmBusinessId(htBoaInBmUser.getUserId());
                    		htBoaInContrasts.add(htBoaInContrast);
                    	}
            		}
        		}
        	}
        }

       /* for (HtBoaInOrg org : newOrgList) {  
            System.out.printf("%-20s%-20s%-20s%-80s%-10s%-15s%-40s\n", org.getOrgCode(), org.getParentOrgCode(), org.getRootOrgCode(), org.getOrgPath(), org.getSequence(), org.getRemark(), org.getOrgNameCn());
            htBoaInContrast = new HtBoaInContrast();
            htBoaInContrast.setType("10");
            htBoaInContrast.setUcBusinessId(org.getOrgCode());
            htBoaInContrast.setDdBusinessId(org.getRemark());
            htBoaInContrast.setContrast("自动对照");
            org.setRemark(null);
            htBoaInContrasts.add(htBoaInContrast);
        }*/

       /* htBoaInUserService.add(newUserList);
        htBoaInLoginService.add(loginList);*/
  //        htBoaInOrgService.add(newOrgList);//保存转换后的数据
          htBoaInContrastService.add(htBoaInContrasts);//保存对照关系
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
    private String generatorUserId(String rootOrgCode, int isOrgUser, int dataSource, String jobNumber) {
        String userId;
        String oc = rootOrgCode.replace("D", "");
        if (jobNumber != null && jobNumber.contains("HX-")) {
            userId = String.format("%s%s%s%s%s", oc, 1, dataSource, isOrgUser, jobNumber.replace("HX-", ""));
        } else {
            userId = String.format("%s%s%s%s%s", oc, 0, dataSource, isOrgUser, generateNumber(5));
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

    private List<HtBoaInOrg> converOrg(List<DdDept> orgList, String orgCode, String newOrgCode, String path) {
        List<HtBoaInOrg> newOrgList = new ArrayList<>();
        int i = 0;
        HtBoaInOrg newOrg;
        for (DdDept ddDept : orgList) {
            if (orgCode.equals(ddDept.getParentId())) {
                newOrg = new HtBoaInOrg();
                String orgCodeTemp = String.format("%s%02d", newOrgCode, (++i));
                String newPath;
                if (StringUtils.isEmpty(path)) {
                    newPath = orgCodeTemp;
                } else {
                    newPath = String.format("%s/%s", path, orgCodeTemp);
                    newOrg.setParentOrgCode(newOrgCode);
                }
                newOrg.setRootOrgCode(newPath.split("/")[0]);
                newOrg.setOrgCode(orgCodeTemp);
                newOrg.setOrgPath(newPath);
                newOrg.setOrgNameCn(ddDept.getDeptName());
                newOrg.setCreateOperator("");
                newOrg.setUpdateOperator("");
                newOrg.setDataSource(3);
                newOrg.setSequence(i);
                newOrg.setRemark(ddDept.getDeptId());//把钉钉组织机构id存入该字段
                newOrgList.add(newOrg);
                //System.out.printf("%-20s%-15s%-15s%-80s%-40s\n", newOrgCode, org.getOrgCode(), orgCode, newPath, org.getOrgNameCn());
                newOrgList.addAll(converOrg(orgList, ddDept.getDeptId(), orgCodeTemp, newPath));
            }
        }
        return newOrgList;
    }
    
    /**
     * 新增的org
     * @param orgList
     * @param orgCode
     * @param newOrgCode
     * @param path
     * @return
     */
    private List<HtBoaInOrg> converOrgNewAdd(List<DdDept> orgList, String orgCode, String newOrgCode, String path,List<HtBoaInOrg> ucOrgList,List<HtBoaInContrast> listHtBoaInContrast) {
        List<HtBoaInOrg> newOrgList = new ArrayList<>();
        int i = 0;
        HtBoaInOrg newOrg;
        for (DdDept ddDept : orgList) {
            if (orgCode.equals(ddDept.getParentId())) {
            	//查询转换表，之前是否已经存在，如果存在则保留原来的数据，否则产生新的数据
            	List<HtBoaInContrast> listorgcontrast = listHtBoaInContrast.stream().filter(ddlist -> orgCode.equals(ddlist.getDdBusinessId())).collect(Collectors.toList());
				
			    if(listorgcontrast!=null && !listorgcontrast.isEmpty()) { //原来已经存在，则保留，继续执行
			    	newOrg= ucOrgList.stream().filter(ddlist -> listorgcontrast.get(0).getUcBusinessId().equals(ddlist.getOrgCode())).findFirst().get();
			    	newOrgList.add(newOrg);
		              //System.out.printf("%-20s%-15s%-15s%-80s%-40s\n", newOrgCode, org.getOrgCode(), orgCode, newPath, org.getOrgNameCn());
		            newOrgList.addAll(converOrgNewAdd(orgList, ddDept.getDeptId(), newOrg.getOrgCode(), newOrg.getOrgPath(),ucOrgList,listHtBoaInContrast));
				}else {
					newOrg = new HtBoaInOrg();
	                String orgCodeTemp = String.format("%s%02d", newOrgCode, (++i));
	                String newPath;
	                if (StringUtils.isEmpty(path)) {
	                    newPath = orgCodeTemp;
	                } else {
	                    newPath = String.format("%s/%s", path, orgCodeTemp);
	                    newOrg.setParentOrgCode(newOrgCode);
	                }
	                newOrg.setRootOrgCode(newPath.split("/")[0]);
	                newOrg.setOrgCode(orgCodeTemp);
	                newOrg.setOrgPath(newPath);
	                newOrg.setOrgNameCn(ddDept.getDeptName());
	                newOrg.setCreateOperator("");
	                newOrg.setUpdateOperator("");
	                newOrg.setDataSource(3);
	                newOrg.setSequence(i);
	                newOrg.setRemark(ddDept.getDeptId());//把钉钉组织机构id存入该字段
	                newOrgList.add(newOrg);
	              //System.out.printf("%-20s%-15s%-15s%-80s%-40s\n", newOrgCode, org.getOrgCode(), orgCode, newPath, org.getOrgNameCn());
	                newOrgList.addAll(converOrgNewAdd(orgList, ddDept.getDeptId(), orgCodeTemp, newPath,ucOrgList,listHtBoaInContrast));
				}
            }
        }
        return newOrgList;
    }
}
