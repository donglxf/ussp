package com.ht.ussp.uc.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInUserApp;
import com.ht.ussp.uc.app.repository.HtBoaInUserAppRepository;

/**
 * 
 * @ClassName: HtBoaInUserAppService
 * @Description: 用户与系统关联业务处理
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月9日 下午6:13:03
 */
@Service
public class HtBoaInUserAppService {
	@Autowired
	private HtBoaInUserAppRepository htBoaInUserAppRepository;
	
	/**
	 * 
	 * @Title: findUserAndAppInfo 
	 * @Description: 通过用户ID查找用户与系统关联信息 
	 * @return HtBoaInUserApp
	 * @throws
	 */
	public HtBoaInUserApp findUserAndAppInfo(String userId) {
		HtBoaInUserApp htBoaInUserApp= htBoaInUserAppRepository.findByuserId(userId);
		return htBoaInUserApp;
	}
}
