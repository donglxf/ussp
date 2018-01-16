package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInPositionRole;
import com.ht.ussp.uc.app.repository.HtBoaInPositionRoleRepository;


@Service
public class HtBoaInPositionRoleService {
	@Autowired
	private HtBoaInPositionRoleRepository htBoaInPositionRoleRepository;
	
	/**
	 * 
	  * @Title: queryRoleCodesByPosition 
	  * @Description: 通过岗位编码查找角色编码 
	  * @return List<String>
	  * @throws
	 */
	public List<String> queryRoleCodesByPosition(List<String> positionCodes){
		return htBoaInPositionRoleRepository.queryRoleCodeByPosition(positionCodes);
	}
	
	public HtBoaInPositionRole findById(Long id) {
		return this.htBoaInPositionRoleRepository.findById(id);
	}

	public List<HtBoaInPositionRole> findAll(HtBoaInPositionRole u) {
		ExampleMatcher matcher = ExampleMatcher.matching();
		Example<HtBoaInPositionRole> ex = Example.of(u, matcher);
		return this.htBoaInPositionRoleRepository.findAll(ex);
	}

	public HtBoaInPositionRole add(HtBoaInPositionRole u) {
		return this.htBoaInPositionRoleRepository.saveAndFlush(u);
	}

	public HtBoaInPositionRole update(HtBoaInPositionRole u) {
		return this.htBoaInPositionRoleRepository.save(u);
	}

	public void delete(String positionCode, String roleCode) {
		this.htBoaInPositionRoleRepository.delete(positionCode, roleCode);
	}
}
