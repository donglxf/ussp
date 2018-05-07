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
@Table(name="HT_BOA_IN_PUBLISH")
@NamedQuery(name="HtBoaInCode.HtBoaInPublish", query="SELECT h FROM HtBoaInPublish h")
public class HtBoaInPublish implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
  
	@Column(name="PUBLISH_CODE")
	private String publishCode;
	
	@Column(name="PUBLISH_FILE_NAME")
	private String publishFileName;
	
	@Column(name="FALLBACK_FILE_NAME")
	private String fallBackFileName;
	
	@Column(name="APP")
	private String app;
	
	@Column(name="PUBLISH_SQL")
	private String publishSql;
	
	@Column(name="FALLBACK_SQL")
	private String fallBackSql;
	
	@Column(name="CREATE_OPERATOR")
	private String createOperator;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATETIME", insertable = false, updatable = false)
	private Date createdDatetime;
	
	@Column(name="UPDATE_OPERATOR")
	private String updateOperator;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATETIME", insertable = false, updatable = false)
	private Date updateDatetime;

	@Column(name="ISDOWN")
	private String isdown;
	
}