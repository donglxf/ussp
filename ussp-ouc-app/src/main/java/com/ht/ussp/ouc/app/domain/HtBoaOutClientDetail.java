package com.ht.ussp.ouc.app.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import lombok.Data;


/**
 * 
 * @ClassName: HtBoaOutClientDetail
 * @Description: 客户端信息表
 * @author wim qiuwenwu@hongte.info
 * @date 2018年5月16日 下午5:25:05
 */
@Data
@Entity
@Table(name="HT_BOA_OUT_CLIENT_DETAILS")
@NamedQuery(name="HtBoaOutClientDetail.findAll", query="SELECT h FROM HtBoaOutClientDetail h")
public class HtBoaOutClientDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(name = "appId")
	private String appId;

	@Column(name = "additionalInformation")
	private String additionalInformation;

	@Column(name = "appCode")
	private String appCode;

	@Column(name = "appSecret")
	private String appSecret;
	 
	@Column(name = "autoApproveScopes")
	private String autoApproveScopes;
	 
	@Column(name = "grantTypes")
	private String grantTypes;
	 
	@Column(name = "redirectUrl")
	private String redirectUrl;

	@Column(name="refresh_token")
	private String refreshToken;
	
	@Column(name = "scope")
	private String scope;
	
}