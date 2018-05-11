package com.ht.ussp.ouc.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht.ussp.ouc.app.domain.HtBoaOutUser;
import com.ht.ussp.ouc.app.repository.HtBoaOutUserRepository;

@Service
public class HtBoaOutUserService {
	@Autowired
	private HtBoaOutUserRepository htBoaOutUserRepository;

	public HtBoaOutUser saveUser(HtBoaOutUser htBoaOutUser) {
		return htBoaOutUserRepository.save(htBoaOutUser);
	}

	public HtBoaOutUser findByEmailOrMobile(String userName, String userName2) {
		return htBoaOutUserRepository.findByEmailOrMobile(userName,userName2);
	}

	public HtBoaOutUser findByEmail(String userName) {
		return htBoaOutUserRepository.findByEmail(userName);
	}
	
	public HtBoaOutUser findByMobile(String userName) {
		return htBoaOutUserRepository.findByMobile(userName);
	}
}
