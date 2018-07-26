package com.ht.ussp.uc.app.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 * @ClassName: RoleDTO
 * @Description: 角色信息DTO
 * @author wim qiuwenwu@hongte.info
 * @date 2018年7月23日 下午6:27:38
 */
@Data
public class RoleDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String roleCode;
	
	private String roleName;

}
