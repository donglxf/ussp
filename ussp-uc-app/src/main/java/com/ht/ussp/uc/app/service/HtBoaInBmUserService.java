/*
 * FileName: HtBoaInContrastService.java
 * Author:   谭荣巧
 * Date:     2018/3/4 20:31
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.config.bm.DESC;
import com.ht.ussp.uc.app.config.bm.RequestData;
import com.ht.ussp.uc.app.domain.HtBoaInBmUser;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInBmUserRepository;
import com.ht.ussp.util.HttpClientUtil;

/**
 * 信贷用户
 * @author tangxs
 *
 */
@Service
public class HtBoaInBmUserService {
    @Autowired
    private HtBoaInBmUserRepository htBoaInBmUserRepository;

    public List<HtBoaInBmUser> getHtBoaInBmUserList() {
        return htBoaInBmUserRepository.findAll();
    }
    
    /**
     * status 1正常 0离职
     * @param status
     * @return
     */
    public List<HtBoaInBmUser> getHtBoaInBmUserListByStatus(String status) {
        return htBoaInBmUserRepository.findByStatus(status);
    }
    
    
    public PageResult<HtBoaInBmUser> findAllByPage(PageConf pageConf) {
    	PageResult result = new PageResult();
        Sort sort = null;
        Pageable pageable = null;
        List<Order> orders = new ArrayList<Order>();
        /*if (null != pageConf.getSortNames()) {
            for (int i = 0; i < pageConf.getSortNames().size(); i++) {
                orders.add(new Order(pageConf.getSortOrders().get(i), pageConf.getSortNames().get(i)));
            }
            sort = new Sort(orders);
        }*/
        orders.add(new Order(Direction.DESC, "orgCode"));
        sort = new Sort(orders);
        if (null != pageConf.getPage() && null != pageConf.getSize())
            pageable = new PageRequest(pageConf.getPage(), pageConf.getSize(), sort);
       

       
        Specification<HtBoaInBmUser> querySpecifi = null;
        if (null != pageConf.getSearch()  && 0 < pageConf.getSearch().trim().length()) {
            String search = pageConf.getSearch().trim();
            querySpecifi = new Specification<HtBoaInBmUser>() {
                @Override
                public Predicate toPredicate(Root<HtBoaInBmUser> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
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
        
        Page<HtBoaInBmUser> pageData =null;
        if (null != pageable && null != querySpecifi) {
        	pageData = this.htBoaInBmUserRepository.findAll(querySpecifi, pageable);
        }else {
        	pageData = this.htBoaInBmUserRepository.findAll(pageable);
        }
        
        if (pageData != null) {
            result.count(pageData.getTotalElements()).data(pageData.getContent());
        }
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
        return result;
    }

    public List<HtBoaInBmUser> getHtBoaInBmUserByUserId(String userId) {
        return htBoaInBmUserRepository.findByUserId(userId);
    }

	public HtBoaInBmUser save(HtBoaInBmUser u) {
		return htBoaInBmUserRepository.save(u);
	}

	public List<HtBoaInBmUser> createBmUserInfo(String bmUserId,String apiUrl) {
		List<HtBoaInBmUser> listHtBoaInBmUser = null;
		if(StringUtils.isEmpty(apiUrl)) {
			apiUrl = "http://172.16.200.104:8084/apitest/api/ltgproject/dod";
		}
		if(StringUtils.isEmpty(bmUserId)) {
			return listHtBoaInBmUser;
		}
		try {
			RequestData requestData = new RequestData();
	    	requestData.setData("{\"xdUserId\":\""+bmUserId+"\"}");
	    	requestData.setMethodName("Api4Alms_GetXindaiUserInfo");
	    	String encryptStr = JSON.toJSONString(requestData);
	    	DESC desc = new DESC();
			encryptStr = desc.Encryption(encryptStr);
			Map<String, String> map = new TreeMap<>();
	        map.put("Content-Type", "application/json; charset=utf-8");
			String  apiResult = HttpClientUtil.getInstance().doPostWithJson(apiUrl, encryptStr, map);
			if(StringUtils.isEmpty(apiResult)) {
				return listHtBoaInBmUser;
			}else {
				Map resultMap =  (Map) JSONUtils.parse(apiResult);
				if(resultMap!=null &&! resultMap.isEmpty()) {
					String resultData = resultMap.get("A")==null?"":resultMap.get("A")+"";
					String resultUUId = resultMap.get("UUId")==null?"":resultMap.get("UUId")+""; 
					String resultContent = desc.Decode(resultData, resultUUId);//解密
					if(StringUtils.isNotEmpty(resultContent)) {
						Map resultContentMap =  (Map) JSONUtils.parse(resultContent);
						if(resultContentMap!=null &&! resultContentMap.isEmpty()) {
							String param = resultContentMap.get("param")+"";
							if(StringUtils.isNotEmpty(param)) {
								Map paramMap =  (Map) JSONUtils.parse(param);
								if(paramMap!=null &&! paramMap.isEmpty()) {
									String returnCode = paramMap.get("ReturnCode")+"";
									if("1".equals(returnCode)) {//成功
										Map dataMap = (Map) paramMap.get("Data");
										if(dataMap!=null) {
											//Map dataMap =  (Map) JSONUtils.parse(data);
											String xdUserId = dataMap.get("xdUserId")==null?"":(dataMap.get("xdUserId")+"");
											String username = dataMap.get("username")==null?"":(dataMap.get("username")+"");
											String mobilephone = dataMap.get("mobilephone")==null?"":(dataMap.get("mobilephone")+"");
											String email = dataMap.get("email")==null?"":(dataMap.get("email")+"");
											String subCompanyId = dataMap.get("subCompanyId")==null?"":(dataMap.get("subCompanyId")+"");
											String organizationId = dataMap.get("organizationId")==null?"":(dataMap.get("organizationId")+"");
											listHtBoaInBmUser = htBoaInBmUserRepository.findByUserId(xdUserId);
											if(listHtBoaInBmUser==null && StringUtils.isNotEmpty(mobilephone)) {
												listHtBoaInBmUser = htBoaInBmUserRepository.findByMobile(mobilephone);
											}
											if(listHtBoaInBmUser==null && StringUtils.isNotEmpty(email)) {
												listHtBoaInBmUser = htBoaInBmUserRepository.findByEmail(email);
											}
											if(listHtBoaInBmUser==null || listHtBoaInBmUser.isEmpty()) {
												HtBoaInBmUser u = new HtBoaInBmUser();
												u.setUserId(xdUserId);
												u.setUserName(username);
												u.setMobile(mobilephone);
												u.setEmail(email);
												u.setParentDept(subCompanyId);//所属分公司
												u.setOrgCode(organizationId);
												u.setDelFlag(0);
												u.setCreatedDatetime(new Date());
												u.setStatus("0");
												u = htBoaInBmUserRepository.save(u);
												listHtBoaInBmUser = new ArrayList<>();
												listHtBoaInBmUser.add(u);
											} 
											return listHtBoaInBmUser;
										}
									}else {
										return null;
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return listHtBoaInBmUser;
	}
	
}
