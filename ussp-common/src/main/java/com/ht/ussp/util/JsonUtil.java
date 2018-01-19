package com.ht.ussp.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 摘要：JSON工具类
 *
 * @author xyt
 * @version 1.0
 * @Date 2016年12月10日
 */
public class JsonUtil {
    private static SerializeConfig mapping = new SerializeConfig();
    private static String dateFormat;
    static {
        dateFormat = "yyyy-MM-dd HH:mm:ss";
    }

    /**
     * 将对象转为JSON字符串,显示NULL的字段
     */
    public static String obj2Str(Object object) {
        return JSON.toJSONString(object,  SerializerFeature.WriteMapNullValue);
    }

    /**
     * 将JSON字符串转换成对象
     *
     * @param jsonStr
     * @param cls
     * @return
     */
    public static <T> T json2Obj(String jsonStr, Class<T> cls) {
        return JSON.parseObject(jsonStr, cls);
    }

    /**
     * 将JSON字符串转成list
     *
     * @param jsonStr
     * @param cls
     * @return
     */
    public static <T> List<T> json2List(String jsonStr, Class<T> cls) {
        return JSON.parseArray(jsonStr, cls);
    }

    /**
     * 将JSON字符串转成map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> json2Map(String jsonStr) {
        return (Map<String, Object>) JSON.parse(jsonStr);
    }

    /**
     * 利用Introspector和PropertyDescriptor 将Bean --> Map
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> obj2Map(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    map.put(key, value);
                }

            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }
        return map;
    }

    // Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean
    public static <T> T  map2Obj(Map<String, Object> map, Class<T> cls) {

        try {
            T obj = cls.newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(cls);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }
            }
            return obj;

        } catch (Exception e) {
            System.out.println("transMap2Bean Error " + e);
        }

        return null;

    }

    /**
     * 自定义时间格式
     *
     * @param jsonText
     * @return
     */
    public static String toJSON(String dateFormat, String jsonText) {
        mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
        return JSON.toJSONString(jsonText, mapping);
    }

}
