package com.ht.ussp.uc.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.repository.HtBoaInUserRepository;

/**
 * 
 * @ClassName: HtBoaInUserService
 * @Description: 用户信息服务层
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月8日 下午9:47:25
 */

@Service
public class HtBoaInUserService {
	@Autowired
	private HtBoaInUserRepository htBoaInUserRepository;
	
	public HtBoaInUser findByUserName(String userName) {
		
		return htBoaInUserRepository.findByUserName(userName);
	}
}
