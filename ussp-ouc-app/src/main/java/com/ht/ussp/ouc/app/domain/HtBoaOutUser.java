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


/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInUser
 * @Description: 用户表
 * @date 2018年1月5日 下午2:57:24
 */
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

    //注册类型：1 手机号 2邮箱 3其他
    @Column(name = "REGIST_TYPE")
    private String registType;
    
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