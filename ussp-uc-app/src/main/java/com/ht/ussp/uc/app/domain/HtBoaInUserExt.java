package com.ht.ussp.uc.app.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;


@Data
@Entity
@Table(name = "HT_BOA_IN_USER_EXT")
@NamedQuery(name = "HtBoaInUserExt.findAll", query = "SELECT h FROM HtBoaInUserExt h")
public class HtBoaInUserExt implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "USER_ID")
    private String userId;
    
    @Column(name = "BM_USER_ID")
    private String bmUserId;

    @Column(name = "BUSI_ORG_CODE")
    private String busiOrgCode;
    
    @Column(name = "CREATE_OPERATOR")
    private String createOperator;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATETIME", insertable = false, updatable = false)
    private Date createdDatetime;

    @Column(name = "JPA_VERSION")
    private int jpaVersion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_MODIFIED_DATETIME", insertable = false, updatable = false)
    private Date lastModifiedDatetime;
 
    @Column(name = "UPDATE_OPERATOR")
    private String updateOperator;

 
}