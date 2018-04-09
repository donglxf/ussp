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
@Table(name = "HT_BOA_IN_BM_ORG")
@NamedQuery(name = "HtBoaInBmOrg.findAll", query = "SELECT h FROM HtBoaInBmOrg h")
public class HtBoaInBmOrg implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="ORG_CODE")
	private String orgCode;

	@Column(name="ORG_NAME")
	private String orgName;

	@Column(name="ORG_NAME_CN")
	private String orgNameCn;

	@Column(name="ORG_PATH")
	private String orgPath;

	@Column(name="ORG_TYPE")
	private String orgType;

	@Column(name="PARENT_ORG_CODE")
	private String parentOrgCode;
	
	@Column(name="REMARK")
	private String remark;

	@Column(name="ROOT_ORG_CODE")
	private String rootOrgCode;

	@Column(name="SEQUENCE")
	private int sequence;


}