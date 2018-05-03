package com.ht.ussp.uc.app.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Data;


 
@Entity
@Data
@Table(name="tb_user_role")
@IdClass(TbUserRoleMultiKeysClass.class)
@NamedQuery(name="TbUserRole.findAll", query="SELECT h FROM TbUserRole h")
public class TbUserRole implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="USER_ID")
	private String userId;
	
	@Id
	@Column(name="ROLE_ID")
	private String roleId;
	
	@Id
	@Column(name="dept_id")
	private String deptId;
	
	
}