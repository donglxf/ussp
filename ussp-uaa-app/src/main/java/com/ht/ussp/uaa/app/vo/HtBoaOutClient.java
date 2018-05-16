package com.ht.ussp.uaa.app.vo;

import lombok.Data;

@Data
public class HtBoaOutClient{

	private int id;
	
	private String appId;

	private String additionalInformation;

	private String appCode;

	private String appSecret;
	 
	private String autoApproveScopes;
	 
	private String grantTypes;
	 
	private String redirectUrl;

	private String refreshToken;
	
	private String scope;
	
}