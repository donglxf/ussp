package com.ht.ussp.uc.app.model;

import java.io.Serializable;

import com.ht.ussp.uc.app.util.SysStatus;


public class ResponseModal extends Modal implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public ResponseModal(){
		super();
	}
	
	public ResponseModal(int code,String message){
		super(code,message);
	}
	
	public ResponseModal(SysStatus sysStatus){
		super(sysStatus.getStatus(),sysStatus.getMsg());
	}
	
	public ResponseModal(int code,String message,Object obj){
		super(code,message);
		this.result = obj;
	}
	
	private Object result;
	
	private Object key;

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	
}
