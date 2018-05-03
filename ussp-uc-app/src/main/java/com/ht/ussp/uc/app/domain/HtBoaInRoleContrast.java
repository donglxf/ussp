package com.ht.ussp.uc.app.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table(name="ht_boa_in_role_contrast")
@NamedQuery(name="HtBoaInRoleContrast.findAll", query="SELECT h FROM HtBoaInRoleContrast h")
public class HtBoaInRoleContrast implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="bm_role_code")
	private String bmRoleCode;
	
	@Column(name="bm_role_name")
	private String bmRoleName;
	
	@Column(name="bm_role_desc")
	private String bmRoleDesc;
	
	@Column(name="uc_role_code")
	private String ucRoleCode;
	
	@Column(name="busines_type_code")
	private String businesTypeode;
	
	@Column(name="remark")
	private String remark;
	
	@Column(name="status")
	private Integer status;
	
	@Column(name="sequence")
	private Integer sequence;
	
	@Column(name="create_operator")
	private String createOperator;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_datetime")
	private Date createdDatetime;

	@Column(name="update_operator")
	private String updateOperator;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="update_datetime")
	private Date updateDatetime;
	
}