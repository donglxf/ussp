package com.ht.ussp.ouc.app.domain;

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
* @ClassName: HtBoaInApp
* @Description: 系统表
* @author wim qiuwenwu@hongte.info
* @date 2018年1月5日 下午2:46:22
 */
@Data
@Entity
@Table(name = "HT_BOA_IN_APP")
@NamedQuery(name = "HtBoaInApp.findAll", query = "SELECT h FROM HtBoaInApp h")
public class HtBoaInApp implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "APP")
	private String app;

	@Column(name = "CREATE_OPERATOR")
	private String createOperator;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATETIME")
	private Date createdDatetime;

	@Column(name = "DEL_FLAG")
	private int delFlag;

	@Column(name = "JPA_VERSION")
	private int jpaVersion;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATETIME")
	private Date lastModifiedDatetime;

	@Column(name = "NAME")
	private String name;

	@Column(name = "NAME_CN")
	private String nameCn;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "UPDATE_OPERATOR")
	private String updateOperator;

	// 是否外部系统 0：是 1：否
	@Column(name = "isOS")
	private Integer isOS;

	// 系统登录最大错误次数
	@Column(name = "MAX_LOGIN_COUNT")
	private Integer maxLoginCount;

	// 返回提示
	@Column(name = "TIPS")
	private String tips;
	
	@Column(name = "SYS_TOKEN")
	private String sysToken;
	
	public HtBoaInApp() {
	}

}