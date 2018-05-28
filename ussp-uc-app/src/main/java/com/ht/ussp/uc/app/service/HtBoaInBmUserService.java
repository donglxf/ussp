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
import java.util.List;

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

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInBmUser;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInBmUserRepository;

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
}
