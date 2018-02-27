package com.ht.ussp.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResDto  {
	
	private String resCode;
	
	private String resNameCn;
	
	private int sequence;
	
	private String resType;
	
	private String resParent;
	
	private String resContent;;
	
	private String fontIcon;
 
}
