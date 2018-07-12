package com.ht.ussp.uc.app.config.bm;

/**
 * 加密j解密返回的对象
 */
public class EncryptionResult {
    private String uuid;
    private  String param;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
