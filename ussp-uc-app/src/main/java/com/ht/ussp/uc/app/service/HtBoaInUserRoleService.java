package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
