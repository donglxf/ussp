package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ht.ussp.common.Constants;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInBmUser;
import com.ht.ussp.uc.app.domain.HtBoaInContrast;
import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.domain.HtBoaInUserApp;
import com.ht.ussp.uc.app.model.SelfBoaInUserInfo;
import com.ht.ussp.uc.app.repository.HtBoaInBmUserRepository;
import com.ht.ussp.uc.app.repository.HtBoaInContrastRepository;
import com.ht.ussp.uc.app.repository.HtBoaInLoginRepository;
import com.ht.ussp.uc.app.repository.HtBoaInOrgRepository;
import com.ht.ussp.uc.app.repository.HtBoaInUserAppRepository;
import com.ht.ussp.uc.app.repository.HtBoaInUserRepository;
import com.ht.ussp.uc.app.vo.BmUserVo;
import com.ht.ussp.uc.app.vo.LoginInfoVo;
import com.ht.ussp.uc.app.vo.UserMessageVo;
import com.ht.ussp.util.BeanUtils;
import com.ht.ussp.util.EncryptUtil;
import com.ht.ussp.util.LogicUtil;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInUserService
 * @Description: 用户信息服务层
 * @date 2018年1月8日 下午9:47:25
 */
@Service
public class HtBoaInUserService {
    @Autowired
    private HtBoaInUserRepository htBoaInUserRepository;
    @Autowired
    private HtBoaInLoginRepository htBoaInLoginRepository;
    @Autowired
    private HtBoaInOrgRepository htBoaInOrgRepository;
    @Autowired
    private HtBoaInContrastRepository htBoaInContrastRepository;
    @Autowired
    private HtBoaInUserAppRepository htBoaInUserAppRepository;
    @Autowired
    private HtBoaInBmUserRepository htBoaInBmUserRepository;
    

    /**
     * @return HtBoaInUser
     * @throws
     * @Title: findByUserName
     * @Description: 通过userId查询用户信息
     */
    public HtBoaInUser findByUserId(String userId) {
        return htBoaInUserRepository.findByUserId(userId);
    }

    /**
     * 通过userId email Mobile 工号登录
     * @param userId
     * @return
     */
    public HtBoaInUser findByUserIdOrEmailOrMobileOrJobNumber(String userId,String email,String mobile,String jboNumber){
        return htBoaInUserRepository.findByUserIdOrEmailOrMobileOrJobNumber(userId, email, mobile, jboNumber);
    }

    public LoginInfoVo queryUserInfo(String userId,String app) {
        LoginInfoVo loginInfoVo = new LoginInfoVo();
        UserMessageVo userMessageVo = htBoaInUserRepository.queryUserByUserId(userId);
        if (LogicUtil.isNull(userMessageVo)) {
            return null;
        }
        List<HtBoaInOrg> orgList = htBoaInOrgRepository.findByOrgCode(userMessageVo.getOrgCode());
        if (orgList != null && !orgList.isEmpty()) {
            userMessageVo.setOrgName(orgList.get(0).getOrgNameCn());
        }
        BeanUtils.deepCopy(userMessageVo, loginInfoVo);
        //获取用户关联的信贷信息 
        if(loginInfoVo!=null) {
        	
        	List<HtBoaInContrast> htBoaInContrastOrgList = htBoaInContrastRepository.findByUcBusinessIdAndType(loginInfoVo.getOrgCode(),"10");
        	if(htBoaInContrastOrgList!=null&&!htBoaInContrastOrgList.isEmpty()) {
        		loginInfoVo.setBmOrgCode(htBoaInContrastOrgList.get(0).getBmBusinessId());
        		loginInfoVo.setDdOrgCode(htBoaInContrastOrgList.get(0).getDdBusinessId());
        	}
        	List<HtBoaInContrast> htBoaInContrastList = htBoaInContrastRepository.findByUcBusinessIdAndType(loginInfoVo.getUserId(),"20");
        	if(htBoaInContrastList!=null&&!htBoaInContrastList.isEmpty()) {
        		loginInfoVo.setBmUserId(htBoaInContrastList.get(0).getBmBusinessId());
        		loginInfoVo.setDdUserId(htBoaInContrastList.get(0).getDdBusinessId());
        		if(StringUtils.isEmpty(loginInfoVo.getBmOrgCode())&&StringUtils.isNoneEmpty(htBoaInContrastList.get(0).getBmBusinessId())) {
        			List<HtBoaInBmUser> listHtBoaInBmUser = htBoaInBmUserRepository.findByUserId(htBoaInContrastList.get(0).getBmBusinessId());
    				if(listHtBoaInBmUser!=null && !listHtBoaInBmUser.isEmpty()) {
    					loginInfoVo.setBmOrgCode(listHtBoaInBmUser.get(0).getOrgCode());
    				}
        		}
        	}else {
        		List<HtBoaInBmUser> listHtBoaInBmUser = htBoaInBmUserRepository.findByMobile(userMessageVo.getMobile());
				if(listHtBoaInBmUser!=null && !listHtBoaInBmUser.isEmpty()) {
					if(loginInfoVo!=null) {
						loginInfoVo.setBmOrgCode(listHtBoaInBmUser.get(0).getOrgCode());
						loginInfoVo.setBmUserId(listHtBoaInBmUser.get(0).getUserId());
					}
				}
        	}
        	
        	//获取用户是否是系统管理员
        	if(StringUtils.isNotEmpty(app)) {
        		HtBoaInUserApp htBoaInUserApp = htBoaInUserAppRepository.findByUserIdAndApp(userId, app);
        		if(htBoaInUserApp!=null) {
        			loginInfoVo.setController(htBoaInUserApp.getController());
        		}else {
        			loginInfoVo.setController("");
        		}
        	}
        }
        return loginInfoVo;
    }

