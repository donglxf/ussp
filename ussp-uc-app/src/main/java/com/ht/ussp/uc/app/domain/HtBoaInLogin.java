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
 * @ClassName: HtBoaInLogin
 * @Description: 登录信息表
 * @date 2018年1月5日 下午2:49:28
 */
@Data
@Entity
@Table(name = "HT_BOA_IN_LOGIN")
@NamedQuery(name = "HtBoaInLogin.findAll", query = "SELECT h FROM HtBoaInLogin h")
public class HtBoaInLogin implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "CREATE_OPERATOR")
    private String createOperator;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATETIME", insertable = false, updatable = false)
    private Date createdDatetime;

    @Column(name = "DEL_FLAG")
    private int delFlag;

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

    @Column(name = "LOGIN_ID")
    private String loginId;

    @Column(name = "PASSWORD")
    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PWD_EXP_DATE")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Date pwdExpDate;

    @Column(name = "ROOT_ORG_CODE")
    private String rootOrgCode;

    @Column(name = "STATUS")
    private int status;

    @Column(name = "UPDATE_OPERATOR")
    private String updateOperator;

    @Column(name = "USER_ID")
    private String userId;

    public HtBoaInLogin() {
    }

    

}