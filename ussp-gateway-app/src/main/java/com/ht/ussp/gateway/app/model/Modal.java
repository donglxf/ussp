package com.ht.ussp.gateway.app.model;

import com.ht.ussp.common.SysStatus;

public class Modal {
	

	public Modal() {
		super();
	}

	public Modal(String status_code, String result_msg) {
		super();
		this.status_code = status_code;	
		this.result_msg = result_msg;
	}

	private String status_code;
	
	private String result_msg;

 
	
	public String getStatus_code() {
		return status_code;
	}

	public void setStatus_code(String status_code) {
		this.status_code = status_code;
	}

	public String getResult_msg() {
		return result_msg;
	}

	public void setResult_msg(String result_msg) {
		this.result_msg = result_msg;
	}

	public void setSysStatus(SysStatus ss) {
		status_code=ss.getStatus();
		result_msg=ss.getMsg();		 
	}
}
