/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: EncryptUtil.java
 * Author:   谭荣巧
 * Date:     2018/1/14 11:53
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 加解密工具类<br>
 * 支持密码加密、MD5加密、RSA加解密、3DES加解密<br>
 *
 * @author 谭荣巧
 * @Date 2018/1/14 11:53
 */
public class EncryptUtil {

    private static final String SALT = "@53#$%^&*(SDF324fvn~!<>m4654SDF:";

    /**
     * 密码加密<br>
     * 使用了org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder加密类
     *
     * @param password 明文密码
     * @return 密文密码
     * @author 谭荣巧
     * @Date 2018/1/14 11:56
     */
    public static String passwordEncrypt(String password) {
        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        for (int i = 0; i < 5; i++) {
            password = password.concat(password);
        }
        password = password.concat(SALT);
        return encode.encode(password);
    }

    /**
     * 密码验证<br>
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 密文密码
     * @return true 密码验证正确，false 密码验证不正确
     * @author 谭荣巧
     * @Date 2018/1/30 14:33
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        for (int i = 0; i < 5; i++) {
            rawPassword = rawPassword.concat(rawPassword);
        }
        rawPassword = rawPassword.concat(SALT);
        return encode.matches(rawPassword, encodedPassword);
    }

}
