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
@Table(name = "HT_BOA_OUT_USER")
@NamedQuery(name = "HtBoaOutUser.findAll", query = "SELECT h FROM HtBoaOutUser h")
public class HtBoaOutUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "email")
    private String email;

    @Column(name = "ID_NO")
    private String idNo;
    
    @Column(name = "mobile")
    private String mobile;
    
    @Column(name = "STATUS")
    private String status;

    //注册类型：sms:短信注册 email:邮箱注册 normal:用户名密码注册  qq:QQ注册  wx:微信注册
    @Column(name = "REGIST_TYPE")
    private String registType;
    
    //0:普通注册用户 1:存量用户转成了普通用户 10:微信系统存量用户
    @Column(name = "USER_TYPE")
    private String userType;
    
    @Column(name = "DATA_SOURCE")
    private String dataSource;

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