package com.ht.ussp.gateway.app.service;

import java.io.Serializable;

//import javax.persistence.Column;
//import javax.persistence.Embeddable;
//import javax.persistence.EmbeddedId;
//import javax.persistence.Entity;
//import javax.persistence.EnumType;
//import javax.persistence.Enumerated;
//import javax.persistence.Table;

/**
 * 
* @ClassName: UserRole
* @Description: TODO
* @author wim qiuwenwu@hongte.info
* @date 2018年1月8日 上午10:06:26
 */
//@Entity
//@Table(name = "USER_ROLE")
public class UserRole {
//    @Embeddable
    public static class Id implements Serializable {
        private static final long serialVersionUID = 1322120000551624359L;
        
//        @Column(name = "APP_USER_ID")
        protected Long userId;
        
//        @Enumerated(EnumType.STRING)
//        @Column(name = "ROLE")
        protected Role role;
        
        public Id() { }

        public Id(Long userId, Role role) {
            this.userId = userId;
            this.role = role;
        }
    }
    
//    @EmbeddedId
    Id id = new Id();
    
//    @Enumerated(EnumType.STRING)
//    @Column(name = "ROLE", insertable=false, updatable=false)
    protected Role role;

    public Role getRole() {
        return role;
    }
}
