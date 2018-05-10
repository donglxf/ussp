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


/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInUser
 * @Description: 用户表
 * @date 2018年1月5日 下午2:57:24
 */
@Data
@Entity
@Table(name = "HT_BOA_IN_USER")
@NamedQuery(name = "HtBoaInUser.findAll", query = "SELECT h FROM HtBoaInUser h")
public class HtBoaInUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "JOB_NUMBER")
    private String jobNumber;

    @Column(name = "CREATE_OPERATOR")
    private String createOperator;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATETIME", insertable = false, updatable = false)
    private Date createdDatetime;

    @Column(name = "DEL_FLAG")
    private int delFlag;

    @Column(name = "email")
    private String email;

    @Column(name = "ID_NO")
    private String idNo;

    @Column(name = "JPA_VERSION")
    private int jpaVersion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_MODIFIED_DATETIME", insertable = false, updatable = false)
    private Date lastModifiedDatetime;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "ORG_CODE")
    private String orgCode;

    @Column(name = "ORG_PATH")
    private String orgPath;

    @Column(name = "ROOT_ORG_CODE")
    private String rootOrgCode;

    @Column(name = "UPDATE_OPERATOR")
    private String updateOperator;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "DATA_SOURCE")
    private int dataSource;
    @Column(name = "USER_TYPE")
    private String userType;
    @Column(name = "IS_ORG_USER")
    private int isOrgUser;

    //状态: 0 正常  2离职
    @Column(name = "STATUS")
    private String status;
    
    public HtBoaInUser() {
    }
}