package com.ht.ussp.uc.app.vo;

import java.io.Serializable;

import lombok.Data;
 
@Data
public class NextChildDto implements Serializable {
    private static final long serialVersionUID = 8979945797301301207L;

    /**
     * 类型编码
     */
    private String typeCode;
    /**
     * 版本
     */
    private String version;
    /**
     * 值编码
     */
    private String valCode;
    
	private String parentId;
	
	private String name;

	private String levelType;
}
