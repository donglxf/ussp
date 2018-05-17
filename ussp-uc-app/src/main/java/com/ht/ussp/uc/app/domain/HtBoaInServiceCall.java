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
@Table(name = "ht_boa_in_service_call")
@NamedQuery(name = "HtBoaInServiceCall.findAll", query = "SELECT h FROM HtBoaInServiceCall h")
public class HtBoaInServiceCall implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "call_servcie_code")
	private String callServcieCode;
	
	@Column(name = "main_service_code")
	private String mainServiceCode;
	
	@Column(name = "call_servcie")
	private String callServcie;
	
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