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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.*;

/**
 * 数据对象转换工具类<br>
 * 1、支持Map转实体<br>
 * 2、支持实体转Map<br>
 *
 * @author 谭荣巧
 * @Date 2018/1/11 17:13
 */
public class DtoUtil {

    /**
     * Map转实体<br>
     *
     * @param map    Map对象
     * @param entity 要转换的实体实例
     * @return E 实体类型
     * @author 谭荣巧
     * @Date 2018/1/11 19:47
     */
    public static <E> E mapToEntity(Map<String, String> map, E entity) {
        // 获取实体的class对象
        Class<? extends Object> e_class = entity.getClass();
        for (Map.Entry<String, String> entry : map.entrySet()) { // 遍历所有属性
            String m_name = entry.getKey(); // 获取属性的名字
            Object obj = entry.getValue(); // 映射的字段属性名，用于与实体类字段属性进行比对和获值，默认是dto自身的属性名
            boolean updatable = true;// 是否更新到数据
            // 获取实体的所有字段属性
            Field[] e_fields = e_class.getDeclaredFields();
            for (Field e_field : e_fields) { // 遍历所有属性
                e_field.setAccessible(true);
                String e_name = e_field.getName(); // 获取属性的名字
                if (e_name != null && e_name.equals(m_name)) {
                    try {
                        if (updatable) {
                            Class<?> e_typeClass = e_field.getType();
                            // 把dto的字符属性值赋给entity
                            e_field.set(entity, dataConversion(e_typeClass, obj));
                            break;
                        }
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        throw new RuntimeException(MessageFormat.format("通过反射给字段赋值失败，字段名：\"{0}\"，非法参数：\"{1}\"、\"{2}\"", e_field.getName(), entity, obj), e);
                    }
                }
            }
        }
        return entity;
    }

    /**
     * 自定义数据对象转换成实体<br>
     *
     * @param <E>    返回的实体对象类型
     * @param dto    自定义数据对象
     * @param entity 实体对象
     * @return 返回实体
     */
    public static <E extends Object> E dtoToEntity(Object dto, E entity) {
        // 获取实体的class对象
        Class<? extends Object> e_class = entity.getClass();
        // 获取Dto的class对象
        Class<? extends Object> d_class = dto.getClass();
        // 获取Dto的所有字段属性
        Field[] d_fields = getAllDeclaredField(d_class);
        for (Field d_field : d_fields) { // 遍历所有属性
            d_field.setAccessible(true);
            Class<?> d_typeClass = d_field.getType();
            String separator = "/";// 分隔符（连接符）
            String[] separatorMapping = null;// 连接前对应的实体字段属性
            String d_name = d_field.getName(); // 获取属性的名字
            String m_name = d_field.getName(); // 映射的字段属性名，用于与实体类字段属性进行比对和获值，默认是dto自身的属性名
            String format = "";// 日期格式
            boolean updatable = true;// 是否更新到数据
//            // 判断是否设置注解
//            if (d_field.isAnnotationPresent(DtoField.class)) {
//                DtoField dtoField = (DtoField) d_field.getAnnotation(DtoField.class);
//                m_name = dtoField.mapping();
//                if (StringUtil.isEmpty(m_name)) {
//                    m_name = d_name;
//                }
//                format = dtoField.format();
//                updatable = dtoField.updatable();
//                separator = dtoField.separator();
//                separatorMapping = dtoField.separatorMapping();
//            }
            Object obj = null;
            try {
                // 获取dto的字段属性值
                obj = d_field.get(dto);
            } catch (IllegalAccessException | IllegalArgumentException e1) {
                throw new RuntimeException(MessageFormat.format("通过反射获取字段值失败，字段名：\"{0}\"，非法参数：\"{1}\"", d_field.getName(), dto), e1);
            }
            // 分隔符(separator)和分割映射字段屬性(separatorMapping)如果不为空，则进行字段属性分割操作
            if (!StringUtil.isEmpty(separator) && separatorMapping != null && separatorMapping.length > 0) {
                // dto的字段属性必须是String类型，且属性值不允许为空
                if (obj != null && d_typeClass == String.class) {
                    // 待分割的属性值
                    String str = obj.toString();
                    // 分割后的字段属性值
                    String[] strs = str.trim().split(separator);
                    int count = strs.length;
                    for (int i = 0; i < count; i++) {
                        // 如果分割出来的字段属性值，比配置的mappings要多，则出来的字段属性值忽略
                        if (separatorMapping.length >= i) {
                            // 数组下标获取对应的字段属性
                            try {
                                Field field = e_class.getDeclaredField(separatorMapping[i]);
                                field.setAccessible(true);
                                if (field != null) {
                                    try {
                                        field.set(entity, strs[i].trim());
                                    } catch (IllegalArgumentException | IllegalAccessException e) {
                                        throw new RuntimeException(MessageFormat.format("通过反射给字段赋值失败，字段名：\"{0}\"，非法参数：\"{1}\"、\"{2}\"", field.getName(), entity, obj), e);
                                    }
                                }
                            } catch (NoSuchFieldException | SecurityException e) {
                                throw new RuntimeException(MessageFormat.format("通过反射获取{0}类的{1}属性的set方法", e_class.getName(), separatorMapping[i]), e);
                            }
                        }
                    }
                }
            } else {
                // 获取实体的所有字段属性
                Field[] e_fields = e_class.getDeclaredFields();
                for (Field e_field : e_fields) { // 遍历所有属性
                    e_field.setAccessible(true);
                    String e_name = e_field.getName(); // 获取属性的名字
                    if (e_name != null && !"serialVersionUID".equals(e_name) && e_name.equals(m_name)) {
                        try {
                            if (updatable) {
                                // 把dto的字符属性值赋给entity
                                e_field.set(entity, dataConversion(e_field.getType(), obj));
                                break;
                            }
                        } catch (IllegalAccessException | IllegalArgumentException e) {
                            throw new RuntimeException(MessageFormat.format("通过反射给字段赋值失败，字段名：\"{0}\"，非法参数：\"{1}\"、\"{2}\"", e_field.getName(), entity, obj), e);
                        }
                    }
                }
            }
        }
        return entity;
    }

