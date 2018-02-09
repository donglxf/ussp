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

import java.util.Random;

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


	/**
	 * 生成随机密码
	 *
	 * @param pwd_len  生成的密码的总长度
	 * @return 密码的字符串
	 */
    public static String genRandomNum(int pwd_len) {
		// 35是因为数组是从0开始的，26个字母+10个数字
		final int maxNum = 36;
		int i; // 生成的随机数
		int count = 0; // 生成的密码的长度
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
				't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		while (count < pwd_len) {
			// 生成随机数，取绝对值，防止生成负数，
			i = Math.abs(r.nextInt(maxNum)); // 生成的数最大为36-1
			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}
		return pwd.toString();
	}
}
