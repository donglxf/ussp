package com.ht.ussp.uaa.app.util;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
* @ClassName: ErrorCode
* @Description: TODO
* @author wim qiuwenwu@hongte.info
* @date 2018年1月8日 上午10:06:39
 */
public enum ErrorCode {
    GLOBAL(2),

    AUTHENTICATION(10), JWT_TOKEN_EXPIRED(11);
    
    private int errorCode;

    private ErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @JsonValue
    public int getErrorCode() {
        return errorCode;
    }
}
