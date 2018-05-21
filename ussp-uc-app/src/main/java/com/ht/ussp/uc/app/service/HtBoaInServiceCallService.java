package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInServiceCall;
import com.ht.ussp.uc.app.model.BoaInServiceInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInServiceCallRepository;

@Service
public class HtBoaInServiceCallService {

	@Autowired
    private HtBoaInServiceCallRepository htBoaInServiceCallRepository;
	
    public HtBoaInServiceCall findById(Long id) {
        return this.htBoaInServiceCallRepository.getOne(id);
    }

    public List<HtBoaInServiceCall> findAll(HtBoaInServiceCall u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaInServiceCall> ex = Example.of(u, matcher);
        return this.htBoaInServiceCallRepository.findAll(ex);
    }
    
    public HtBoaInServiceCall add(HtBoaInServiceCall u) {
        return this.htBoaInServiceCallRepository.saveAndFlush(u);
    }
    
    public List<HtBoaInServiceCall> addList(List<HtBoaInServiceCall> u) {
        return this.htBoaInServiceCallRepository.save(u);
    }

    public HtBoaInServiceCall update(HtBoaInServiceCall u) {
        return this.htBoaInServiceCallRepository.save(u);
    }
    
    public void delete(long id) {
        this.htBoaInServiceCallRepository.delete(id);
    }

	public PageResult<List<HtBoaInServiceCall>> findAllByPage(PageRequest pageRequest, Map<String, String> query) {
		PageResult result = new PageResult();
		Page<HtBoaInServiceCall> pageData = null;
		if(query!=null) {
			Specification<HtBoaInServiceCall> specification = (root, query1, cb) -> {
				Predicate p1 = cb.like(root.get("mainServiceCode").as(String.class), "%" + query.get("serviceCode") + "%");
				Predicate p2 = cb.like(root.get("callService").as(String.class), "%" + query.get("serviceCode") + "%");
				Predicate p3 = cb.equal(root.get("app").as(String.class), query.get("app"));
				query1.where(cb.and(cb.or(p1, p2), p3));
				return query1.getRestriction();
			};
			  pageData = htBoaInServiceCallRepository.findAll(specification, pageRequest);
		}else {
			Specification<HtBoaInServiceCall> specification = (root, query1, cb) -> {
				Predicate p1 = cb.like(root.get("mainServiceCode").as(String.class), "%%");
				Predicate p2 = cb.like(root.get("callService").as(String.class), "%%");
				query1.where(cb.or(p1, p2));
				return query1.getRestriction();
			};
			  pageData = htBoaInServiceCallRepository.findAll(specification, pageRequest);
		}
		
		if (pageData != null) {
			result.count(pageData.getTotalElements()).data(pageData.getContent());
		}
		result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
		return result;
	}
	
	public PageResult findAllByPage2(PageConf pageConf, Map<String, String> query) {
		PageResult result = new PageResult();
		Sort sort = null;
		Pageable pageable = null;
		List<Order> orders = new ArrayList<Order>();
		if (null != pageConf.getSortNames()) {
			for (int i = 0; i < pageConf.getSortNames().size(); i++) {
				orders.add(new Order(pageConf.getSortOrders().get(i), pageConf.getSortNames().get(i)));
			}
			sort = new Sort(orders);
		}
		if (null != pageConf.getPage() && null != pageConf.getSize())
			pageable = new PageRequest(pageConf.getPage(), pageConf.getSize(), sort);

		String search = pageConf.getSearch();
		Page<BoaInServiceInfo> pageData = null;
		if (null == search || 0 == search.trim().length())
			search = "%%";
		else
			search = "%" + search + "%";
		if (null != pageable) {
			pageData = this.htBoaInServiceCallRepository.listBoaInServiceInfoByPageWeb(pageable, search);
		}
		if (pageData != null) {
			result.count(pageData.getTotalElements()).data(pageData.getContent());
		}
		result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
		return result;
	}
 
	/**
	 * 获取可调用的微服务
	 * @param u
	 * @return
	 */
	public List<HtBoaInServiceCall> findByMainServiceCode(String mainServiceCode) {
	        return this.htBoaInServiceCallRepository.findByMainServiceCode(mainServiceCode);
	}


}
