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

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInPositionUser;
import com.ht.ussp.uc.app.model.BoaInPositionInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInPositionUserRepository;

@Service
public class HtBoaInPositionUserService {
	@Autowired
	private HtBoaInPositionUserRepository htBoaInPositionUserRepository;
	
	/**
	 * 
	  * @Title: queryRoleCodes 
	  * @Description: 查找当前用户的岗位编码 
	  * @return List<String>
	  * @throws
	 */
	public List<String> queryRoleCodes(String userId){
		return htBoaInPositionUserRepository.queryPositionCodes(userId);
	}
	
	public HtBoaInPositionUser findById(Long id) {
		return this.htBoaInPositionUserRepository.findById(id);
	}

	public List<HtBoaInPositionUser> findAll(HtBoaInPositionUser u) {
		ExampleMatcher matcher = ExampleMatcher.matching();
		Example<HtBoaInPositionUser> ex = Example.of(u, matcher);
		return this.htBoaInPositionUserRepository.findAll(ex);
	}

	public HtBoaInPositionUser add(HtBoaInPositionUser u) {
		return this.htBoaInPositionUserRepository.saveAndFlush(u);
	}

	public List<HtBoaInPositionUser> add(List<HtBoaInPositionUser> u) {
		return this.htBoaInPositionUserRepository.save(u);
	}

	public HtBoaInPositionUser update(HtBoaInPositionUser u) {
		return this.htBoaInPositionUserRepository.save(u);
	}

	public void delete(HtBoaInPositionUser u) {
		this.htBoaInPositionUserRepository.delete(u);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PageResult listPositionUserByPage(PageConf pageConf, Map<String, String> query) {
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
        
        String search = "";
        String userId = "";
        if (query != null && query.size() > 0 && query.get("userId") != null) {
        	userId =  query.get("userId") ;
        }
        
        if (query != null && query.size() > 0 && query.get("userId") != null) {
        	search =  query.get("keyWord") ;
        }
        
        if (null == search || 0 == search.trim().length())
            search = "%%";
        else
            search = "%" + search + "%";
         
        Page<BoaInPositionInfo> pageData = this.htBoaInPositionUserRepository.listPositionUserByPage(pageable, search,userId);
		
        if (pageData != null) {
            result.count(pageData.getTotalElements()).data(pageData.getContent());
        }
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
		return result;
	}

	public List<BoaInPositionInfo> getPositionUser(String positionCode, String userId) {
		return this.htBoaInPositionUserRepository.getPositionUser(positionCode,userId);
	}
}
