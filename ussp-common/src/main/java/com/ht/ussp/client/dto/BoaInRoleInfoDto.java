package com.ht.ussp.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoaInRoleInfoDto {

	long id;
	
    String roleCode;

    String roleName;

    String roleNameCn;

    String rOrgCode;

    String rOrgName;

    String rOrgNameCn;

    String rOrgType;

    String status;
    
    int delFlag;
    
    String app;
 
}
