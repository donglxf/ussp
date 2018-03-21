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
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    
    
    
    /**
     * 获取钉钉用户数据
     * @param page
     * @return
     */
    @RequestMapping(value = {"/user/getDownDD" }, method = RequestMethod.POST)
    public Result  getDownDD(PageVo page) {
    	//1.获取钉钉用户 、机构保存到中间表
    	htBoaInOrgService.getDD();
    	
    	//2.对钉钉用户数据进行转换  a.将钉钉机构转换成UC机构 b.将新的机构赋值给用户
    	//a。将钉钉机构转换成UC机构  如果UC已经存在的机构 则不再重新生成UC的orgCode
    	List<DdDept> ddOrgList = htBoaInOrgService.getDdDeptList(); //钉钉机构列表
    	List<HtBoaInOrg> listHtBoaInOrg = htBoaInOrgService.findAll(); //信贷机构列表
    	List<HtBoaInContrast> listHtBoaInContrast = synDataService.findListByType("10");//对照列表
    	List<DdDept> ddAddOrgList = new ArrayList<DdDept>(); //钉钉机构列表(需要新增的机构)
    	for(HtBoaInContrast htBoaInContrast : listHtBoaInContrast) {
    		ddAddOrgList = ddOrgList.stream().filter(ddlist -> htBoaInContrast.getDdBusinessId().equals(ddlist.getDeptId())).collect(Collectors.toList());
    	}
    	
    	for(DdDept ddDept : ddAddOrgList) {
    		/* if (StringUtils.isNotEmpty(ddDept.getParentId())) {
    			 List<HtBoaInOrg> listHtBoaInOrgs=htBoaInOrgService.findByParentOrgCode(ddDept.getParentId());
    			 int size = 0;
		    	 if(listHtBoaInOrg==null || listHtBoaInOrg.isEmpty()) {
		    		size = 1;
		    	 }else {
		    		size = listHtBoaInOrg.size()+1;
		    	 }
		    	 String orgCode= String.format("%s%02d", ddDept.getParentId(), size);
    		        
    			 HtBoaInOrg newOrg = new HtBoaInOrg();
                 String newPath;
                 if (StringUtils.isEmpty(path)) {
                     newPath = orgCodeTemp;
                 } else {
                     newPath = String.format("%s/%s", path, orgCodeTemp);
                     newOrg.setParentOrgCode(newOrgCode);
                 }
                 newOrg.setRootOrgCode(newPath.split("/")[0]);
                 newOrg.setOrgCode(orgCode);
                 newOrg.setOrgPath(newPath);
                 newOrg.setOrgNameCn(ddDept.getDeptName());
                 newOrg.setCreateOperator("");
                 newOrg.setUpdateOperator("");
                 newOrg.setDataSource(3);
                 newOrg.setSequence(i);
                 newOrg.setRemark(ddDept.getDeptId());//把钉钉组织机构id存入该字段
                 newOrgList.add(newOrg);
             }*/
    	}
    	
        
        List<HtBoaInOrg> newOrgList = converOrg(ddOrgList, "1", "D", "");
        
    	List<HtBoaInUser> userList = htBoaInUserService.findAll();
    	List<DdDeptUser> listDdDeptUser =  htBoaInOrgService.getDDUserList();
    	List<HtBoaInUser> newUserList = new ArrayList<>();
        HtBoaInUser u;

        List<HtBoaInLogin> loginList = new ArrayList<>();
        HtBoaInLogin l;
        
        
        
    	List<HtBoaInBmUser> listHtBoaInBmUser = htBoaInBmUserService.getHtBoaInBmUserList();
        
    	//3.对钉钉机构数据进行转换
    	
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
    private String generatorUserId(List<String> userIds, String rootOrgCode, int isOrgUser, int dataSource, String jobNumber) {
        String userId;
        String oc = rootOrgCode.replace("D", "");
        if (jobNumber != null && jobNumber.contains("HX-")) {
            userId = String.format("%s%s%s%s%s", oc, 1, dataSource, isOrgUser, jobNumber.replace("HX-", ""));
            if (userIds.contains(userId)) {
                userId = String.format("%s%s%s%s%s", oc, 1, dataSource, isOrgUser, generateNumber(5));
            }
        } else {
            userId = String.format("%s%s%s%s%s", oc, 0, dataSource, isOrgUser, generateNumber(5));
            if (userIds.contains(userId)) {
                userId = String.format("%s%s%s%s%s", oc, 0, dataSource, isOrgUser, generateNumber(5));
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
}
