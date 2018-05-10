package com.ht.ussp.ouc.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht.ussp.ouc.app.domain.HtBoaOutLogin;
import com.ht.ussp.ouc.app.repository.HtBoaOutLoginRepository;

@Service
public class HtBoaOutLoginService {
	@Autowired
	private HtBoaOutLoginRepository htBoaOutLoginRepository;

	public HtBoaOutLogin saveUserLogin(HtBoaOutLogin htBoaOutLogin) {
		return htBoaOutLoginRepository.save(htBoaOutLogin);		
	}
}