    /**
     * 用户信息分页查询<br>
     *
     * @param pageRequest 分页对象
     * @param keyWord     关键字查询
     * @param query       高级查询
     * @return 用户信息列表
     * @author 谭荣巧
     * @Date 2018/1/12 8:58
     */
    public PageResult<List<UserMessageVo>> getUserListPage(PageRequest pageRequest, String orgCode, String keyWord, Map<String, String> query) {
        PageResult result = new PageResult();
        Page<UserMessageVo> pageData = null;
        Page<UserMessageVo> pageDataAll = null;
        List<UserMessageVo> listUserMessageVo = new ArrayList<UserMessageVo>();
        List<HtBoaInOrg> orgList = null;
        if(StringUtils.isEmpty(orgCode)&&StringUtils.isNotEmpty(keyWord)) { //查全局
        	orgList = htBoaInOrgRepository.findAll();
        	pageDataAll = htBoaInUserRepository.queryUserPageAll( keyWord, pageRequest);
        }
        if(StringUtils.isNotEmpty(orgCode)&&StringUtils.isNotEmpty(keyWord)) {
        	if("D01".equals(orgCode)) {//顶级机构查全局
        		orgList = htBoaInOrgRepository.findAll();
        		pageDataAll = htBoaInUserRepository.queryUserPageAll( keyWord, pageRequest);
        	}else {//按条件查询
        		pageData = htBoaInUserRepository.queryUserPage(orgCode, keyWord, pageRequest);
        	}
        }
        if(StringUtils.isNotEmpty(orgCode)&&StringUtils.isEmpty(keyWord)) {//按条件查询
        	pageData = htBoaInUserRepository.queryUserPage(orgCode, keyWord, pageRequest);
        }
//        Page<HtBoaInUser> pageData = null;
//        if (query != null && query.size() > 0 && query.get("orgCode") != null) {
//            if (!StringUtil.isEmpty(keyWord)) {
//                Specification<HtBoaInUser> specification = (root, query1, cb) -> {
//                    Predicate p1 = cb.like(root.get("jobNumber").as(String.class), "%" + keyWord + "%");
//                    Predicate p2 = cb.like(root.get("userName").as(String.class), "%" + keyWord + "%");
//                    Predicate p3 = cb.like(root.get("mobile").as(String.class), "%" + keyWord + "%");
//                    Predicate p4 = cb.equal(root.get("orgCode").as(String.class), query.get("orgCode"));
////                    Join<HtBoaInUser, HtBoaInLogin> join = root.join("htBoaInLogin", JoinType.LEFT);
////                    Predicate p5 = cb.equal(join.get("userId").as(String.class), root.get("userId").as(String.class));
//                    //把Predicate应用到CriteriaQuery中去,因为还可以给CriteriaQuery添加其他的功能，比如排序、分组啥的
//                    query1.where(cb.and(cb.or(p1, p2, p3), p4));
//                    return query1.getRestriction();
//                };
//                pageData = htBoaInUserRepository.findAll(specification, pageRequest);
//            } else {//高级查询
//                //创建查询条件数据对象
//                HtBoaInUser customer = DtoUtil.mapToEntity(query, new HtBoaInUser());
//                //创建匹配器，即如何使用查询条件
//                ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
//                        // 忽略 id 和 createTime 字段。
//                        .withIgnorePaths("id", "createdDatetime", "orgPath", "jpaVersion")
//                        // 忽略为空字段。
//                        .withIgnoreNullValues();
//                //创建实例
//                Example<HtBoaInUser> ex = Example.of(customer, matcher);
//                pageData = htBoaInUserRepository.findAll(ex, pageRequest);
//            }
//        }
        if (pageData != null) {
            result.count(pageData.getTotalElements()).data(pageData.getContent());
        }else {
        	if(pageDataAll!=null) {
        		for(UserMessageVo userMessageVo : pageDataAll.getContent()) {
        			if(orgList!=null) {
        				HtBoaInOrg o = orgList.stream().filter(org -> org.getOrgCode().equals(userMessageVo.getOrgCode())).findFirst().get();
        				userMessageVo.setOrgName(o.getOrgNameCn());
        				listUserMessageVo.add(userMessageVo);
        			}
        		}
        		result.count(pageDataAll.getTotalElements()).data(listUserMessageVo);
        	}
        	
        }
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
        return result;
    }

