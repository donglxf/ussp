package com.ht.ussp.core;


/**
 * 公共返回码
 */
public enum ReturnCodeEnum {
    SUCCESS("0000", "执行成功"),
    ERROR_PARAM("9997", "参数错误"),
    EXCEPTION("9998", "执行异常"),
    FAIL("9999", "执行失败");
    protected String returnCode;
    protected String codeDesc;

    private ReturnCodeEnum(String returnCode, String codeDesc)
    {
        this.returnCode = returnCode;
        this.codeDesc = codeDesc;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public String getCodeDesc() {
        return codeDesc;
    }
}
