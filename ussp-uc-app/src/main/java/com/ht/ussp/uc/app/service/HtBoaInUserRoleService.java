package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInUserRole;
import com.ht.ussp.uc.app.repository.HtBoaInUserRoleRepository;
/**
 * 
 * @ClassName: HtBoaInUserRoleService
 * @Description: 用户角色关联信息服务层
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月10日 下午10:22:09
 */
@Service
public class HtBoaInUserRoleService {
	@Autowired
	private HtBoaInUserRoleRepository htBoaInUserRoleRepository;
	
	
	public List<String> queryRoleCodes(String userId) {
		return htBoaInUserRoleRepository.queryRoleCodes(userId);
	}
	
	public HtBoaInUserRole findById(Long id) {
		return this.htBoaInUserRoleRepository.findById(id);
	}

	public List<HtBoaInUserRole> findAll(HtBoaInUserRole u) {
		ExampleMatcher matcher = ExampleMatcher.matching();
		Example<HtBoaInUserRole> ex = Example.of(u, matcher);
		return this.htBoaInUserRoleRepository.findAll(ex);
	}

	public HtBoaInUserRole add(HtBoaInUserRole u) {
		return this.htBoaInUserRoleRepository.saveAndFlush(u);
	}

	public List<HtBoaInUserRole> add(List<HtBoaInUserRole> u) {
		return this.htBoaInUserRoleRepository.save(u);
	}

	public HtBoaInUserRole update(HtBoaInUserRole u) {
		return this.htBoaInUserRoleRepository.save(u);
	}

	public void delete(HtBoaInUserRole u) {
		this.htBoaInUserRoleRepository.delete(u);
	}
}
