package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.repository.HtBoaInLoginRepository;

@Service
public class HtBoaInLoginService {
	@Autowired
	private HtBoaInLoginRepository htBoaInLoginRepository;
	
	public HtBoaInLogin findByUserId(String userId) {
		
		return htBoaInLoginRepository.findByUserId(userId);
	}
	
	public List<HtBoaInLogin> findAll(HtBoaInLogin u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaInLogin> ex = Example.of(u, matcher);
        return this.htBoaInLoginRepository.findAll(ex);
    }

    public HtBoaInLogin add(HtBoaInLogin u) {
        return this.htBoaInLoginRepository.saveAndFlush(u);
    }
    
    public HtBoaInLogin update(HtBoaInLogin u) {
        return this.htBoaInLoginRepository.save(u);
    }
}