    /**
     * 新增用户信息<br>
     *
     * @param user 用户信息
     * @return 新的用户信息
     * @author 谭荣巧
     * @Date 2018/1/13 16:49
     */
    @Transactional
    public boolean saveUserInfoAndLoginInfo(HtBoaInUser user, HtBoaInLogin logininfo) {
    	String userId = "";
    	int isOrgUser = 1;
    	if (user.getJobNumber() != null && user.getJobNumber().contains("HX-")) {
            userId = String.format("%s%s%s%s%s", "01", 1, "1", "1", user.getJobNumber().replace("HX-", ""));
        } else {
        	isOrgUser = 0;
            userId = String.format("%s%s%s%s%s", "01", 0, "1", "1", generateNumber(5));
        }
    	//查看userId是否被占用
    	HtBoaInUser htBoaInUser = findByUserId(userId);
    	if(htBoaInUser!=null) {
    		userId = String.format("%s%s%s%s%s", "01", isOrgUser, "1", "1", generateNumber(6));
    	}
    	user.setUserId(userId);
        htBoaInUserRepository.save(user);
        logininfo.setUserId(userId);
        htBoaInLoginRepository.save(logininfo);
        return true;
    }
   
    /**
     * 新增信贷用户信息<br>
     *
     */
    @Transactional
    public HtBoaInContrast saveBmUserInfo(BmUserVo bmuservo,List<HtBoaInUser> listHtBoaInUser,List<HtBoaInLogin> listHtBoaInLogin,List<HtBoaInBmUser> listHtBoaInBmUser, List<HtBoaInContrast> listHtBoaInContrast) {
    	 HtBoaInUser htBoaInUser = null;
    	 HtBoaInContrast htBoaInContrast = null;
    	 if(null==listHtBoaInContrast) {
    		 listHtBoaInContrast = htBoaInContrastRepository.findByBmBusinessIdAndType(bmuservo.getBmUserId(), "20");
    	 }
    	//查看是否存在用户，如果存在用户则不用新增用户
    	 if(listHtBoaInContrast!=null&&!listHtBoaInContrast.isEmpty()) {
    		 Optional<HtBoaInContrast>  htBoaInContrastOptional =listHtBoaInContrast.stream().filter(contrast ->bmuservo.getBmUserId().equals(contrast.getBmBusinessId())).findFirst();
			 if(htBoaInContrastOptional!=null&&htBoaInContrastOptional.isPresent()) {
				 htBoaInContrast = htBoaInContrastOptional.get();
			 }
    		 HtBoaInContrast htBoaInContrast2 = htBoaInContrast;
    		 if(htBoaInContrast2!=null) {
    			 if(StringUtils.isNotEmpty(htBoaInContrast2.getUcBusinessId())) {
    				 Optional<HtBoaInUser>  htBoaInUserOptional =listHtBoaInUser.stream().filter(user ->htBoaInContrast2.getUcBusinessId().equals(user.getUserId())).findFirst();
        			 if(htBoaInUserOptional!=null&&htBoaInUserOptional.isPresent()) {
        				 htBoaInUser = htBoaInUserOptional.get();
        			 }
    			 }
    		 }
    	 } 
    	 if(htBoaInUser==null) {
	    	if(StringUtils.isNotEmpty(bmuservo.getEmail())) {
	    			 if(listHtBoaInUser!=null&&!listHtBoaInUser.isEmpty()) {
		    			 Optional<HtBoaInUser>  htBoaInUserOptional =listHtBoaInUser.stream().filter(user ->bmuservo.getEmail().equals(user.getEmail())).findFirst();
		    			 if(htBoaInUserOptional!=null &&htBoaInUserOptional.isPresent()) {
		    				 htBoaInUser = htBoaInUserOptional.get();
	    			 }else {
	    				 List<HtBoaInUser> listHtBoaInUsertemp = htBoaInUserRepository.findByEmail(bmuservo.getEmail());
	    				 if(listHtBoaInUsertemp!=null&&!listHtBoaInUsertemp.isEmpty()) {
	    					 htBoaInUser = listHtBoaInUsertemp.get(0);
	    				 }
	    			 }
	    		 }
    		 } 
    	 }
    	 if(htBoaInUser==null) {
			if (StringUtils.isNotEmpty(bmuservo.getMobile())) {
				if (listHtBoaInUser != null && !listHtBoaInUser.isEmpty()) {
					Optional<HtBoaInUser>  htBoaInUserOptional =listHtBoaInUser.stream().filter(user ->bmuservo.getMobile().equals(user.getMobile())).findFirst();
	    			 if(htBoaInUserOptional!=null&&htBoaInUserOptional.isPresent()) {
	    				 htBoaInUser = htBoaInUserOptional.get();
	    			 }
				}else {
					List<HtBoaInUser> listHtBoaInUsertemp = htBoaInUserRepository.findByMobile(bmuservo.getMobile());
					if (listHtBoaInUsertemp != null && !listHtBoaInUsertemp.isEmpty()) {
						htBoaInUser = listHtBoaInUsertemp.get(0);
					}
				}
			}
    	 }
    	 if(StringUtils.isEmpty(bmuservo.getOrgCode())) {
    		 bmuservo.setOrgCode("D0100");
    	 }
    	 if(htBoaInUser==null) {
    		    String userId = "";
    		    int isOrgUser = 1;
				HtBoaInUser user = new HtBoaInUser();
				user.setDataSource(Constants.USER_DATASOURCE_3);
				user.setEmail(bmuservo.getEmail());
				user.setIsOrgUser(1);
				user.setJobNumber(StringUtils.isEmpty(bmuservo.getJobNumber())?null:bmuservo.getJobNumber());
				user.setMobile(StringUtils.isEmpty(bmuservo.getMobile())?null:bmuservo.getMobile()  );
				user.setOrgCode(bmuservo.getOrgCode());
				user.setUserName(bmuservo.getUserName());
				user.setIdNo(StringUtils.isEmpty(bmuservo.getIdNo())?null:bmuservo.getIdNo()   );
				user.setRootOrgCode("D01");
				user.setUserType("10");
				user.setDelFlag(0);
				user.setStatus(Constants.STATUS_0);
				user.setCreatedDatetime(new Date());
				user.setLastModifiedDatetime(new Date());

    	    	if (user.getJobNumber() != null && user.getJobNumber().contains("HX-")) {
    	            userId = String.format("%s%s%s%s%s", "01", 1, "1", "1", user.getJobNumber().replace("HX-", ""));
    	        } else {
    	        	isOrgUser = 0;
    	            userId = String.format("%s%s%s%s%s", "01", 0, "1", "1", generateNumber(5));
    	        }
    	    	//查看userId是否被占用
    	    	HtBoaInUser htBoaInUsers = findByUserId(userId);
    	    	if(htBoaInUsers!=null) {
    	    		userId = String.format("%s%s%s%s%s", "01", isOrgUser, "1", "1", generateNumber(6));
    	    	}
    	    	user.setUserId(userId);
    	    	htBoaInUser =  htBoaInUserRepository.save(user);
    	    	
    	    	
    	    	HtBoaInLogin loginInfo = null;
				if(listHtBoaInLogin==null) {
					loginInfo = htBoaInLoginRepository.findByLoginId(bmuservo.getBmUserId());
				}else {
					Optional<HtBoaInLogin>  htBoaInLoginOptional =listHtBoaInLogin.stream().filter(login ->bmuservo.getBmUserId().equals(login.getUserId())).findFirst();
	    			 if(htBoaInLoginOptional!=null&&htBoaInLoginOptional.isPresent()) {
	    				 loginInfo = htBoaInLoginOptional.get();
	    			 }
				}
    	    	if(loginInfo==null) {
					loginInfo = new HtBoaInLogin();
					loginInfo.setStatus(Constants.USER_STATUS_0);
					loginInfo.setPassword(EncryptUtil.passwordEncrypt("123456"));
					loginInfo.setFailedCount(0);
					loginInfo.setRootOrgCode(bmuservo.getOrgCode());
					loginInfo.setDelFlag(0);
					loginInfo.setStatus(Constants.STATUS_0);
					loginInfo.setCreatedDatetime(new Date());
					loginInfo.setLastModifiedDatetime(new Date());
					loginInfo.setUserId(userId);
	    	        htBoaInLoginRepository.save(loginInfo);
				} 
    	 }
    	 HtBoaInBmUser htBoaInBmUser = null;
        //添加bmUser
    	 if(listHtBoaInBmUser==null) {
    		 List<HtBoaInBmUser> listHtBoaInBmUserTemp = htBoaInBmUserRepository.findByUserId(bmuservo.getBmUserId());
    		 if(listHtBoaInBmUserTemp!=null &&!listHtBoaInBmUserTemp.isEmpty()) {
    			 htBoaInBmUser = listHtBoaInBmUserTemp.get(0);
    		 }
    	 }else {
    		 if(StringUtils.isNotEmpty(bmuservo.getBmUserId())) {
    			 Optional<HtBoaInBmUser>  htBoaInBmUserOptional =listHtBoaInBmUser.stream().filter(bumuser ->bmuservo.getBmUserId().equals(bumuser.getUserId())).findFirst();
    			 if(htBoaInBmUserOptional!=null&&htBoaInBmUserOptional.isPresent()) {
    				 htBoaInBmUser = htBoaInBmUserOptional.get();
    			 }
    		 }
    	 }
        if(htBoaInBmUser==null) { //不存在，则添加
        	htBoaInBmUser = new HtBoaInBmUser();
        	htBoaInBmUser.setUserId(bmuservo.getBmUserId());
        	htBoaInBmUser.setEmail(bmuservo.getEmail());
        	htBoaInBmUser.setJobNumber(bmuservo.getJobNumber());
        	htBoaInBmUser.setMobile(bmuservo.getMobile());
        	htBoaInBmUser.setOrgCode(bmuservo.getOrgCode());
        	htBoaInBmUser.setUserName(bmuservo.getUserName());
        	htBoaInBmUser.setIdNo(bmuservo.getIdNo());
        	htBoaInBmUser.setDelFlag(0);
        	htBoaInBmUser.setCreatedDatetime(new Date());
        	htBoaInBmUser.setLastModifiedDatetime(new Date());
        	htBoaInBmUser = htBoaInBmUserRepository.save(htBoaInBmUser);
        }
        //添加关系
        if(listHtBoaInContrast==null || listHtBoaInContrast.isEmpty()) {
        	htBoaInContrast = new HtBoaInContrast();
        	htBoaInContrast.setBmBusinessId(bmuservo.getBmUserId());
        	htBoaInContrast.setType("20");
        	htBoaInContrast.setContrast("信贷添加");
        	htBoaInContrast.setContrastDatetime(new Date());
        	htBoaInContrast.setStatus(Constants.STATUS_0);
        	htBoaInContrast.setUcBusinessId(htBoaInUser.getUserId());
        	htBoaInContrast.setContrastDatetime(new Date());
        	htBoaInContrast = htBoaInContrastRepository.save(htBoaInContrast);
        }
        return htBoaInContrast;
    }
    
