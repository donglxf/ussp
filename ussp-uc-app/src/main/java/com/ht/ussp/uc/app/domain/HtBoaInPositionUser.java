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
 * 
* @ClassName: HtBoaInPositionUser
* @Description: 岗位用户表
* @author wim qiuwenwu@hongte.info
* @date 2018年1月5日 下午2:51:31
 */
@Entity
@Data
@Table(name="HT_BOA_IN_POSITION_USER")
@NamedQuery(name="HtBoaInPositionUser.findAll", query="SELECT h FROM HtBoaInPositionUser h")
public class HtBoaInPositionUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="CREATE_OPERATOR")
	private String createOperator;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATETIME")
	private Date createdDatetime;

	@Column(name="DEL_FLAG")
	private int delFlag;

	@Column(name="JPA_VERSION")
	private int jpaVersion;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_MODIFIED_DATETIME")
	private Date lastModifiedDatetime;

	@Column(name="POSITION_CODE")
	private String positionCode;

	@Column(name="ROOT_ORG_CODE")
	private String rootOrgCode;

	@Column(name="UPDATE_OPERATOR")
	private String updateOperator;

	@Column(name="USER_ID")
	private String userId;

	public HtBoaInPositionUser() {
	}

	 

}