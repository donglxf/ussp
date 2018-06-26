package com.ht.ussp.common;
/**
 *层级(20-公司层级  40-片区层级  60-分公司层级  80-部门层级  100-小组层级)
 */
public enum OrgBusiTypeEnum {
	BUSI_ORG_TYPE_COMPANY("20", "公司"),
	BUSI_ORG_TYPE_DISPATCH("40", "片区"),
	BUSI_ORG_TYPE_BRANCH("60", "分公司"),
	BUSI_ORG_TYPE_DEPT("80", "部门"),
	BUSI_ORG_TYPE_GROUP("100", "小组") ;
	
    protected String returnCode;
    protected String codeDesc;

    private OrgBusiTypeEnum(String returnCode, String codeDesc)
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