package com.ht.ussp.uc.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInUser
 * @Description: 用户表
 * @date 2018年1月5日 下午2:57:24
 */
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

    public HtBoaInUser() {
    }

    public Long getId() {
        return this.id;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreateOperator() {
        return this.createOperator;
    }

    public void setCreateOperator(String createOperator) {
        this.createOperator = createOperator;
    }

    public Date getCreatedDatetime() {
        return this.createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public int getDelFlag() {
        return this.delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = "".equals(email) ? null : email;
    }

    public String getIdNo() {
        return this.idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = "".equals(idNo) ? null : idNo;
    }

    public int getJpaVersion() {
        return this.jpaVersion;
    }

    public void setJpaVersion(int jpaVersion) {
        this.jpaVersion = jpaVersion;
    }

    public Date getLastModifiedDatetime() {
        return this.lastModifiedDatetime;
    }

    public void setLastModifiedDatetime(Date lastModifiedDatetime) {
        this.lastModifiedDatetime = lastModifiedDatetime;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = "".equals(mobile) ? null : mobile;
    }

    public String getOrgCode() {
        return this.orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgPath() {
        return this.orgPath;
    }

    public void setOrgPath(String orgPath) {
        this.orgPath = orgPath;
    }

    public String getRootOrgCode() {
        return this.rootOrgCode;
    }

    public void setRootOrgCode(String rootOrgCode) {
        this.rootOrgCode = rootOrgCode;
    }

    public String getUpdateOperator() {
        return this.updateOperator;
    }

    public void setUpdateOperator(String updateOperator) {
        this.updateOperator = updateOperator;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}