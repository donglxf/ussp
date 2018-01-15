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

import com.ht.ussp.uc.app.domain.HtBoaOutRole;
import com.ht.ussp.uc.app.model.BoaOutRoleInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaOutRoleRepository;

@Service
public class HtBoaOutRoleService {

    @Autowired
    private HtBoaOutRoleRepository htBoaOutRoleRepository;

    public HtBoaOutRole findById(Long id) {
        return this.htBoaOutRoleRepository.findById(id);
    }

    public List<HtBoaOutRole> findAll(HtBoaOutRole u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaOutRole> ex = Example.of(u, matcher);
        return this.htBoaOutRoleRepository.findAll(ex);
    }
    
    public List<HtBoaOutRole> findByRoleCodeIn(Set<String> roleCodes) {
        return this.htBoaOutRoleRepository.findByRoleCodeIn(roleCodes);
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
            Page<BoaOutRoleInfo> p = this.htBoaOutRoleRepository.listRoleInfo(pageable, search);
            for (BoaOutRoleInfo u : p.getContent()) {
                u.setUsers(this.htBoaOutRoleRepository.listHtBoaOutUser(u.getRoleCode()));
            }
            return p;
        } else {
            List<BoaOutRoleInfo> p = this.htBoaOutRoleRepository.listRoleInfo(search);
            for (BoaOutRoleInfo u : p) {
                u.setUsers(this.htBoaOutRoleRepository.listHtBoaOutUser(u.getRoleCode()));
            }
            return p;
        }
    }

    public HtBoaOutRole add(HtBoaOutRole u) {
        return this.htBoaOutRoleRepository.saveAndFlush(u);
    }

    public HtBoaOutRole update(HtBoaOutRole u) {
        return this.htBoaOutRoleRepository.save(u);
    }
    
    public void delete(Set<String> roleCodes) {
        this.htBoaOutRoleRepository.delete(roleCodes);
    }
    
}
