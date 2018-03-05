package com.ht.ussp.uaa.app.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @ClassName: LoginRequest
 * @Description: 创造登录请求对象
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月6日 上午11:48:41
 */

public class LoginRequest {
    private String userName;
    private String password;

    @JsonCreator
    public LoginRequest(@JsonProperty("userName") String userName, @JsonProperty("password") String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
