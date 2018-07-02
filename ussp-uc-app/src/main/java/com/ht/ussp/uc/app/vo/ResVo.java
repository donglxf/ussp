package com.ht.ussp.uc.app.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String resCode;

    private String resNameCn;

    private Integer sequence;

    private String resType;

    private String resParent;

    private String resContent;

    private String fontIcon;

    private String status;
    
    private String ruleNum;
    
    private String ruleNumName;
    
    private String ruleContent;

	public ResVo(String resCode, String resNameCn, Integer sequence, String resType, String resParent,
			String resContent, String fontIcon, String status, String ruleNum, String ruleNumName) {
		super();
		this.resCode = resCode;
		this.resNameCn = resNameCn;
		this.sequence = sequence;
		this.resType = resType;
		this.resParent = resParent;
		this.resContent = resContent;
		this.fontIcon = fontIcon;
		this.status = status;
		this.ruleNum = ruleNum;
		this.ruleNumName = ruleNumName;
	}
    
    
}
