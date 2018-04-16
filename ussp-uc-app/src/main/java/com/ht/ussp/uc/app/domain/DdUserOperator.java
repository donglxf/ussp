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
@Table(name="dd_user_operator")
@NamedQuery(name="DdUserOperator.findAll", query="SELECT h FROM DdUserOperator h")
public class DdUserOperator implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="USER_ID")
	private String userId;

	@Column(name="USER_NAME")
	private String userName;
	
	@Column(name="dept_id")
	private String deptId;
	
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
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_datetime")
	private Date creatDatetime;
	
	//1:钉钉新增，2：钉钉删除，3：钉钉修改
	@Column(name="operator_type")
	private Integer operatorType;
	
	@Column(name="syn_flag")
	private String synFlag;
	
	
}