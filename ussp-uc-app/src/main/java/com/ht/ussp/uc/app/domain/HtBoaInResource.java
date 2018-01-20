package com.ht.ussp.uc.app.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ht.ussp.uc.app.vo.ResVo;


/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInResource
 * @Description: 资源信息表
 * @date 2018年1月5日 下午2:52:34
 */
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
    private int delFlag;

    @Column(name = "JPA_VERSION")
    private int jpaVersion;

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
    private int sequence;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "UPDATE_OPERATOR")
    private String updateOperator;

    public HtBoaInResource() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApp() {
        return this.app;
    }

    public void setApp(String app) {
        this.app = app;
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

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getResCode() {
        return this.resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResContent() {
        return this.resContent;
    }

    public void setResContent(String resContent) {
        this.resContent = "".equals(resContent) ? null : resContent;
    }

    public byte[] getResIcon() {
        return this.resIcon;
    }

    public void setResIcon(byte[] resIcon) {
        this.resIcon = resIcon;
    }

    public String getResName() {
        return this.resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getResNameCn() {
        return this.resNameCn;
    }

    public void setResNameCn(String resNameCn) {
        this.resNameCn = resNameCn;
    }

    public String getResParent() {
        return this.resParent;
    }

    public void setResParent(String resParent) {
        this.resParent = "".equals(resParent) ? null : resParent;
    }

    public String getResType() {
        return this.resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdateOperator() {
        return this.updateOperator;
    }

    public void setUpdateOperator(String updateOperator) {
        this.updateOperator = updateOperator;
    }

    public String getFontIcon() {
        return fontIcon;
    }

    public void setFontIcon(String fontIcon) {
        this.fontIcon = fontIcon;
    }

}