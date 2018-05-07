package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInPublish;
import com.ht.ussp.uc.app.repository.HtBoaInPublishRepository;

@Service
public class HtBoaInPublishService {

	@Autowired
    private HtBoaInPublishRepository htBoaInPublishRepository;
	
	
	public List<HtBoaInPublish> getHtBoaInPublishListByPublishCode(String publishCode){
		return htBoaInPublishRepository.findByPublishCode(publishCode);
	}

	public HtBoaInPublish save(HtBoaInPublish u){
		return htBoaInPublishRepository.save(u);
	}
}
