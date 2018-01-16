package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaOutUserRole;
import com.ht.ussp.uc.app.repository.HtBoaOutUserRoleRepository;

@Service
public class HtBoaOutUserRoleService {

    @Autowired
    private HtBoaOutUserRoleRepository htBoaOutUserRoleRepository;

    public HtBoaOutUserRole findById(Long id) {
        return this.htBoaOutUserRoleRepository.findById(id);
    }

    public List<HtBoaOutUserRole> findAll(HtBoaOutUserRole u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaOutUserRole> ex = Example.of(u, matcher);
        return this.htBoaOutUserRoleRepository.findAll(ex);
    }

    public HtBoaOutUserRole add(HtBoaOutUserRole u) {
        return this.htBoaOutUserRoleRepository.saveAndFlush(u);
    }

    public List<HtBoaOutUserRole> add(List<HtBoaOutUserRole> u) {
        return this.htBoaOutUserRoleRepository.save(u);
    }

    public HtBoaOutUserRole update(HtBoaOutUserRole u) {
        return this.htBoaOutUserRoleRepository.save(u);
    }
    
    public void delete(HtBoaOutUserRole u) {
        this.htBoaOutUserRoleRepository.delete(u);
    }

}
