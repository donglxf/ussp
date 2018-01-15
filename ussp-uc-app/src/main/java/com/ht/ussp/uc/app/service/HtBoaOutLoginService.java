package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaOutLogin;
import com.ht.ussp.uc.app.repository.HtBoaOutLoginRepository;

@Service
public class HtBoaOutLoginService {

    @Autowired
    private HtBoaOutLoginRepository htBoaOutLoginRepository;

    public HtBoaOutLogin findById(Long id) {
        return this.htBoaOutLoginRepository.findById(id);
    }

    public List<HtBoaOutLogin> findAll(HtBoaOutLogin u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaOutLogin> ex = Example.of(u, matcher);
        return this.htBoaOutLoginRepository.findAll(ex);
    }

    public HtBoaOutLogin add(HtBoaOutLogin u) {
        return this.htBoaOutLoginRepository.saveAndFlush(u);
    }
    
    public HtBoaOutLogin update(HtBoaOutLogin u) {
        return this.htBoaOutLoginRepository.save(u);
    }

}