    /**
     * 实体转换成自定义数据对象<br>
     *
     * @param <E>    返回的自定义传输对象
     * @param entity 实体对象
     * @param dCalss 自定义数据对象的Class
     * @return 自定义传输对象
     */
    public static <E> E entityToDto(Object entity, Class<E> dCalss) {
        Class<? extends Object> e_class = entity.getClass();
        Class<E> d_class = dCalss;
        E dto = null;
        try {
            // 实例化对象
            dto = d_class.newInstance();
        } catch (InstantiationException | IllegalAccessException e2) {
            throw new RuntimeException(MessageFormat.format("通过反射实例对象失败，实例化的类名是：\"{0}\"", d_class.getName()), e2);
        }
        Field[] d_fields = getAllDeclaredField(d_class);
        for (Field d_field : d_fields) { // 遍历所有属性
            d_field.setAccessible(true);
            Class<?> d_typeClass = d_field.getType();
            String d_name = d_field.getName();// 获取属性的名字
            String m_name = d_field.getName();// 映射的字段属性名，用于与实体类字段属性进行比对和获值，默认是dto自身的属性名
            String format = "";// 日期格式
            String separator = "/";// 分隔符（连接符）
            String[] separatorMapping = null;// 连接前对应的实体字段属性
            // 判断是否设置注解
//            if (d_field.isAnnotationPresent(DtoField.class)) {
//                DtoField dtoField = (DtoField) d_field.getAnnotation(DtoField.class);
//                m_name = dtoField.mapping();
//                if (StringUtil.isEmpty(m_name)) {
//                    m_name = d_name;
//                }
//                format = dtoField.format();
//                separator = dtoField.separator();
//                separatorMapping = dtoField.separatorMapping();
//            }
            // 分隔符(separator)和分割映射字段屬性(separatorMapping)如果不为空，则进行字段属性合并操作
            if (!StringUtil.isEmpty(separator) && separatorMapping != null && separatorMapping.length > 0) {
                // dto的字段属性必须是String类型
                if (d_typeClass == String.class) {
                    int count = separatorMapping.length;
                    StringBuilder mergerStr = new StringBuilder();
                    for (int i = 0; i < count; i++) {
                        // 如果分割出来的字段属性值，比配置的mappings要多，则出来的字段属性值忽略
                        if (separatorMapping.length >= i) {
                            // 数组下标获取对应的字段属性
                            try {
                                Field field = e_class.getDeclaredField(separatorMapping[i]);
                                field.setAccessible(true);
                                if (field != null) {
                                    try {
                                        Object obj = field.get(entity);
                                        if (obj != null) {
                                            if (i > 0) {
                                                mergerStr.append(separator);
                                            }
                                            mergerStr.append(obj.toString());
                                        }
                                    } catch (IllegalArgumentException | IllegalAccessException e) {
                                        throw new RuntimeException(MessageFormat.format("通过反射获取字段值失败，字段名：\"{0}\"，非法参数：\"{1}\"", field.getName(), entity), e);
                                    }
                                }
                            } catch (NoSuchFieldException | SecurityException e) {
                                throw new RuntimeException(MessageFormat.format("通过反射获取{0}类的{1}属性的set方法", e_class.getName(), separatorMapping[i]), e);
                            }
                        }
                    }
                    // 合并以后，赋值
                    try {
                        d_field.set(dto, mergerStr.toString());
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException(MessageFormat.format("通过反射给字段赋值失败，字段名：\"{0}\"，非法参数：\"{1}\"、\"{2}\"", d_field.getName(), dto, mergerStr), e);
                    }
                }
            } else {
                Field[] e_fields = e_class.getDeclaredFields();
                for (Field e_field : e_fields) { // 遍历所有属性
                    e_field.setAccessible(true);
                    String e_name = e_field.getName(); // 获取属性的名字
                    if (e_name != null && !"serialVersionUID".equals(e_field) && e_name.equals(m_name)) {
                        Object obj = null;
                        try {
                            // 获取entity的字段属性值
                            obj = e_field.get(entity);
                        } catch (IllegalAccessException | IllegalArgumentException e1) {
                            throw new RuntimeException(MessageFormat.format("通过反射获取字段值失败，字段名：\"{0}\"，非法参数：\"{1}\"", e_field.getName(), entity), e1);
                        }
                        try {
                            Class<?> e_typeClass = e_field.getType();

                            if (obj != null && d_typeClass == String.class && !StringUtil.isEmpty(obj.toString())) {
                                if (e_typeClass == BigDecimal.class) {
                                    if (!StringUtil.isEmpty(format)) {
                                        obj = new DecimalFormat(format).format(obj);
                                    } else {
                                        obj = obj.toString();
                                    }
                                } else if (e_typeClass == Date.class) {
                                    if (StringUtil.isEmpty(format)) {
                                        format = "yyyy-MM-dd HH:mm:ss";
                                    }
                                    obj = DateUtil.formatDate(format, (Date) obj);
                                } else if (e_typeClass == int.class || e_typeClass == Integer.class) {
                                    if (!StringUtil.isEmpty(format)) {
                                        obj = new DecimalFormat(format).format(obj);
                                    } else {
                                        obj = obj.toString();
                                    }
                                } else if (e_typeClass == double.class || e_typeClass == Double.class) {
                                    if (!StringUtil.isEmpty(format)) {
                                        obj = new DecimalFormat(format).format(obj);
                                    } else {
                                        obj = obj.toString();
                                    }
                                }
                            } else if (obj == null) {
                                if (d_typeClass == int.class || d_typeClass == double.class) {
                                    obj = 0;
                                }
                            }
                            // 把entity的字段属性值赋给dto
                            d_field.set(dto, obj);
                            break;
                        } catch (IllegalAccessException | IllegalArgumentException e) {
                            throw new RuntimeException(MessageFormat.format("通过反射给字段赋值失败，字段名：\"{0}\"，非法参数：\"{1}\"、\"{2}\"", d_field.getName(), dto, obj), e);
                        }
                    }
                }
            }
        }
        return dto;
    }

