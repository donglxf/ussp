package com.ht.ussp.common;
/**
 * (10:公司 20:中心 30:片区 40:分公司 50部门 60小组)
 */
public enum OrgTypeEnum {
	ORG_TYPE_COMPANY("10", "公司"),
	ORG_TYPE_CENTER("20", "中心"),
	ORG_TYPE_AREAS("30", "片区"),
	ORG_TYPE_SUB_COMPANY("40", "分公司"),
	ORG_TYPE_DEPT("50", "部门"),
	ORG_TYPE_GROUP("60", "小组") ;
	
    protected String returnCode;
    protected String codeDesc;

    private OrgTypeEnum(String returnCode, String codeDesc)
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