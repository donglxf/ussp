package com.ht.ussp.uc.app.model;

import com.ht.ussp.common.SysStatus;

public class Modal {
	

	public Modal() {
		super();
	}

	public Modal(String statusCode, String resultMsg) {
		super();
		this.statusCode = statusCode;	
		this.resultMsg = resultMsg;
	}

	private String statusCode;
	
	private String resultMsg;

 
	
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public void setSysStatus(SysStatus ss) {
		statusCode=ss.getStatus();
		resultMsg=ss.getMsg();		 
	}
}
