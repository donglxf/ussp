package com.ht.ussp.uc.app.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;


/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInResource
 * @Description: 资源信息表
 * @date 2018年1月5日 下午2:52:34
 */
@Data
@Entity
@Table(name = "HT_BOA_IN_RESOURCE")
@NamedQuery(name = "HtBoaInResource.findAll", query = "SELECT h FROM HtBoaInResource h")
public class HtBoaInResource implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "APP")
    private String app;

    @Column(name = "CREATE_OPERATOR")
    private String createOperator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATETIME", updatable = false, insertable = false)
    private Date createdDatetime;

    @Column(name = "DEL_FLAG")
    private Integer delFlag;

    @Column(name = "JPA_VERSION")
    private Integer jpaVersion;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_MODIFIED_DATETIME", updatable = false, insertable = false)
    private Date lastModifiedDatetime;

    @Column(name = "REMARK")
    private String remark;

    @Column(name = "RES_CODE")
    private String resCode;

    @Column(name = "RES_CONTENT")
    private String resContent;

    @Column(name = "FONT_ICON")
    private String fontIcon;

    @Lob
    @Column(name = "RES_ICON")
    private byte[] resIcon;

    @Column(name = "RES_NAME")
    private String resName;

    @Column(name = "RES_NAME_CN")
    private String resNameCn;

    @Column(name = "RES_PARENT")
    private String resParent;

    @Column(name = "RES_TYPE")
    private String resType;

    @Column(name = "SEQUENCE")
    private Integer sequence;

    //状态（1，禁用，0，启用，2，隐藏 ）
    @Column(name = "STATUS")
    private String status;

    @Column(name = "UPDATE_OPERATOR")
    private String updateOperator;

    public HtBoaInResource() {
    }

}