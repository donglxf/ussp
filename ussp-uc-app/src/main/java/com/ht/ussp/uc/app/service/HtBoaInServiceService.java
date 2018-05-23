package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
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
import com.ht.ussp.uc.app.vo.AppAndServiceVo;

@Service
public class HtBoaInServiceService {

	@Autowired
    private HtBoaInServiceRepository htBoaInServiceRepository;
	
    public HtBoaInService findById(Long id) {
        return this.htBoaInServiceRepository.getOne(id);
    }
    
    public List<HtBoaInService> findByApplicationServiceAndAppAndStatus(String applicationService,String app,String status) {
        return this.htBoaInServiceRepository.findByApplicationServiceAndAppAndStatus(applicationService,app,status);
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

 
	public PageResult<List<HtBoaInService>> getServiceListByPage(PageRequest pageRequest, Map<String, String> query) {
		PageResult result = new PageResult();
		Page<HtBoaInService> pageData = null;
////      Join<HtBoaInUser, HtBoaInLogin> join = root.join("htBoaInLogin", JoinType.LEFT);
////      Predicate p5 = cb.equal(join.get("userId").as(String.class), root.get("userId").as(String.class));
//      //把Predicate应用到CriteriaQuery中去,因为还可以给CriteriaQuery添加其他的功能，比如排序、分组啥的
//      query1.where(cb.and(cb.or(p1, p2, p3), p4));
		if(query!=null) {
			String keyWord=query.get("keyWord")==null?"":query.get("keyWord");
			String app=query.get("app");
			Specification<HtBoaInService> specification = null;
			if(StringUtils.isEmpty(app)) {
				specification = (root, query1, cb) -> {
					Predicate p1 = cb.like(root.get("applicationServiceName").as(String.class), "%" + keyWord + "%");
					Predicate p2 = cb.like(root.get("applicationService").as(String.class), "%" + keyWord + "%");
					Predicate p3 = cb.like(root.get("app").as(String.class), "%" + keyWord + "%");
					query1.where(cb.or(p1, p2,p3));
					//query1.orderBy(cb.desc(root.get("app").as(String.class)));
					return query1.getRestriction();
				};
			}else {
				specification = (root, query1, cb) -> {
					Predicate p1 = cb.like(root.get("applicationServiceName").as(String.class), "%" + keyWord + "%");
					Predicate p2 = cb.like(root.get("applicationService").as(String.class), "%" + keyWord + "%");
					Predicate p3 = cb.equal(root.get("app").as(String.class), query.get("app"));
					query1.where(cb.and(cb.or(p1, p2), p3));
					//query1.orderBy(cb.desc(root.get("app").as(String.class)));
					return query1.getRestriction();
				};
			}
			  pageData = htBoaInServiceRepository.findAll(specification, pageRequest);
		}else {
			  pageData = htBoaInServiceRepository.findAll(pageRequest);
		}
		
		if (pageData != null) {
			result.count(pageData.getTotalElements()).data(pageData.getContent());
		}
		result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
		return result;
	}

	public List<AppAndServiceVo> loadAppAndServiceVoList() {
		 List<Object[]> list = htBoaInServiceRepository.queryAppAndServiceTree();
	        List<AppAndServiceVo> aaaList = new ArrayList<>();
	        AppAndServiceVo aaa;
	        for (Object[] objects : list) {
	            aaa = new AppAndServiceVo(objects[0], objects[1], objects[2], objects[3], objects[4]);
	            aaaList.add(aaa);
	        }
	        return aaaList;
	}

}
