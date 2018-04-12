package com.ht.ussp.client.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HtBoaInOrgDto   {

	private Long id;

	private String createOperator;

	private Date createdDatetime;

	private int delFlag;

	private int jpaVersion;

	private Date lastModifiedDatetime;

	private String orgCode;

	private String orgName;

	private String orgNameCn;

	private String orgPath;

	private String orgType;

	private String parentOrgCode;
	
	private String remark;

	private String rootOrgCode;

	private int sequence;

	private String updateOperator;


}