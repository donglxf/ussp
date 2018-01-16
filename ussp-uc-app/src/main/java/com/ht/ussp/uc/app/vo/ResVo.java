package com.ht.ussp.uc.app.vo;

import java.io.Serializable;


public class ResVo implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String resCode;
	
	private String resNameCn;
	
	private int sequence;
	
	private String resType;
	
	private String resParent;
	
	private String resContent;;
	
	private String fontIcon;

	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}

	public String getResNameCn() {
		return resNameCn;
	}

	public void setResNameCn(String resNameCn) {
		this.resNameCn = resNameCn;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getResType() {
		return resType;
	}

	public void setResType(String resType) {
		this.resType = resType;
	}

	public String getResParent() {
		return resParent;
	}

	public void setResParent(String resParent) {
		this.resParent = resParent;
	}

	public String getResContent() {
		return resContent;
	}

	public void setResContent(String resContent) {
		this.resContent = resContent;
	}

	public String getFontIcon() {
		return fontIcon;
	}

	public void setFontIcon(String fontIcon) {
		this.fontIcon = fontIcon;
	}
	
	public ResVo(String resCode, String resNameCn, int sequence, String resType, String resParent, String resContent,
			String fontIcon) {
		super();
		this.resCode = resCode;
		this.resNameCn = resNameCn;
		this.sequence = sequence;
		this.resType = resType;
		this.resParent = resParent;
		this.resContent = resContent;
		this.fontIcon = fontIcon;
	}

}
