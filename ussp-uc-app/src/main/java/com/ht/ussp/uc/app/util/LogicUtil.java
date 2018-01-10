package com.ht.ussp.uc.app.util;

import java.util.Collection;
import java.util.Map;

import com.ht.ussp.uc.app.common.Constants;


/**
 * 
 * @ClassName: LogicUtil
 * @Description: 简单逻辑判断
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月9日 下午5:54:25
 */
public class LogicUtil {

    @SuppressWarnings("rawtypes")
    public static boolean isNullOrEmpty(Collection collection) {
        if (null == collection || Constants.ZERO == collection.size()) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("rawtypes")
    public static boolean isNotNullAndEmpty(Collection collection) {
        return !isNullOrEmpty(collection);
    }

    @SuppressWarnings("rawtypes")
    public static boolean isNullOrEmpty(Map map) {
        if (null == map || Constants.ZERO == map.size()) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("rawtypes")
    public static boolean isNotNullAndEmpty(Map map) {
        return !isNullOrEmpty(map);
    }

    public static boolean isNullOrEmpty(Object[] objects) {
        if (null == objects || Constants.ZERO == objects.length) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNotNullAndEmpty(Object[] objects) {
        return !isNullOrEmpty(objects);
    }

    public static boolean isNull(Object object) {
        if (object == null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNotNull(Object Object) {
        return !isNull(Object);
    }

    public static boolean isNullOrEmpty(String subject) {
        if (null == subject || Constants.EMPTY_STRING.equals(subject)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNotNullAndEmpty(String subject) {
        return !isNullOrEmpty(subject);
    }

}
