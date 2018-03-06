package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInContrast;
import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.model.SelfBoaInUserInfo;
import com.ht.ussp.uc.app.repository.HtBoaInContrastRepository;
import com.ht.ussp.uc.app.repository.HtBoaInLoginRepository;
import com.ht.ussp.uc.app.repository.HtBoaInOrgRepository;
import com.ht.ussp.uc.app.repository.HtBoaInUserRepository;
import com.ht.ussp.uc.app.vo.LoginInfoVo;
import com.ht.ussp.uc.app.vo.UserMessageVo;
import com.ht.ussp.util.BeanUtils;
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

    public LoginInfoVo queryUserInfo(String userId) {
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
        if(loginInfoVo!=null) {
        	HtBoaInContrast htBoaInContrast = htBoaInContrastRepository.findByUcBusinessIdAndType(loginInfoVo.getUserId(),"20");
        	if(htBoaInContrast!=null) {
        		loginInfoVo.setBmUserId(htBoaInContrast.getBmBusinessId());
        		loginInfoVo.setDdUserId(htBoaInContrast.getDdBusinessId());
        	}
        	HtBoaInContrast htBoaInContrastOrg = htBoaInContrastRepository.findByUcBusinessIdAndType(loginInfoVo.getOrgCode(),"10");
        	if(htBoaInContrastOrg!=null) {
        		loginInfoVo.setBmOrgCode(htBoaInContrastOrg.getBmBusinessId());
        		loginInfoVo.setDdOrgCode(htBoaInContrastOrg.getDdBusinessId());
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
        Page<UserMessageVo> pageData = htBoaInUserRepository.queryUserPage(orgCode, keyWord, pageRequest);
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
    		userId = String.format("%s%s%s%s%s", "01", isOrgUser, "1", "1", generateNumber(5));
    	}
    	user.setUserId(userId);
        htBoaInUserRepository.save(user);
        logininfo.setUserId(userId);
        htBoaInLoginRepository.save(logininfo);
        return true;
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


}
