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
        for (int i = 0; i < 10; i++) {
            password = password.concat(password);
        }
        return encode.encode(password);
    }
}
