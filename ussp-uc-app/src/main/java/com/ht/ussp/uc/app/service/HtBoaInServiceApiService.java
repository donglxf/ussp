package com.ht.ussp.uc.app.service;

import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Predicate;

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
import com.ht.ussp.uc.app.domain.HtBoaInServiceApi;
import com.ht.ussp.uc.app.repository.HtBoaInServiceApiRepository;

@Service
public class HtBoaInServiceApiService {

	@Autowired
    private HtBoaInServiceApiRepository htBoaInServiceApiRepository;
	
    public HtBoaInServiceApi findById(Long id) {
        return this.htBoaInServiceApiRepository.getOne(id);
    }

    public List<HtBoaInServiceApi> findAll(HtBoaInServiceApi u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaInServiceApi> ex = Example.of(u, matcher);
        return this.htBoaInServiceApiRepository.findAll(ex);
    }
    
    public HtBoaInServiceApi add(HtBoaInServiceApi u) {
        return this.htBoaInServiceApiRepository.saveAndFlush(u);
    }

    public HtBoaInServiceApi update(HtBoaInServiceApi u) {
        return this.htBoaInServiceApiRepository.save(u);
    }
    
    public void delete(long id) {
        this.htBoaInServiceApiRepository.delete(id);
    }

	public PageResult findAllByPage(PageRequest pageRequest, Map<String, String> query, String keyWord) {
		PageResult result = new PageResult();
		Page<HtBoaInServiceApi> pageData = null;
		if(query!=null) {
			Specification<HtBoaInServiceApi> specification = (root, query1, cb) -> {
				Predicate p1 = cb.like(root.get("apiContent").as(String.class), "%" + keyWord + "%");
				Predicate p2 = cb.like(root.get("apiContent").as(String.class), "%" + keyWord + "%");
				Predicate p3 = cb.equal(root.get("apiDesc").as(String.class), query.get("app"));
				query1.where(cb.and(cb.or(p1, p2), p3));
				return query1.getRestriction();
			};
			  pageData = htBoaInServiceApiRepository.findAll(specification, pageRequest);
		}else {
			Specification<HtBoaInServiceApi> specification = (root, query1, cb) -> {
				Predicate p1 = cb.like(root.get("apiContent").as(String.class), "%" + keyWord + "%");
				Predicate p2 = cb.like(root.get("apiDesc").as(String.class), "%" + keyWord + "%");
				query1.where(cb.or(p1, p2));
				return query1.getRestriction();
			};
			  pageData = htBoaInServiceApiRepository.findAll(specification, pageRequest);
		}
		
		if (pageData != null) {
			result.count(pageData.getTotalElements()).data(pageData.getContent());
		}
		result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
		return result;
	}
 


}
