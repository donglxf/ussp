package com.ht.ussp.uc.app.service;

import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInService;
import com.ht.ussp.uc.app.repository.HtBoaInServiceRepository;

@Service
public class HtBoaInServiceService {

	@Autowired
    private HtBoaInServiceRepository htBoaInServiceRepository;
	
    public HtBoaInService findById(Long id) {
        return this.htBoaInServiceRepository.getOne(id);
    }

    public List<HtBoaInService> findAll(HtBoaInService u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaInService> ex = Example.of(u, matcher);
        return this.htBoaInServiceRepository.findAll(ex);
    }
    
    public HtBoaInService add(HtBoaInService u) {
        return this.htBoaInServiceRepository.saveAndFlush(u);
    }

    public HtBoaInService update(HtBoaInService u) {
        return this.htBoaInServiceRepository.save(u);
    }
    
    public void delete(long id) {
        this.htBoaInServiceRepository.delete(id);
    }

 
	public PageResult<List<HtBoaInService>> getUserListByPage(PageRequest pageRequest, Map<String, String> query,String keyWord) {
		PageResult result = new PageResult();
		Page<HtBoaInService> pageData = null;
////      Join<HtBoaInUser, HtBoaInLogin> join = root.join("htBoaInLogin", JoinType.LEFT);
////      Predicate p5 = cb.equal(join.get("userId").as(String.class), root.get("userId").as(String.class));
//      //把Predicate应用到CriteriaQuery中去,因为还可以给CriteriaQuery添加其他的功能，比如排序、分组啥的
//      query1.where(cb.and(cb.or(p1, p2, p3), p4));
		  
		if(query!=null) {
			Specification<HtBoaInService> specification = (root, query1, cb) -> {
				Predicate p1 = cb.like(root.get("mainServiceName").as(String.class), "%" + keyWord + "%");
				Predicate p2 = cb.like(root.get("mainService").as(String.class), "%" + keyWord + "%");
				Predicate p3 = cb.equal(root.get("app").as(String.class), query.get("app"));
				query1.where(cb.and(cb.or(p1, p2), p3));
				return query1.getRestriction();
			};
			  pageData = htBoaInServiceRepository.findAll(specification, pageRequest);
		}else {
			Specification<HtBoaInService> specification = (root, query1, cb) -> {
				Predicate p1 = cb.like(root.get("mainServiceName").as(String.class), "%" + keyWord + "%");
				Predicate p2 = cb.like(root.get("mainService").as(String.class), "%" + keyWord + "%");
				query1.where(cb.or(p1, p2));
				return query1.getRestriction();
			};
			  pageData = htBoaInServiceRepository.findAll(specification, pageRequest);
		}
		
		if (pageData != null) {
			result.count(pageData.getTotalElements()).data(pageData.getContent());
		}
		result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
		return result;
	}


}
