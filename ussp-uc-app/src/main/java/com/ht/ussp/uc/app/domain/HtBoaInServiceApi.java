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
@Table(name = "ht_boa_in_service_api")
@NamedQuery(name = "HtBoaInServiceApi.findAll", query = "SELECT h FROM HtBoaInServiceApi h")
public class HtBoaInServiceApi implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//授权编码
	@Column(name = "auth_service_code")
	private String authServiceCode;
	
	@Column(name = "api_content")
	private String apiContent;
	
	@Column(name = "api_desc")
	private String apiDesc;
	
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