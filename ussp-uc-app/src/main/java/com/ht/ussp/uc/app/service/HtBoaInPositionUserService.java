package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.repository.HtBoaInPositionUserRepository;

@Service
public class HtBoaInPositionUserService {
	@Autowired
	private HtBoaInPositionUserRepository htBoaInPositionUserRepository;
	
	/**
	 * 
	  * @Title: queryRoleCodes 
	  * @Description: 查找当前用户的岗位编码 
	  * @return List<String>
	  * @throws
	 */
	public List<String> queryRoleCodes(String userId){
		return htBoaInPositionUserRepository.queryPositionCodes(userId);
	}
}
