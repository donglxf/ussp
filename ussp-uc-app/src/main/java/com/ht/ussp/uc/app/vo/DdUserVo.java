package com.ht.ussp.uc.app.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class DdUserVo   {

	private String userId;

	private String userName;
	
	private String deptId;
	
	private String deptName;
	
	private String email;
	
	private String idNo;
	
	private String mobile;
	
	private String jobNumber;
	
	private String position;
	
	private Date creatDatetime;
	
	
	
}