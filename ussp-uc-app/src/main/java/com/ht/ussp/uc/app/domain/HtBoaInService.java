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
@Table(name = "HT_BOA_IN_SERVICE")
@NamedQuery(name = "HtBoaInService.findAll", query = "SELECT h FROM HtBoaInService h")
public class HtBoaInService implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "service_code")
	private String serviceCode;
	
	@Column(name = "application_service")
	private String applicationService;
	
	@Column(name = "application_service_name")
	private String applicationServiceName;
	
	@Column(name = "app")
	private String app;
	
	//状态（0正常  1禁用，也将禁用所有调用的权限  2删除）
	@Column(name = "status")
	private String status;

	@Column(name = "jpa_version")
	private String japVersion;

	@Column(name = "create_operator")
	private String createOperator;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_datetime")
	private Date createdDatetime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_datetime")
	private Date updateDatetime;

	@Column(name = "update_operator")
	private String updateOperator;

}