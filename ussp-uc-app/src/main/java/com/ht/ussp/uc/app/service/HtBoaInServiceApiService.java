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

	public PageResult findAllByPage(PageRequest pageRequest, Map<String, String> query) {
		PageResult result = new PageResult();
		Page<HtBoaInServiceApi> pageData = null;
		if(query!=null) {
			String keyWord = query.get("keyWord")==null?"":query.get("keyWord");
			Specification<HtBoaInServiceApi> specification = (root, query1, cb) -> {
				Predicate p1 = cb.like(root.get("apiContent").as(String.class), "%" + keyWord + "%");
				Predicate p2 = cb.equal(root.get("authServiceCode").as(String.class), query.get("authServiceCode"));
				query1.where(cb.and(p1, p2));
				return query1.getRestriction();
			};
			pageData = htBoaInServiceApiRepository.findAll(specification, pageRequest);
		}else {
			 // pageData = htBoaInServiceApiRepository.findAll(pageRequest);
		}
		
		if (pageData != null) {
			result.count(pageData.getTotalElements()).data(pageData.getContent());
		}
		result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
		return result;
	}

	public List<HtBoaInServiceApi> getApiByAuthServiceCodeAndApiContentAndStatus(String authServiceCode, String mainApi, String status) {
		return htBoaInServiceApiRepository.findByAuthServiceCodeAndApiContentAndStatus(authServiceCode,mainApi,status);
	}
 


}
