package com.ht.ussp.ouc.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht.ussp.ouc.app.domain.HtBoaOutClientDetail;
import com.ht.ussp.ouc.app.repository.HtBoaOutClientDetailRepository;

@Service
public class HtBoaOutClientDetailService {
	@Autowired
	private HtBoaOutClientDetailRepository htBoaOutClientDetailRepository;
	
	public HtBoaOutClientDetail findByAppCode(String appCode) {
		return htBoaOutClientDetailRepository.findByAppCode(appCode);
	}
}
