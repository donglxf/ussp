package com.ht.ussp.uc.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInApp;
import com.ht.ussp.uc.app.repository.HtBoaInAppRepository;

@Service
public class HtBoaInAppService {
	@Autowired
	private HtBoaInAppRepository htBoaInAppRepository;
	
	public HtBoaInApp findById(Long id) {
		return htBoaInAppRepository.findById(id);
	}
}
