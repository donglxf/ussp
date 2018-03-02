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


 
@Entity
@Data
@Table(name="DD_DEPT_USER")
@NamedQuery(name="DdDeptUser.findAll", query="SELECT h FROM DdDeptUser h")
public class DdDeptUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="CREATE_OPERATOR")
	private String createOperator;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATETIME")
	private Date createdDatetime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_MODIFIED_DATETIME")
	private Date lastModifiedDatetime;

	@Column(name="USER_ID")
	private String userId;

	@Column(name="USER_NAME")
	private String userName;

	@Column(name="ORG_CODE")
	private String orgCode;

	@Column(name="UPDATE_OPERATOR")
	private String updateOperator;
	
	@Column(name="EMAIL")
	private String email;
	
	@Column(name="ID_NO")
	private String idNo;
	
	@Column(name="MOBILE")
	private String mobile;
	
	@Column(name="JOB_NUMBER")
	private String jobNumber;
	
	@Column(name="POSITION")
	private String position;
	
	
	
}