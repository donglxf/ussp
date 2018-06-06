/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: PatternUtil.java
 * Author:   谭荣巧
 * Date:     2018/1/24 14:00
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.util;

/**
 * 路径匹配器，支持通配符“*”<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/1/24 14:00
 */
public class PatternUtil {
    private static final char ESCAPES[] = {'$', '^', '[', ']', '(', ')', '{',
            '|', '+', '\\', '.', '<', '>'};

    public static PatternUtil compile(String pattern) {
        return new PatternUtil(pattern);
    }

    private PatternUtil(String pattern) {
        regexp = wildcardToRegexp(pattern);
    }

    private String regexp;

    public boolean match(String input) {
        if (input == null) {
            return false;
        }
        return input.matches(this.regexp);
    }

    private String wildcardToRegexp(String pattern) {
        String result = "^";

        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            boolean isEscaped = false;
            for (int j = 0; j < ESCAPES.length; j++) {
                if (ch == ESCAPES[j]) {
                    result += "\\" + ch;
                    isEscaped = true;
                    break;
                }
            }

            if (!isEscaped) {
                if (ch == '*') {
                    result += ".*";
                } else if (ch == '?') {
                    result += ".";
                } else {
                    result += ch;
                }
            }
        }
        result += "$";
        return result;
    }
}
