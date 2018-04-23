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
@Table(name = "HT_BOA_IN_BM_USER")
@NamedQuery(name = "HtBoaInBmUser.findAll", query = "SELECT h FROM HtBoaInBmUser h")
public class HtBoaInBmUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "JOB_NUMBER")
    private String jobNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATETIME", insertable = false, updatable = false)
    private Date createdDatetime;
  
    @Column(name = "DEL_FLAG")
    private int delFlag;

    @Column(name = "email")
    private String email;

    @Column(name = "ID_NO")
    private String idNo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_MODIFIED_DATETIME", insertable = false, updatable = false)
    private Date lastModifiedDatetime;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "ORG_CODE")
    private String orgCode;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "PARENT_DEPT")
    private String parentDept; //所属分公司/中心
    
    @Column(name = "STATUS")
    private String status;
 
    

}