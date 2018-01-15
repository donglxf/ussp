package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInPositionUser;
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
	
	public HtBoaInPositionUser findById(Long id) {
		return this.htBoaInPositionUserRepository.findById(id);
	}

	public List<HtBoaInPositionUser> findAll(HtBoaInPositionUser u) {
		ExampleMatcher matcher = ExampleMatcher.matching();
		Example<HtBoaInPositionUser> ex = Example.of(u, matcher);
		return this.htBoaInPositionUserRepository.findAll(ex);
	}

	public HtBoaInPositionUser add(HtBoaInPositionUser u) {
		return this.htBoaInPositionUserRepository.saveAndFlush(u);
	}

	public List<HtBoaInPositionUser> add(List<HtBoaInPositionUser> u) {
		return this.htBoaInPositionUserRepository.save(u);
	}

	public HtBoaInPositionUser update(HtBoaInPositionUser u) {
		return this.htBoaInPositionUserRepository.save(u);
	}

	public void delete(HtBoaInPositionUser u) {
		this.htBoaInPositionUserRepository.delete(u);
	}

}
