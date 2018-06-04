package com.ht.ussp.uaa.app.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateSalaryJwtVo {

	private String jobNumber;
	
	private String batchNumber;
	
	private Integer sendType;
	
	private Integer type;
}
