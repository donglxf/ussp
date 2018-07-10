package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.DdUserOperator;
import com.ht.ussp.uc.app.domain.HtBoaInBmOrg;
import com.ht.ussp.uc.app.domain.HtBoaInBmUser;
import com.ht.ussp.uc.app.domain.HtBoaInContrast;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInBmOrgRepository;
import com.ht.ussp.uc.app.repository.HtBoaInBmUserRepository;
import com.ht.ussp.uc.app.repository.HtBoaInContrastRepository;
import com.ht.ussp.uc.app.repository.HtBoaInUserRepository;
import com.ht.ussp.uc.app.vo.UserContrastVo;
import com.ht.ussp.util.HttpClientUtil;
import com.ht.ussp.util.JsonUtil;

@Service
public class SynDataService {

    @Autowired
    private HtBoaInContrastRepository htBoaInContrastRepository;
    
    @Autowired
    private HtBoaInUserRepository htBoaInUserRepository;
    
    @Autowired
    private HtBoaInBmUserRepository htBoaInBmUserRepository;
    
    @Autowired
    private HtBoaInBmOrgRepository htBoaInBmOrgRepository;
    
    @Autowired
    private HtBoaInOrgService htBoaInOrgService;
    
    public HtBoaInContrast findByUcBusinessIdAndType(String userId,String type) {
    	List<HtBoaInContrast> listHtBoaInContrast = htBoaInContrastRepository.findByUcBusinessIdAndType(userId, type);
    	if(listHtBoaInContrast!=null&&!listHtBoaInContrast.isEmpty()) {
    		return listHtBoaInContrast.get(0);
    	}else {
    		return null;
    	}
    }
    
    public PageResult<UserContrastVo> findAllByPage(PageConf pageConf) {
    	PageResult result = new PageResult();
        Sort sort = null;
        Pageable pageable = null;
        List<Order> orders = new ArrayList<Order>();
        List<HtBoaInBmUser> listHtBoaInBmUser = new ArrayList<HtBoaInBmUser>();
        List<HtBoaInContrast> listHtBoaInContrast = new ArrayList<HtBoaInContrast>();
        List<HtBoaInOrg> ucOrgList = new ArrayList<HtBoaInOrg>();
        List<HtBoaInBmOrg> bmOrgList = new ArrayList<HtBoaInBmOrg>();
        if (null != pageConf.getSortNames()) {   
            for (int i = 0; i < pageConf.getSortNames().size(); i++) {
                orders.add(new Order(Direction.DESC, pageConf.getSortNames().get(i)));
            }
            sort = new Sort(orders);
        }
        if (null != pageConf.getPage() && null != pageConf.getSize())
            pageable = new PageRequest(pageConf.getPage(), pageConf.getSize(), sort);
       
        List<UserContrastVo> userContrastVoList = new ArrayList<>();
        
        Specification<HtBoaInUser> querySpecifi = null;
        if (null != pageConf.getSearch()  && 0 < pageConf.getSearch().trim().length()) {
            String search = pageConf.getSearch().trim();
            querySpecifi = new Specification<HtBoaInUser>() {
                @Override
                public Predicate toPredicate(Root<HtBoaInUser> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    // 模糊查找
                    predicates.add(cb.like(root.get("userId").as(String.class), "%" + search + "%"));
                    predicates .add(cb.like(root.get("userName").as(String.class),  "%" + search + "%"));
                    predicates.add( cb.like(root.get("mobile").as(String.class), "%" + search + "%"));
                    predicates.add(  cb.like(root.get("jobNumber").as(String.class), "%" + search + "%"));
                    // and到一起的话所有条件就是且关系，or就是或关系
                    return cb.or(predicates.toArray(new Predicate[predicates.size()]));
                } 
            };
        }
        if (null != pageConf.getPage() && null != pageConf.getSize()  && null != sort)
            pageable = new PageRequest(pageConf.getPage(), pageConf.getSize(), sort);
        else if (null != pageConf.getPage() && null != pageConf.getSize())
            pageable = new PageRequest(pageConf.getPage(), pageConf.getSize());
        
        Page<HtBoaInUser> pageData =null;
        if (null != pageable && null != querySpecifi) {
        	pageData = this.htBoaInUserRepository.findAll(querySpecifi, pageable);
        }else {
        	pageData = this.htBoaInUserRepository.findAll(pageable);
        }
        
        if (pageData != null) {
            if(pageData.getContent()!=null) {
            	listHtBoaInContrast = htBoaInContrastRepository.findByType("20");
            	listHtBoaInBmUser = htBoaInBmUserRepository.findAll();
            	ucOrgList =htBoaInOrgService.findAll();
            	bmOrgList =htBoaInBmOrgRepository.findAll();
            	for(HtBoaInUser user :pageData.getContent()) {
            		UserContrastVo userContrastVo = new UserContrastVo();
                	userContrastVo.setUserId(user.getUserId());
                	userContrastVo.setUserName(user.getUserName());
                	userContrastVo.setOrgCode(user.getOrgCode());
                	userContrastVo.setEmail(user.getEmail());
                	userContrastVo.setMobile(user.getMobile());
                	userContrastVo.setJobNumber(user.getJobNumber());
                	//HtBoaInContrast htBoaInContrast = listHtBoaInContrast.stream().filter(hc -> hc.getUcBusinessId().equals(user.getUserId())).findFirst().get();
                	List<HtBoaInContrast> listorgcontrast = listHtBoaInContrast.stream().filter(hc -> hc.getUcBusinessId().equals(user.getUserId())).collect(Collectors.toList());
    			    if(listorgcontrast!=null && !listorgcontrast.isEmpty()) {
    			    	HtBoaInContrast htBoaInContrast = listorgcontrast.get(0);
    			    	if(htBoaInContrast!=null && htBoaInContrast.getBmBusinessId()!=null) {
    	                	List<HtBoaInBmUser> listbmuser = listHtBoaInBmUser.stream().filter(us -> us.getUserId().equals(htBoaInContrast.getBmBusinessId())).collect(Collectors.toList());
    	                	 if(listbmuser!=null && !listbmuser.isEmpty()) {
    	                		 HtBoaInBmUser htBoaInBmUser = listbmuser.get(0);
    	                    		if(htBoaInBmUser!=null) {
    	                    			userContrastVo.setBmUserId(htBoaInBmUser.getUserId());
    	                            	userContrastVo.setBmUserName(htBoaInBmUser.getUserName());
    	                            	userContrastVo.setBmOrgCode(htBoaInBmUser.getOrgCode());
    	                            	userContrastVo.setBmEmail(htBoaInBmUser.getEmail());
    	                            	userContrastVo.setBmMobile(htBoaInBmUser.getMobile());
    	                            	userContrastVo.setBmJobNumber(htBoaInBmUser.getJobNumber());
    	                            	 List<HtBoaInBmOrg> listorgbm = bmOrgList.stream().filter(hc -> hc.getOrgCode().equals(htBoaInBmUser.getOrgCode())).collect(Collectors.toList());
    	                            	 if(listorgbm!=null && !listorgbm.isEmpty()) {
    	                            		 HtBoaInBmOrg  o = listorgbm.get(0);
    	                            		 userContrastVo.setBmOrgName(o.getOrgNameCn());
    	                            	 }
    	                    		}
    	                	 }
                    		userContrastVo.setId(htBoaInContrast.getId() == null ? null : htBoaInContrast.getId());
                    	}
    			    }
    			    
                	List<HtBoaInOrg> listorguc = ucOrgList.stream().filter(hc -> hc.getOrgCode().equals(user.getOrgCode())).collect(Collectors.toList());
                	 if(listorguc!=null && !listorguc.isEmpty()) {
                		 HtBoaInOrg  o = listorguc.get(0);
                		 userContrastVo.setOrgName(o.getOrgNameCn());
                	 }
                	userContrastVoList.add(userContrastVo);  
            	}
            	
            }
            result.count(pageData.getTotalElements()).data(userContrastVoList);
        }
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
        return result;
    }


