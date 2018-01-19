package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.uc.app.domain.HtBoaInUserRole;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInPositionRoleRepository;
import com.ht.ussp.uc.app.repository.HtBoaInPositionUserRepository;
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
	
	@Autowired
	private HtBoaInPositionUserRepository htBoaInPositionUserRepository;
	
	@Autowired
	private HtBoaInPositionRoleRepository htBoaInPositionRoleRepository;
	
	
	
	/**
	 * 
	 * @Title: queryRoleCodes 
	 * @Description: 通过用户ID查找角色编码 
	 * @return List<String>
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年1月18日 下午9:32:06
	 */
	public List<String> queryRoleCodes(String userId) {
		return htBoaInUserRoleRepository.queryRoleCodes(userId);
	}
	/**
	 * 
	 * @Title: getAllRoleCodes 
	 * @Description: 查询非管理员的所有角色编码 
	 * @return List<String>
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年1月18日 下午9:40:12
	 */
	public List<String> getAllRoleCodes(String userId) {
		List<String> allRoleCodes = new ArrayList<>();
		// 查找当前用户的角色编码
		List<String> userRoleCodes = queryRoleCodes(userId);
		if (!userRoleCodes.isEmpty()) {
			userRoleCodes.forEach(userRoleCode -> {
				allRoleCodes.add(userRoleCode);
			});
		}
		// 查找当前用户岗位编码
		List<String> positionCodes = htBoaInPositionUserRepository.queryPositionCodes(userId);

		// 通过岗位编码查询关联的角色编码
		if (positionCodes != null && positionCodes.size() > 0) {
			List<String> userRoleCodesByPosition = htBoaInPositionRoleRepository.queryRoleCodeByPosition(positionCodes);
			if (!userRoleCodesByPosition.isEmpty()) {
				userRoleCodesByPosition.forEach(userRoleCode -> {
					allRoleCodes.add(userRoleCode);
				});
			}
		}
			return allRoleCodes;
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

	public PageResult listUserRoleByPage(PageConf pageConf, Map<String, String> query) {
		PageResult result = new PageResult();
		return null;
	}
}
