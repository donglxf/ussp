package com.ht.ussp.gateway.app.model;

import java.io.Serializable;

import com.ht.ussp.gateway.app.util.SysStatus;


public class ResponseModal extends Modal implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public ResponseModal(){
		super();
	}
	
	public ResponseModal(String code,String message){
		super(code,message);
	}
	
	public ResponseModal(SysStatus sysStatus){
		super(sysStatus.getStatus(),sysStatus.getMsg());
	}
	
	public ResponseModal(String code,String message,Object obj){
		super(code,message);
		this.result = obj;
	}
	
	private Object result;
	
	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
