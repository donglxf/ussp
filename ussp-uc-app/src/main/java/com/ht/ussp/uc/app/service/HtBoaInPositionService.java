package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInPosition;
import com.ht.ussp.uc.app.model.BoaInPositionInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInPositionRepository;

@Service
public class HtBoaInPositionService {

    @Autowired
    private HtBoaInPositionRepository htBoaInPositionRepository;

    public HtBoaInPosition findById(Long id) {
        return this.htBoaInPositionRepository.findById(id);
    }

    public List<HtBoaInPosition> findAll(HtBoaInPosition u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaInPosition> ex = Example.of(u, matcher);
        return this.htBoaInPositionRepository.findAll(ex);
    }
    
    public List<HtBoaInPosition> findByPositionCodeIn(Set<String> positionCodes) {
        return this.htBoaInPositionRepository.findByPositionCodeIn(positionCodes);
    }
    
    public Object findAllByPage(PageConf pageConf,Map<String, String> query) {
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
            pageable = new PageRequest(pageConf.getPage(), pageConf.getSize(),  sort);
        String search = "";
        String orgPath = "";
        if (query != null && query.size() > 0 && query.get("orgCode") != null) {
        	orgPath = "%" +query.get("orgCode")+ "%";
        }
        if (query != null && query.size() > 0 && query.get("keyWord") != null) {
        	search = "%" +query.get("keyWord")+ "%";
        }
        
        if (null == search || 0 == search.trim().length())
            search = "%%";
    
        
        if (null != pageable) {
            Page<BoaInPositionInfo> p = this.htBoaInPositionRepository.listPositionByPageWeb(pageable, search,orgPath);
            return p;
        } else {
            List<BoaInPositionInfo> p = this.htBoaInPositionRepository.listPositionInfo(search);
          
            return p;
        }
    }

    public HtBoaInPosition add(HtBoaInPosition u) {
        return this.htBoaInPositionRepository.saveAndFlush(u);
    }

    public HtBoaInPosition update(HtBoaInPosition u) {
        return this.htBoaInPositionRepository.save(u);
    }
    
    public void delete(long id) {
        this.htBoaInPositionRepository.delete(id);;
    }

	public List<HtBoaInPosition> findByPositionCode(String positionCode) {
		return this.htBoaInPositionRepository.findByPositionCode(positionCode);
	}

}
