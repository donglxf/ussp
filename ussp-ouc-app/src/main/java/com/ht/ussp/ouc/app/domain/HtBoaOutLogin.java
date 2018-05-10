package com.ht.ussp.ouc.app.domain;

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
@Table(name = "HT_BOA_OUT_LOGIN")
@NamedQuery(name = "HtBoaOutLogin.findAll", query = "SELECT h FROM HtBoaOutLogin h")
public class HtBoaOutLogin implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "LOGIN_ID")
    private String loginId;
    
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "PASSWORD")
    private String password;
    
    @Column(name = "OLD_PASSWORD")
    private String oldPassword;
    
    @Column(name = "DEL_FLAG")
    private int delFlag;
    
    //状态: 0 正常 1初始密码 
    @Column(name = "STATUS")
    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PWD_EXP_DATE")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Date pwdExpDate;

    @Column(name = "CREATE_OPERATOR")
    private String createOperator;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATETIME", insertable = false, updatable = false)
    private Date createdDatetime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EFFECTIVE_DATE")
    private Date effectiveDate;

    @Column(name = "FAILED_COUNT")
    private Integer failedCount;

    @Column(name = "JPA_VERSION")
    private int jpaVersion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_MODIFIED_DATETIME", insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Date lastModifiedDatetime;

    @Column(name = "UPDATE_OPERATOR")
    private String updateOperator;


    

}