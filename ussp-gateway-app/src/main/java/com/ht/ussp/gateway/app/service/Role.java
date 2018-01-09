package com.ht.ussp.gateway.app.service;



/**
 * 
* @ClassName: Role
* @Description: TODO
* @author wim qiuwenwu@hongte.info
* @date 2018年1月6日 上午11:40:30
 */
public enum Role {
    ADMIN, PREMIUM_MEMBER, MEMBER;
    
    public String authority() {
        return "ROLE_" + this.name();
    }
}
