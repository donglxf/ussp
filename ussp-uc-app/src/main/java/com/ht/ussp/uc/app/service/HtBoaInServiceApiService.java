package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInServiceApi;
import com.ht.ussp.uc.app.repository.HtBoaInServiceApiRepository;

@Service
public class HtBoaInServiceApiService {

	@Autowired
    private HtBoaInServiceApiRepository htBoaInServiceApiRepository;
	
    public HtBoaInServiceApi findById(Long id) {
        return this.htBoaInServiceApiRepository.getOne(id);
    }

    public List<HtBoaInServiceApi> findAll(HtBoaInServiceApi u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaInServiceApi> ex = Example.of(u, matcher);
        return this.htBoaInServiceApiRepository.findAll(ex);
    }
    
    public HtBoaInServiceApi add(HtBoaInServiceApi u) {
        return this.htBoaInServiceApiRepository.saveAndFlush(u);
    }

    public HtBoaInServiceApi update(HtBoaInServiceApi u) {
        return this.htBoaInServiceApiRepository.save(u);
    }
    
    public void delete(long id) {
        this.htBoaInServiceApiRepository.delete(id);
    }
 


}
