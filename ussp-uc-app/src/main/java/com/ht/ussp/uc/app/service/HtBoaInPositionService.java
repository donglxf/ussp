package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.List;
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
    
    public Object findAllByPage(PageConf pageConf) {
        Sort sort = null;
        Pageable pageable = null;
        List<Order> orders = new ArrayList<Order>();
        if (null != pageConf.getSortNames()) {
            for (int i = 0; i < pageConf.getSortNames().size(); i++) {
                orders.add(new Order(pageConf.getSortOrders().get(i),
                        pageConf.getSortNames().get(i)));
            }
            sort = new Sort(orders);
        }
        if (null != pageConf.getPage() && null != pageConf.getSize())
            pageable = new PageRequest(pageConf.getPage(), pageConf.getSize(),
                    sort);
        String search = pageConf.getSearch();
        if (null == search || 0 == search.trim().length())
            search = "";
        else
            search = "%" + search + "%";
        if (null != pageable) {
            Page<BoaInPositionInfo> p = this.htBoaInPositionRepository.listPositionInfo(pageable, search);
            for (BoaInPositionInfo u : p.getContent()) {
                u.setUsers(this.htBoaInPositionRepository.listHtBoaInUser(u.getPositionCode()));
                u.setRoles(this.htBoaInPositionRepository.listHtBoaInRole(u.getPositionCode()));
            }
            return p;
        } else {
            List<BoaInPositionInfo> p = this.htBoaInPositionRepository.listPositionInfo(search);
            for (BoaInPositionInfo u : p) {
                u.setUsers(this.htBoaInPositionRepository.listHtBoaInUser(u.getPositionCode()));
                u.setRoles(this.htBoaInPositionRepository.listHtBoaInRole(u.getPositionCode()));
            }
            return p;
        }
    }

    public HtBoaInPosition add(HtBoaInPosition u) {
        return this.htBoaInPositionRepository.saveAndFlush(u);
    }

    public HtBoaInPosition update(HtBoaInPosition u) {
        return this.htBoaInPositionRepository.save(u);
    }
    
    public void delete(Set<String> positionCodes) {
        this.htBoaInPositionRepository.delete(positionCodes);
    }

}
