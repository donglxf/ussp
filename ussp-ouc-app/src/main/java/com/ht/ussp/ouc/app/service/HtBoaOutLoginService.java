package com.ht.ussp.ouc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht.ussp.ouc.app.domain.HtBoaInApp;
import com.ht.ussp.ouc.app.domain.HtBoaOutLogin;
import com.ht.ussp.ouc.app.repository.HtBoaInAppRepository;
import com.ht.ussp.ouc.app.repository.HtBoaOutLoginRepository;

@Service
public class HtBoaOutLoginService {
	@Autowired
	private HtBoaOutLoginRepository htBoaOutLoginRepository;
	
	@Autowired
	private HtBoaInAppRepository htBoaInAppRepository;

	public HtBoaOutLogin saveUserLogin(HtBoaOutLogin htBoaOutLogin) {
		return htBoaOutLoginRepository.save(htBoaOutLogin);		
	}

	public HtBoaOutLogin findByUserId(String userId) {
		return htBoaOutLoginRepository.findByUserId(userId);
	}

	public HtBoaOutLogin findByLoginId(String loginId) {
		return htBoaOutLoginRepository.findByLoginId(loginId);
	}

	public void delete(HtBoaOutLogin u) {
		htBoaOutLoginRepository.delete(u);
	}

	public List<HtBoaInApp> getHtBoaInApp(String app) {
		return htBoaInAppRepository.findByApp(app);
	}
 
}
