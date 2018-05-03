package com.ht.ussp.uc.app.domain;

import java.io.Serializable;

import lombok.Data;


 
/**
 * 的复合主键类
 *
 * @Param userId
 * @Param roleId
 * @Param deptId
 * 由这三个共同组成复合主键
 */
@Data
public class TbUserRoleMultiKeysClass implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userId;

	private String roleId;
	
	private String deptId;
	
}