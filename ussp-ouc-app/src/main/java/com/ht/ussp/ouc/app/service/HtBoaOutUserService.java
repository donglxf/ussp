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
}
