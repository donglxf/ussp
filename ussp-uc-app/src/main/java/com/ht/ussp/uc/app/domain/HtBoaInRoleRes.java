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
 * @ClassName: HtBoaInRoleRes
 * @Description: 角色资源表
 * @date 2018年1月5日 下午2:57:02
 */
@Data
@Entity
@Table(name = "HT_BOA_IN_ROLE_RES")
@NamedQuery(name = "HtBoaInRoleRes.findAll", query = "SELECT h FROM HtBoaInRoleRes h")
public class HtBoaInRoleRes implements Serializable {
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

    @Column(name = "JPA_VERSION")
    private int jpaVersion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_MODIFIED_DATETIME", insertable = false, updatable = false)
    private Date lastModifiedDatetime;

    @Column(name = "RES_CODE")
    private String resCode;

    @Column(name = "ROLE_CODE")
    private String roleCode;

    @Column(name = "ROOT_ORG_CODE")
    private String rootOrgCode;

    @Column(name = "UPDATE_OPERATOR")
    private String updateOperator;

    public HtBoaInRoleRes() {
    }

}