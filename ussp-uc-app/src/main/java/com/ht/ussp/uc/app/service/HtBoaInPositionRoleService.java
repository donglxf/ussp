package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
