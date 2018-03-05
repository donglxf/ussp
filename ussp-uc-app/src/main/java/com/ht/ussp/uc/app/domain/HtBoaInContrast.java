package com.ht.ussp.uc.app.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the HT_BOA_IN_CONTRAST database table.
 */
@Entity
@Table(name = "HT_BOA_IN_CONTRAST")
@NamedQuery(name = "HtBoaInContrast.findAll", query = "SELECT h FROM HtBoaInContrast h")
public class HtBoaInContrast implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "bm_business_id")
    private String bmBusinessId;

    private String contrast;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "contrast_datetime", insertable = false, updatable = false)
    private Date contrastDatetime;

    @Column(name = "dd_business_id")
    private String ddBusinessId;

    @Column(name = "extend_business_id_1")
    private String extendBusinessId1;

    @Column(name = "extend_business_id_2")
    private String extendBusinessId2;

    @Column(name = "extend_business_id_3")
    private String extendBusinessId3;

    @Column(name = "extend_business_id_4")
    private String extendBusinessId4;

    @Column(name = "extend_business_id_5")
    private String extendBusinessId5;

    private String type;

    @Column(name = "uc_business_id")
    private String ucBusinessId;

    public HtBoaInContrast() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBmBusinessId() {
        return this.bmBusinessId;
    }

    public void setBmBusinessId(String bmBusinessId) {
        this.bmBusinessId = bmBusinessId;
    }

    public String getContrast() {
        return this.contrast;
    }

    public void setContrast(String contrast) {
        this.contrast = contrast;
    }

    public Date getContrastDatetime() {
        return this.contrastDatetime;
    }

    public void setContrastDatetime(Date contrastDatetime) {
        this.contrastDatetime = contrastDatetime;
    }

    public String getDdBusinessId() {
        return this.ddBusinessId;
    }

    public void setDdBusinessId(String ddBusinessId) {
        this.ddBusinessId = ddBusinessId;
    }

    public String getExtendBusinessId1() {
        return this.extendBusinessId1;
    }

    public void setExtendBusinessId1(String extendBusinessId1) {
        this.extendBusinessId1 = extendBusinessId1;
    }

    public String getExtendBusinessId2() {
        return this.extendBusinessId2;
    }

    public void setExtendBusinessId2(String extendBusinessId2) {
        this.extendBusinessId2 = extendBusinessId2;
    }

    public String getExtendBusinessId3() {
        return this.extendBusinessId3;
    }

    public void setExtendBusinessId3(String extendBusinessId3) {
        this.extendBusinessId3 = extendBusinessId3;
    }

    public String getExtendBusinessId4() {
        return this.extendBusinessId4;
    }

    public void setExtendBusinessId4(String extendBusinessId4) {
        this.extendBusinessId4 = extendBusinessId4;
    }

    public String getExtendBusinessId5() {
        return this.extendBusinessId5;
    }

    public void setExtendBusinessId5(String extendBusinessId5) {
        this.extendBusinessId5 = extendBusinessId5;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUcBusinessId() {
        return this.ucBusinessId;
    }

    public void setUcBusinessId(String ucBusinessId) {
        this.ucBusinessId = ucBusinessId;
    }

}