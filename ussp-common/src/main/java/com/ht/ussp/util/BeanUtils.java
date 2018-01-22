package com.ht.ussp.util;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;


/**
 * 
 * @ClassName: BeanUtils
 * @Description: Bean工具类——提供了常用的bean操作，继承自Spring的org.springframework.beans.BeanUtils，并添加了一些新的方法
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月9日 下午9:52:18
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {

	private static Mapper dozerMapper = new DozerBeanMapper(); 
	
	/** 
	 * 将一个对象的内容，深拷贝到一个新对象（如果仅仅是浅拷贝时，请使用BeanUtils.copyProperties方法，性能会更好）
	 * @param sourceObject 源对象
	 * @param destinationClass 目标对象的Class
	 * @return  拷贝好的目标对象
	 * @throws 
	 */
	public static <T> T deepCopy(Object sourceObject, Class<T> destinationClass) {
		return dozerMapper.map(sourceObject, destinationClass);
	}
	
	/** 
	 * 将一个对象的内容，深拷贝到一个新对象（如果仅仅是浅拷贝时，请使用BeanUtils.copyProperties方法，性能会更好）
	 * @param sourceObject 源对象
	 * @param destinationObject  目标对象
	 * @throws 
	 */
	public static void deepCopy(Object sourceObject, Object destinationObject) {
		dozerMapper.map(sourceObject, destinationObject);
	}
	
	/**
     * 清理Dao对象内所有属性，父类属性清除不了
     * 适用于实体对象按照属性数据库查询初始化使用
     * 
     * @param obj
     */
    public static void setObjectFieldsEmpty(Object obj) {
        Class<?> objClass = obj.getClass();
        Method[] objmethods = objClass.getDeclaredMethods();
        Map<String, Method> objMeMap = new HashMap<String, Method>();
        for (int i = 0; i < objmethods.length; i++) {
            Method method = objmethods[i];
            objMeMap.put(method.getName(), method);
        }
        for (int i = 0; i < objmethods.length; i++) {
            {
                String methodName = objmethods[i].getName();
                if (methodName != null && methodName.startsWith("get")) {
                    try {
                        Object returnObj = objmethods[i].invoke(obj,
                                new Object[0]);
                        Method setmethod = (Method) objMeMap
                                .get("set" + methodName.split("get")[1]);
                        if (returnObj != null) {
                            returnObj = null;
                        }
                        setmethod.invoke(obj, returnObj);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
	
    /** 
     * (1)能匹配的年月日类型有： 
     *    2014年4月19日 
     *    2014年4月19号 
     *    2014-4-19 
     *    2014/4/19 
     *    2014.4.19 
     * (2)能匹配的时分秒类型有： 
     *    15:28:21 
     *    15:28 
     *    5:28 pm 
     *    15点28分21秒 
     *    15点28分 
     *    15点 
     * (3)能匹配的年月日时分秒类型有： 
     *    (1)和(2)的任意组合，二者中间可有任意多个空格 
     * 如果dateStr中有多个时间串存在，只会匹配第一个串，其他的串忽略 
     * @param text 
     * @return 
     */  
    public static String matchDateString(String dateStr) {
        try {
            List<String> matches = null;
            Pattern p = Pattern.compile(
                    "(\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号])?(\\s)*(\\d{1,2}([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?)",
                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
            Matcher matcher = p.matcher(dateStr);
            if (matcher.find() && matcher.groupCount() >= 1) {
                matches = new ArrayList<String>();
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String temp = matcher.group(i);
                    matches.add(temp);
                }
            } else {
                matches = Collections.emptyList();
            }
            if (matches.size() > 0) {
                return ((String) matches.get(0)).trim();
            } else {
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }
	
}