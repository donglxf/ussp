/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: 谭荣巧
 * Author:   tanrq
 * Date:     2018/1/11 17:13
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类<br>
 *
 * @author 谭荣巧
 * @Date 2018/1/11 17:13
 */
public class StringUtil {
    /**
     * 判断内容是否需要分割.<br>
     *
     * @param str    待判断的字符串
     * @param length 判断的长度
     * @return true：需要;false：不需要
     */
    public static boolean checkContent(String str, int length) {
        if (str.getBytes().length > length) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 截取指定字符串的某截内容<br>
     *
     * @param src        原始字符串
     * @param beginIndex 开始位置以英文字符为标准
     * @param length     要截取的长度,以英文字符为标准,1个中文字符=2个英文字符
     * @return 截取后的字符串
     */
    public static String getString(String src, int beginIndex, int length) {
        if (beginIndex < 0) {
            beginIndex = 0;
        }
        if (src == null || beginIndex >= src.length()) {
            return "";
        }
        if (beginIndex == 0 && !checkContent(src, length)) {
            return src;
        }
        int maxlength = src.length();
        int realLength = length;
        int count = length;
        Character c = '0';
        for (int i = beginIndex; count > 0 && i < maxlength; i++) {
            c = src.charAt(i);
            int i1 = c.compareTo((char) 127);
            int i2 = c.compareTo((char) 0);
            if (i1 > 0 || i2 < 0) {// 判断是否为非ascii字符
                realLength--;
                count = count - 2;
            } else {
                count--;
            }
        }
        int endIndex = beginIndex + realLength;
        if (endIndex > maxlength) {
            endIndex = maxlength;
        }
        return src.substring(beginIndex, endIndex);
    }

    /**
     * 去掉字符串前后的空格，如果为空则返回空字符串<br>
     *
     * @param str 待处理的字符串
     * @return 去掉空格后的字符串
     * @since baothink-framework-core 0.0.1
     */
    public static String clear(String str) {
        return nvl(str, "").trim();
    }

    /**
     * 去掉字符中所有空格<br>
     *
     * @param str 待处理的字符串
     * @return 处理后的字符串
     * @since baothink-framework-core 0.0.1
     */
    public static String clearAll(String str) {
        return nvl(str, "").replaceAll("\\s*", "");
    }

    /**
     * 判断字符串是否为空<br>
     * 为空的判断规则为：字符串为空或者长度等于0
     *
     * @param str 待判断的字符串
     * @return true：字符串为空<br>
     * false：字符串不为空
     * @since baothink-framework-core 0.0.1
     */
    public static boolean isEmpty(String str) {
        return "".equals(clear(str));
    }

    /**
     * 判断参数a是否为空，如果为空则返回b (适用与字符串等所有对象)<br>
     *
     * @param <K> 任意对象类型
     * @param a   待判断的对象（任意对象）
     * @param b   为空时返回的对象（跟a对象必须是同一个对象类型）
     * @return 为空则返回b对象，不为空则返回a对象
     * @since baothink-framework-core 0.0.1
     */
    public static <K> K nvl(K a, K b) {
        return a == null ? b : a;
    }

    /**
     * 以指定字符把字符串分割成数组<br>
     * <br>
     *
     * @param str      待分割字符串
     * @param splitStr 分割字符
     * @return 分割后的字符串数组
     * @since baothink-framework-core 0.0.1
     */
    public static String[] splitString(String str, String splitStr) {
        String[] arrayOfString = null;
        String str1 = "[\\s]+";
        if (splitStr != null)
            str1 = new StringBuilder().append("[\\s").append(splitStr).append("]+").toString();
        try {
            Pattern localPattern = Pattern.compile(str1);
            Matcher localMatcher = localPattern.matcher(str);
            String str2 = localMatcher.replaceAll(splitStr);
            localPattern = Pattern.compile(splitStr);
            arrayOfString = localPattern.split(str2);
        } catch (Exception localException) {
            localException.printStackTrace();
        } finally {
        }
        return arrayOfString;
    }
}
