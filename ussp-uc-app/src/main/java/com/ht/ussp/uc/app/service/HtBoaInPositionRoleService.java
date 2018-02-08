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
import com.ht.ussp.uc.app.domain.HtBoaInPositionRole;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInPositionRoleRepository;


@Service
public class HtBoaInPositionRoleService {
	@Autowired
	private HtBoaInPositionRoleRepository htBoaInPositionRoleRepository;
	
	/**
	 * 
	  * @Title: queryRoleCodesByPosition 
	  * @Description: 通过岗位编码查找角色编码 
	  * @return List<String>
	  * @throws
	 */
	public List<String> queryRoleCodesByPosition(List<String> positionCodes){
		return htBoaInPositionRoleRepository.queryRoleCodeByPosition(positionCodes);
	}
	
	public HtBoaInPositionRole findById(Long id) {
		return this.htBoaInPositionRoleRepository.findById(id);
	}

	public List<HtBoaInPositionRole> findAll(HtBoaInPositionRole u) {
		ExampleMatcher matcher = ExampleMatcher.matching();
		Example<HtBoaInPositionRole> ex = Example.of(u, matcher);
		return this.htBoaInPositionRoleRepository.findAll(ex);
	}

	public HtBoaInPositionRole add(HtBoaInPositionRole u) {
		return this.htBoaInPositionRoleRepository.saveAndFlush(u);
	}

	public HtBoaInPositionRole update(HtBoaInPositionRole u) {
		return this.htBoaInPositionRoleRepository.save(u);
	}

	public void delete(String positionCode, String roleCode) {
		this.htBoaInPositionRoleRepository.delete(positionCode, roleCode);
	}
	
	public void delete(HtBoaInPositionRole u) {
		this.htBoaInPositionRoleRepository.delete(u);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PageResult listPositionRoleByPage(PageConf pageConf, Map<String, String> query) {
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
        String positionCode = "";
        if (query != null && query.size() > 0 && query.get("positionCode") != null) {
        	positionCode =  query.get("positionCode") ;
        }
        
        if (query != null && query.size() > 0 && query.get("search") != null) {
        	search =  query.get("keyWord") ;
        }
        
        if (null == search || 0 == search.trim().length())
            search = "%%";
        else
            search = "%" + search + "%";
         
        Page<HtBoaInPositionRole> pageData = this.htBoaInPositionRoleRepository.listPositionRoleByPageWeb(pageable, search,positionCode);
		
        if (pageData != null) {
            result.count(pageData.getTotalElements()).data(pageData.getContent());
        }
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
		return result;
	}

	public List<HtBoaInPositionRole> getPositionRoleList(String roleCode, String positionCode) {
		return this.htBoaInPositionRoleRepository.getPositionRoleList(roleCode,positionCode);
	}

	public void deleteByRoleCode(String roleCode) {
		this.htBoaInPositionRoleRepository.deleteByRoleCode(roleCode);
	}
	
}
