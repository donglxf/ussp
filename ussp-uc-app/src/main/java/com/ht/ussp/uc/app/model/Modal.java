package com.ht.ussp.uc.app.model;

import com.ht.ussp.uc.app.util.SysStatus;

public class Modal {
	

	public Modal() {
		super();
	}

	public Modal(int status_code, String result_msg) {
		super();
		this.status_code = status_code;	
		this.result_msg = result_msg;
	}

	private int status_code;
	
	private String result_msg;

 
	
	public int getStatus_code() {
		return status_code;
	}

	public void setStatus_code(int status_code) {
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
