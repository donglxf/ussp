package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.repository.HtBoaInRoleResRepository;

/**
 * 
 * @ClassName: HtBoaInRoleResService
 * @Description: 资源编码关系服务
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月15日 下午3:29:24
 */
@Service
public class HtBoaInRoleResService {

	@Autowired
	private HtBoaInRoleResRepository htBoaInRoleResRepository;

	/**
	 * 
	 * @Title: queryResByCode 
	 * @Description: 通过角色编码查询资源编码 
	 * @return List<String>
	 * @throws
	 */
	public List<String> queryResByCode(@Param("role_code") List<String> role_code) {

		return htBoaInRoleResRepository.queryResByCode(role_code);

	}
}
