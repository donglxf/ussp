package com.ht.ussp.uc.app.service;

import java.util.List;

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
	 * @Description: 验证用户 
	 * @return HtBoaInUserApp
	 * @throws
	 */
	public String findUserAndAppInfo(String userId,String app) {
		try {
		List<HtBoaInUserApp> htBoaInUserApp = htBoaInUserAppRepository.findByuserId(userId);
		if (htBoaInUserApp.isEmpty()) {
			return null;
		}
		if (htBoaInUserApp.size() > 0) {
			for(int i=0;i<htBoaInUserApp.size();i++) {
				if(app.equals(htBoaInUserApp.get(i).getApp())) {
					return htBoaInUserApp.get(i).getController();
				}
			}
		}

		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
}