    public HtBoaInContrast saveBmUserInfoByBmToken(BmUserVo bmuservo) {
   	 HtBoaInUser htBoaInUser = null;
   	 HtBoaInContrast htBoaInContrast = null;
   	 List<HtBoaInContrast> listHtBoaInContrast = htBoaInContrastRepository.findByBmBusinessIdAndType(bmuservo.getBmUserId(), "20");
   	 
   	//查看是否存在用户，如果存在用户则不用新增用户
   	 if(listHtBoaInContrast!=null&&!listHtBoaInContrast.isEmpty()) {
   		htBoaInContrast = listHtBoaInContrast.get(0);
   		if(htBoaInContrast!=null) {
   			htBoaInUser = htBoaInUserRepository.findByUserId(htBoaInContrast.getUcBusinessId());
   		}
   	 } 
   	 if(htBoaInUser==null) {
	    if(StringUtils.isNotEmpty(bmuservo.getEmail())) {
	    	List<HtBoaInUser> listEmailU= htBoaInUserRepository.findByEmail(bmuservo.getEmail());
	    	if(listEmailU!=null&&!listEmailU.isEmpty()) {
	    		htBoaInUser = listEmailU.get(0);
	    	}
   		 } 
   	 }
	 if (htBoaInUser == null) {
		if (StringUtils.isNotEmpty(bmuservo.getMobile())) {
			List<HtBoaInUser> listMobileU = htBoaInUserRepository.findByMobile(bmuservo.getMobile());
			if (listMobileU != null && !listMobileU.isEmpty()) {
				htBoaInUser = listMobileU.get(0);
			}
		}
	 }
   	 if(StringUtils.isEmpty(bmuservo.getOrgCode())) {
   		 bmuservo.setOrgCode("D0100");
   	 }
   	 if(htBoaInUser==null) {
   		    String userId = "";
   		    int isOrgUser = 1;
				HtBoaInUser user = new HtBoaInUser();
				user.setDataSource(Constants.USER_DATASOURCE_3);
				user.setEmail(bmuservo.getEmail());
				user.setIsOrgUser(1);
				user.setJobNumber(bmuservo.getJobNumber());
				user.setMobile(bmuservo.getMobile());
				user.setOrgCode(bmuservo.getOrgCode());
				user.setUserName(bmuservo.getUserName());
				user.setIdNo(bmuservo.getIdNo());
				user.setRootOrgCode("D01");
				user.setUserType("10");
				user.setDelFlag(0);
				user.setStatus(Constants.STATUS_0);
				user.setCreatedDatetime(new Date());
				user.setLastModifiedDatetime(new Date());

   	    	if (user.getJobNumber() != null && user.getJobNumber().contains("HX-")) {
   	            userId = String.format("%s%s%s%s%s", "01", 1, "1", "1", user.getJobNumber().replace("HX-", ""));
   	        } else {
   	        	isOrgUser = 0;
   	            userId = String.format("%s%s%s%s%s", "01", 0, "1", "1", generateNumber(5));
   	        }
   	    	//查看userId是否被占用
   	    	HtBoaInUser htBoaInUsers = findByUserId(userId);
   	    	if(htBoaInUsers!=null) {
   	    		userId = String.format("%s%s%s%s%s", "01", isOrgUser, "1", "1", generateNumber(6));
   	    	}
   	    	user.setUserId(userId);
   	    	htBoaInUser =  htBoaInUserRepository.save(user);
   	    	
   	    	HtBoaInLogin loginInfo = htBoaInLoginRepository.findByLoginId(bmuservo.getBmUserId());
   	    	if(loginInfo==null) {
					loginInfo = new HtBoaInLogin();
					loginInfo.setStatus(Constants.USER_STATUS_0);
					loginInfo.setPassword(EncryptUtil.passwordEncrypt("123456"));
					loginInfo.setFailedCount(0);
					loginInfo.setRootOrgCode(bmuservo.getOrgCode());
					loginInfo.setDelFlag(0);
					loginInfo.setStatus(Constants.STATUS_0);
					loginInfo.setCreatedDatetime(new Date());
					loginInfo.setLastModifiedDatetime(new Date());
					loginInfo.setUserId(userId);
	    	        htBoaInLoginRepository.save(loginInfo);
			} 
   	 }
   	 HtBoaInBmUser htBoaInBmUser = null;
       //添加bmUser
   	 List<HtBoaInBmUser> listHtBoaInBmUserTemp = htBoaInBmUserRepository.findByUserId(bmuservo.getBmUserId());
	 if(listHtBoaInBmUserTemp!=null &&!listHtBoaInBmUserTemp.isEmpty()) {
			 htBoaInBmUser = listHtBoaInBmUserTemp.get(0);
	 }
     if(htBoaInBmUser==null) { //不存在，则添加
       	htBoaInBmUser = new HtBoaInBmUser();
       	htBoaInBmUser.setUserId(bmuservo.getBmUserId());
       	htBoaInBmUser.setEmail(bmuservo.getEmail());
       	htBoaInBmUser.setJobNumber(bmuservo.getJobNumber());
       	htBoaInBmUser.setMobile(bmuservo.getMobile());
       	htBoaInBmUser.setOrgCode(bmuservo.getOrgCode());
       	htBoaInBmUser.setUserName(bmuservo.getUserName());
       	htBoaInBmUser.setIdNo(bmuservo.getIdNo());
       	htBoaInBmUser.setDelFlag(0);
       	htBoaInBmUser.setCreatedDatetime(new Date());
       	htBoaInBmUser.setLastModifiedDatetime(new Date());
       	htBoaInBmUser = htBoaInBmUserRepository.save(htBoaInBmUser);
      }
      //添加关系
      if(listHtBoaInContrast==null || listHtBoaInContrast.isEmpty()) {
       	htBoaInContrast = new HtBoaInContrast();
       	htBoaInContrast.setBmBusinessId(bmuservo.getBmUserId());
       	htBoaInContrast.setType("20");
       	htBoaInContrast.setContrast("信贷添加");
       	htBoaInContrast.setContrastDatetime(new Date());
       	htBoaInContrast.setStatus(Constants.STATUS_0);
       	htBoaInContrast.setUcBusinessId(htBoaInUser.getUserId());
       	htBoaInContrast.setContrastDatetime(new Date());
       	htBoaInContrast = htBoaInContrastRepository.save(htBoaInContrast);
       }
       return htBoaInContrast;
   }
    public HtBoaInUser saveBmUserInfo(HtBoaInBmUser htBoaInBmUser) { 
    	HtBoaInUser htBoaInUser = null;
    	try {
			if(htBoaInBmUser!=null) {
				if(StringUtils.isNotEmpty(htBoaInBmUser.getUserId())) { //根据email mobile查看用户是否存在UC，如果存在则添加关联关系，如果不存在则添加UC用户
					if(StringUtils.isNotEmpty(htBoaInBmUser.getEmail())) {
						List<HtBoaInUser> listHtBoaInUsertemp = htBoaInUserRepository.findByEmail(htBoaInBmUser.getEmail());
						if(listHtBoaInUsertemp!=null&&!listHtBoaInUsertemp.isEmpty()) {
							HtBoaInUser htBoaInUserTemp = listHtBoaInUsertemp.get(0);
							List<HtBoaInContrast> listHtBoaInContrast = htBoaInContrastRepository.findByBmBusinessIdAndType(htBoaInBmUser.getUserId(), "20");//bm关系是否存在，不存在则添加
							if(listHtBoaInContrast==null||listHtBoaInContrast.isEmpty()) {
								HtBoaInContrast htBoaInContrast = new HtBoaInContrast();
					        	htBoaInContrast.setBmBusinessId(htBoaInBmUser.getUserId());
					        	htBoaInContrast.setType("20");
					        	htBoaInContrast.setContrast("信贷查询添加");
					        	htBoaInContrast.setContrastDatetime(new Date());
					        	htBoaInContrast.setStatus(Constants.STATUS_0);
					        	htBoaInContrast.setUcBusinessId(htBoaInUserTemp.getUserId());
					        	htBoaInContrast.setContrastDatetime(new Date());
					        	htBoaInContrast = htBoaInContrastRepository.save(htBoaInContrast);
							}else {
								HtBoaInContrast u = listHtBoaInContrast.get(0);
								if(u!=null) {
									if(u.getBmBusinessId()!=null) {
										if(!u.getBmBusinessId().equals(htBoaInBmUser.getUserId())) {
											u.setExtendBusinessId1(htBoaInBmUser.getUserId());
										}
									}
								}
							}
							return htBoaInUserTemp;
						}
					}
					if(StringUtils.isNotEmpty(htBoaInBmUser.getMobile())) {
						List<HtBoaInUser> listHtBoaInUsertemp = htBoaInUserRepository.findByMobile(htBoaInBmUser.getMobile());
						if(listHtBoaInUsertemp!=null&&!listHtBoaInUsertemp.isEmpty()) {
							HtBoaInUser htBoaInUserTemp = listHtBoaInUsertemp.get(0);
							List<HtBoaInContrast> listHtBoaInContrast = htBoaInContrastRepository.findByBmBusinessIdAndType(htBoaInBmUser.getUserId(), "20");//bm关系是否存在，不存在则添加
							if(listHtBoaInContrast==null||listHtBoaInContrast.isEmpty()) {
								HtBoaInContrast htBoaInContrast = new HtBoaInContrast();
					        	htBoaInContrast.setBmBusinessId(htBoaInBmUser.getUserId());
					        	htBoaInContrast.setType("20");
					        	htBoaInContrast.setContrast("信贷查询添加");
					        	htBoaInContrast.setContrastDatetime(new Date());
					        	htBoaInContrast.setStatus(Constants.STATUS_0);
					        	htBoaInContrast.setUcBusinessId(htBoaInUserTemp.getUserId());
					        	htBoaInContrast.setContrastDatetime(new Date());
					        	htBoaInContrast = htBoaInContrastRepository.save(htBoaInContrast);
							}else {
								HtBoaInContrast u = listHtBoaInContrast.get(0);
								if(u!=null) {
									if(u.getBmBusinessId()!=null) {
										if(!u.getBmBusinessId().equals(htBoaInBmUser.getUserId())) {
											u.setExtendBusinessId1(htBoaInBmUser.getUserId());
										}
									}
								}
							}
							return htBoaInUserTemp;
						}
					}
					//UC不存在用户则添加
					 if(htBoaInUser==null) {
			    		    String userId = "";
			    		    int isOrgUser = 1;
							HtBoaInUser user = new HtBoaInUser();
							user.setDataSource(Constants.USER_DATASOURCE_3);
							user.setEmail(htBoaInBmUser.getEmail());
							user.setIsOrgUser(1);
							user.setJobNumber(htBoaInBmUser.getJobNumber());
							user.setMobile(htBoaInBmUser.getMobile());
							user.setOrgCode("D0100");
							user.setUserName(htBoaInBmUser.getUserName());
							user.setIdNo(htBoaInBmUser.getIdNo());
							user.setRootOrgCode("D01");
							user.setUserType("10");
							user.setDelFlag(0);
							user.setStatus(Constants.STATUS_0);
							user.setCreatedDatetime(new Date());
							user.setLastModifiedDatetime(new Date());

			    	    	if (user.getJobNumber() != null && user.getJobNumber().contains("HX-")) {
			    	            userId = String.format("%s%s%s%s%s", "01", 1, "1", "1", user.getJobNumber().replace("HX-", ""));
			    	        } else {
			    	        	isOrgUser = 0;
			    	            userId = String.format("%s%s%s%s%s", "01", 0, "1", "1", generateNumber(5));
			    	        }
			    	    	//查看userId是否被占用
			    	    	HtBoaInUser htBoaInUsers = findByUserId(userId);
			    	    	if(htBoaInUsers!=null) {
			    	    		userId = String.format("%s%s%s%s%s", "01", isOrgUser, "1", "1", generateNumber(6));
			    	    	}
			    	    	user.setUserId(userId);
			    	    	htBoaInUser =  htBoaInUserRepository.save(user);
			    	    	
			    	    	HtBoaInLogin loginInfo = null;
			    	    	if(loginInfo==null) {
								loginInfo = new HtBoaInLogin();
								loginInfo.setStatus(Constants.USER_STATUS_0);
								loginInfo.setPassword(EncryptUtil.passwordEncrypt("123456"));
								loginInfo.setFailedCount(0);
								loginInfo.setRootOrgCode("D01");
								loginInfo.setDelFlag(0);
								loginInfo.setStatus(Constants.STATUS_1);
								loginInfo.setCreatedDatetime(new Date());
								loginInfo.setLastModifiedDatetime(new Date());
								loginInfo.setUserId(userId);
				    	        htBoaInLoginRepository.save(loginInfo);
							} 
			    	    	List<HtBoaInContrast> listHtBoaInContrast = htBoaInContrastRepository.findByUcBusinessIdAndType(htBoaInBmUser.getUserId(), "20");//bm关系是否存在，不存在则添加
							if(listHtBoaInContrast==null||listHtBoaInContrast.isEmpty()) {
								HtBoaInContrast htBoaInContrast = new HtBoaInContrast();
					        	htBoaInContrast.setBmBusinessId(htBoaInBmUser.getUserId());
					        	htBoaInContrast.setType("20");
					        	htBoaInContrast.setContrast("信贷查询添加");
					        	htBoaInContrast.setContrastDatetime(new Date());
					        	htBoaInContrast.setStatus(Constants.STATUS_0);
					        	htBoaInContrast.setUcBusinessId(htBoaInUser.getUserId());
					        	htBoaInContrast.setContrastDatetime(new Date());
					        	htBoaInContrast = htBoaInContrastRepository.save(htBoaInContrast);
							}
			    	 }
					
					
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return htBoaInUser;
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
    

    public List<HtBoaInUser> findAll() {
        return this.htBoaInUserRepository.findAll();
    }

    public List<HtBoaInUser> findAll(HtBoaInUser u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaInUser> ex = Example.of(u, matcher);
        return this.htBoaInUserRepository.findAll(ex);
    }

    public List<SelfBoaInUserInfo> findAll(SelfBoaInUserInfo u) {
        List<SelfBoaInUserInfo> list = this.htBoaInUserRepository
                .listSelfUserInfo(u.getUserId());
        for (SelfBoaInUserInfo s : list) {
            List<Map<String, Object>> roles = this.htBoaInUserRepository
                    .listSelfUserInfo4Role(s.getUserId());
            for (Map<String, Object> o : roles) {
                s.getRoleCodes()
                        .add(null != o.get("0") ? o.get("0").toString() : null);
                s.getRoleNames()
                        .add(null != o.get("1") ? o.get("1").toString() : null);
                s.getRoleNameChs()
                        .add(null != o.get("2") ? o.get("2").toString() : null);
            }
            List<Map<String, Object>> positions = this.htBoaInUserRepository
                    .listSelfUserInfo4Position(s.getUserId());
            for (Map<String, Object> o : positions) {
                s.getPositionCodes()
                        .add(null != o.get("0") ? o.get("0").toString() : null);
                s.getPositionNames()
                        .add(null != o.get("1") ? o.get("1").toString() : null);
                s.getPositionNameChs()
                        .add(null != o.get("2") ? o.get("2").toString() : null);
            }
        }
        return list;
    }

    public HtBoaInUser add(HtBoaInUser u) {
        return this.htBoaInUserRepository.saveAndFlush(u);
    }

    public HtBoaInUser update(HtBoaInUser u) {
        return this.htBoaInUserRepository.save(u);
    }

    @Transactional
    public boolean setDelFlagByUserId(String userId) {
        return htBoaInUserRepository.setDelFlagByUserId(userId) == 1;
    }

    public UserMessageVo getUserByUserId(String userId) {
        return htBoaInUserRepository.queryUserByUserId(userId);
    }

    @Transactional
    public boolean updateUserByUserId(HtBoaInUser user) {
        return htBoaInUserRepository.updateUserByUserId(user.getUserId(), user.getUserName(), user.getJobNumber(), user.getMobile(), user.getIdNo(), user.getEmail(), user.getUpdateOperator()) == 1;
    }


    public PageResult<List<UserMessageVo>> queryUserIsNullPwd(PageRequest pageRequest, String orgCode, String keyWord) {
        PageResult result = new PageResult();
        keyWord = keyWord == null ? "" : keyWord;
        Page<Object[]> pageData = htBoaInUserRepository.queryUserIsNullPwd(pageRequest, orgCode, keyWord);
        List<UserMessageVo> userMessageVoList = new ArrayList<>();
        if (pageData.getContent() != null) {
            for (Object[] objects : pageData.getContent()) {
                UserMessageVo userMessageVo = new UserMessageVo();
                userMessageVo.setId(objects[0] == null ? null : Long.parseLong(objects[0].toString()));
                userMessageVo.setUserId(objects[1] == null ? null : objects[1].toString());
                userMessageVo.setJobNumber(objects[2] == null ? null : objects[2].toString());
                userMessageVo.setUserName(objects[3] == null ? null : objects[3].toString());
                userMessageVo.setOrgCode(objects[4] == null ? null : objects[4].toString());
                userMessageVo.setMobile(objects[5] == null ? null : objects[5].toString());
                userMessageVo.setEmail(objects[6] == null ? null : objects[6].toString());
                userMessageVo.setIdNo(objects[7] == null ? null : objects[7].toString());
                userMessageVo.setDelFlag(objects[8] == null ? null : Integer.parseInt(objects[8].toString()));
                userMessageVo.setOrgName(objects[9] == null ? null : objects[9].toString());
                userMessageVoList.add(userMessageVo);
            }
        }
        if (pageData != null) {
            result.count(pageData.getTotalElements()).data(userMessageVoList);
        }
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
        return result;
    }

	public void add(List<HtBoaInUser> newUserList) {
		this.htBoaInUserRepository.save(newUserList);
	}

	public HtBoaInUser findByJobNumber(String jobnum) {
		 HtBoaInUser htBoaInUser = null;
		 List<HtBoaInUser> listHtBoaInUser = this.htBoaInUserRepository.findByJobNumber(jobnum);
		 if(listHtBoaInUser!=null && !listHtBoaInUser.isEmpty()) {
			 htBoaInUser = listHtBoaInUser.get(0);
		 }
		return htBoaInUser;
	}

	public HtBoaInUser findByMobile(String mobile) {
		 HtBoaInUser htBoaInUser = null;
		 List<HtBoaInUser> listHtBoaInUser = this.htBoaInUserRepository.findByMobile(mobile);
		 if(listHtBoaInUser!=null && !listHtBoaInUser.isEmpty()) {
			 htBoaInUser = listHtBoaInUser.get(0);
		 }
		return htBoaInUser;
	}

	public HtBoaInUser findByEmail(String email) {
		 HtBoaInUser htBoaInUser = null;
		 List<HtBoaInUser> listHtBoaInUser = this.htBoaInUserRepository.findByEmail(email);
		 if(listHtBoaInUser!=null && !listHtBoaInUser.isEmpty()) {
			 htBoaInUser = listHtBoaInUser.get(0);
		 }
		return htBoaInUser;
	}

	public List<HtBoaInUser> getUserListByTime(String startTime, String endTime) {
		return this.htBoaInUserRepository.getUserListByTime(startTime,endTime);
	}

}
