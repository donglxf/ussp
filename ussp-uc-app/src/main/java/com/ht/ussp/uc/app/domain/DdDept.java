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
@Table(name="DD_DEPT")
@NamedQuery(name="DdDept.findAll", query="SELECT h FROM DdDept h")
public class DdDept implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="dept_id")
	private String deptId;

	@Column(name="DEPT_NAME")
	private String deptName;

	@Column(name="parent_dept_id")
	private String parentId;
	
	@Column(name="level")
	private Integer level;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_datetime")
	private Date creatDatetime;

	@Column(name="ORDERS")
	private String orders;
	
	
}