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
 * The persistent class for the HT_BOA_IN_CONTRAST database table.
 */
@Entity
@Data
@Table(name = "HT_BOA_IN_CONTRAST")
@NamedQuery(name = "HtBoaInContrast.findAll", query = "SELECT h FROM HtBoaInContrast h")
public class HtBoaInContrast implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "status")
    private String status;

}