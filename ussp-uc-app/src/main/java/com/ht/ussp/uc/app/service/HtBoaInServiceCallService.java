package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInServiceCall;
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

    public HtBoaInServiceCall update(HtBoaInServiceCall u) {
        return this.htBoaInServiceCallRepository.save(u);
    }
    
    public void delete(long id) {
        this.htBoaInServiceCallRepository.delete(id);
    }
 


}
