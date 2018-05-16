package com.ht.ussp.ouc.app.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * 
 * @ClassName: HtBoaOutClientDetail
 * @Description: 客户端信息表
 * @author wim qiuwenwu@hongte.info
 * @date 2018年5月16日 下午5:25:05
 */
@Entity
@Table(name="HT_BOA_OUT_CLIENT_DETAILS")
@NamedQuery(name="HtBoaOutClientDetail.findAll", query="SELECT h FROM HtBoaOutClientDetail h")
public class HtBoaOutClientDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String appId;

	private String additionalInformation;

	private String appCode;

	private String appSecret;

	private String autoApproveScopes;

	private String grantTypes;

	private String redirectUrl;

	@Column(name="refresh_token")
	private String refreshToken;

	private String scope;

	public HtBoaOutClientDetail() {
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAdditionalInformation() {
		return this.additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public String getAppCode() {
		return this.appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getAppSecret() {
		return this.appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getAutoApproveScopes() {
		return this.autoApproveScopes;
	}

	public void setAutoApproveScopes(String autoApproveScopes) {
		this.autoApproveScopes = autoApproveScopes;
	}

	public String getGrantTypes() {
		return this.grantTypes;
	}

	public void setGrantTypes(String grantTypes) {
		this.grantTypes = grantTypes;
	}

	public String getRedirectUrl() {
		return this.redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getRefreshToken() {
		return this.refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getScope() {
		return this.scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}