package com.ht.ussp.uaa.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @ClassName: JwtSettings
 * @Description: jwt属性配置
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月6日 上午10:29:21
 */
@Configuration
@ConfigurationProperties(prefix = "uaa.security.jwt")
public class JwtSettings {
	/**
	 * token有效时间
	 */
	private Integer tokenExpirationTime;
	/**
	 * 外部系统token有效时间
	 */
	private Integer appTokenTime;

	/**
	 * 发布者
	 */
	private String tokenIssuer;

	/**
	 * 签名使用的key
	 */
	private String tokenSigningKey;

	/**
	 * token刷新时间
	 */
	private Integer refreshTokenExpTime;
	
	
	private Integer outUserTokenTime;

	public Integer getRefreshTokenExpTime() {
		return refreshTokenExpTime;
	}

	public void setRefreshTokenExpTime(Integer refreshTokenExpTime) {
		this.refreshTokenExpTime = refreshTokenExpTime;
	}

	public Integer getTokenExpirationTime() {
		return tokenExpirationTime;
	}

	public void setTokenExpirationTime(Integer tokenExpirationTime) {
		this.tokenExpirationTime = tokenExpirationTime;
	}

	public String getTokenIssuer() {
		return tokenIssuer;
	}

	public void setTokenIssuer(String tokenIssuer) {
		this.tokenIssuer = tokenIssuer;
	}

	public String getTokenSigningKey() {
		return tokenSigningKey;
	}

	public void setTokenSigningKey(String tokenSigningKey) {
		this.tokenSigningKey = tokenSigningKey;
	}

	public Integer getAppTokenTime() {
		return appTokenTime;
	}

	public void setAppTokenTime(Integer appTokenTime) {
		this.appTokenTime = appTokenTime;
	}

	public Integer getOutUserTokenTime() {
		return outUserTokenTime;
	}

	public void setOutUserTokenTime(Integer outUserTokenTime) {
		this.outUserTokenTime = outUserTokenTime;
	}
	
}
