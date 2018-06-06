package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
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

    public HtBoaInServiceCall findByMainServiceCodeAndCallService(String mainServiceCode,String callServiceCode) {
    	List<HtBoaInServiceCall>  listHtBoaInServiceCall = this.htBoaInServiceCallRepository.findByMainServiceCodeAndCallServiceCode(mainServiceCode,callServiceCode);
    	if(listHtBoaInServiceCall!=null&&!listHtBoaInServiceCall.isEmpty()) {
    		return listHtBoaInServiceCall.get(0);
    	}else {
    		return null;
    	}
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

	public PageResult findAllByPage(PageConf pageConf, Map<String, String> query) {
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
		if (null != pageConf.getPage() && null != pageConf.getSize()) {
			pageable = new PageRequest(pageConf.getPage(), pageConf.getSize(), sort);
		}
		String serviceCode = "";
		String keyword = "";
		if(query!=null) {
			serviceCode = query.get("serviceCode");
			keyword = query.get("keyWord");
		}
		if(StringUtils.isEmpty(keyword)) {
			keyword = "%%";
		}else {
			keyword = "%"+keyword+"%";
		}
		
		Page<BoaInServiceInfo> pageData = null;
		if (null != pageable) {
			pageData = this.htBoaInServiceCallRepository.listBoaInServiceInfoByPageWeb(pageable, serviceCode,keyword);
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