    /**
     * 通过属性名获取Dto的属性值<br>
     *
     * @param <D>           dto数据对象类型
     * @param dto           dto数据对象
     * @param attributeName 属性名
     * @return 属性值
     */
    public static <D extends Object> Object getDtoAttributeValue(D dto, String attributeName) {
        if (attributeName == null) {
            return null;
        }
        Field field;
        try {
            field = dto.getClass().getDeclaredField(attributeName);
            field.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(MessageFormat.format("通过反射获取{0}字段失败", attributeName), e);
        }
        try {
            return field.get(dto);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(MessageFormat.format("通过反射获取字段值失败，字段名：\"{0}\"，非法参数：\"{1}\"", attributeName, dto), e);
        }
    }

    /**
     * 数据转换<br>
     *
     * @param e_typeClass
     * @param obj
     * @return java.lang.Object
     * @author 谭荣巧
     * @Date 2018/1/11 19:45
     */
    private static Object dataConversion(Class<?> e_typeClass, Object obj) {

        // 当传入的值为空值或者空字符串时
        if (obj == null || StringUtil.isEmpty(obj.toString())) {
            if (e_typeClass == int.class || e_typeClass == Integer.class) {
                obj = 0;
            } else if (e_typeClass == BigDecimal.class) {
                obj = null;
            } else if (e_typeClass == Date.class) {
                obj = null;
            } else if (e_typeClass == double.class || e_typeClass == Double.class) {
                obj = 0;
            }
        } else {// 但是传入的参数类型为String，并且obj不为空时
            if (e_typeClass == BigDecimal.class) {
                obj = new BigDecimal(obj.toString());
            } else if (e_typeClass == Date.class) {
                obj = DateUtil.getDate(obj.toString(), "yyyy-MM-dd HH:mm:ss");
            } else if (e_typeClass == int.class || e_typeClass == Integer.class) {
                obj = Integer.parseInt(obj.toString());
            } else if (e_typeClass == double.class || e_typeClass == Double.class) {
                obj = Double.parseDouble(obj.toString());
            }
        }
        return obj;
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     *
     * @param clazz Class对象
     * @return 所有属性对象
     */
    private static Field[] getAllDeclaredField(Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            } catch (Exception e) {
                // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会执行clazz =
                // clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
        return fields.toArray(new Field[0]);
    }
}
