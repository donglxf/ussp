package com.ht.ussp.common;
/**
 * 资源类型
 */
public enum ResTypeEnum {
	RES_TYPE_MODULE("module", "模块"),
	RES_TYPE_GROUP("group", "组别"),
	RES_TYPE_VIEW("view", "页面"),
	RES_TYPE_BUTTON("btn", "按钮"),
	RES_TYPE_TAB("tab", "tab"),
	RES_TYPE_API("api", "api");
	
    protected String returnCode;
    protected String codeDesc;

    private ResTypeEnum(String returnCode, String codeDesc)
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