	public void removeBmUserById(long id) {
		htBoaInContrastRepository.delete(id);
	}


	public void addBmUser(HtBoaInContrast htBoaInContrast) {
		htBoaInContrastRepository.saveAndFlush(htBoaInContrast);
	}
	
	public static void main(String[] args) throws Exception {
	    Map<String, String> map = new TreeMap<>();
	    map.put("app", "UC");
        map.put("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJVc2VyIEF1dGhvcml6ZSIsInVzZXJJZCI6IjAxMTExMTAwMDAiLCJvcmdDb2RlIjoiRDAxIiwiaXNzIjoicWl1d2Vud3VAaG9uZ3RlLmluZm8iLCJpYXQiOjE1MzExMjA5MzcsImV4cCI6MTUzMTEyMjczNywiYXVkIjoi57uf5LiA55So5oi35p2D6ZmQ57O757uf566h55CG5ZGYIn0.itTlJxoFpstp8FG8Cy35u9ANKxykJvD0Lx8ZgfyhDlVXYxQgucXSDWQiv2__2r3TCrJVi2vhF8osh89hRr6Ibg");
        map.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> paramMap = new TreeMap<>();
        paramMap.put("page", "1");
        paramMap.put("limit", "2000");
        paramMap.put("query.keyWord", "optype=1syntype=0");
		String  apiResult = HttpClientUtil.getInstance().doPostWithMap("http://47.106.46.65:30111/uc/syndata/loadSynUserListByPage", paramMap, map);
		Result result =  JsonUtil.json2Obj(apiResult, Result.class);
		List<DdUserOperator> listDdUserOperator = (List<DdUserOperator>) result.getData();
		for(int i = 0;i<listDdUserOperator.size();i++) {
			DdUserOperator d = JsonUtil.json2Obj(JSONObject.toJSONString(listDdUserOperator.get(i)), DdUserOperator.class);
			paramMap.put("id", d.getId()+"");
			String  apiResult2 = HttpClientUtil.getInstance().doPostWithMap("http://47.106.46.65:30111/uc/syndata/synUserDataToUc?id="+d.getId(), paramMap, map);
			System.out.println(apiResult2);
		}
		System.out.println("完成");
	}
}
