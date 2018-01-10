package com.ht.ussp.uc.app.service;

import org.springframework.beans.factory.annotation.Autowired;
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
}
