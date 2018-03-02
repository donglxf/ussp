package org.ussp.uaa.app.config;

import org.springframework.context.annotation.Configuration;

/**
 * 
 * @ClassName: JwtSettings
 * @Description: jwt属性配置
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月6日 上午10:29:21
 */
@Configuration
public class JwtSettings {
	/**
	 * token有效时间
	 */
	private Integer tokenExpirationTime;

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
}
