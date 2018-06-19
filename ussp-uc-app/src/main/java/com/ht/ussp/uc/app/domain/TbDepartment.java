package com.ht.ussp.uc.app.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="tb_department")
@NamedQuery(name="TbDepartment.findAll", query="SELECT h FROM TbDepartment h")
public class TbDepartment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "DEPT_ID", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String deptId;
	
	@Column(name="DEPT_NAME")
	private String deptName;
	
	@Column(name="RESERVE_1")
	private String parentOrgCode;
	
	@Column(name="DEPT_DESC")
	private String businessGroup;
	
	//所属分公司
	@Column(name="RESERVE_2")
	private String branchCode;
	
	//片区
	@Column(name="district_id")
	private String districtCode;
	
	//所属财务分公司
	@Column(name="RESERVE_3")
	private String financeCode;
	
	//审批中心
	@Column(name="RESERVE_4")
	private String approvalCode;
	
	//流程中心
	@Column(name="RESERVE_5")
	private String activityCode;
	
	@Column(name="dept_level")
	private Integer orgLevel;
	
	@Column(name="branch_province")
	private String province;
	
	@Column(name="branch_city")
	private String city;
	
	@Column(name="area_range")
	private String county;
	
	//用于转换层级
	@Column(name="business_region")
	private String businessRegion;
	

}