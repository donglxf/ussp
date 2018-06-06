package com.ht.ussp.util;


import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by xiayt on 2017/3/24.
 */
public class EnumUtil {
    public static String getValue(Class<?> ref , Object key){
        return parseEnum(ref).get( key + "" ) ;
    }

    public static <T> Map<String, String> parseEnum(Class<T> ref){
        Map<String, String> map = new LinkedHashMap<String, String>() ;
        if(ref.isEnum()){
            T[] ts = ref.getEnumConstants() ;
            for(T t : ts){
                String value = getInvokeValue(t, "getValue") ;
                Enum<?> tempEnum = (Enum<?>) t ;
                if(value == null){
                    value = tempEnum.name() ;
                }
                String key = getInvokeValue(t, "getKey") ;
                if(key == null){
                    key = tempEnum.ordinal()+"";
                }
                map.put(key , value ) ;
            }
        }
        return map ;
    }


    public static <T> T getEnumItem(Class<T> ref , String str){
        T returnT = null ;
        if(ref.isEnum()){
            T[] ts = ref.getEnumConstants() ;
            for(T t : ts){
                Enum<?> tempEnum = (Enum<?>) t ;
                String code = getInvokeValue(t, "getCode") ;
                if(code == null){
                    code = tempEnum.ordinal()+ "";
                }
                if(str.equals(code)){
                    returnT = t;
                    break ;
                }
            }
        }
        return returnT ;
    }

    static <T> String getInvokeValue(T t , String methodName){
        Method method = ReflectionUtils.findMethod(t.getClass() , methodName);
        if(null == method){
            return null ;
        }
        try {
            String text = method.invoke( t ) + "" ;
            return text ;
        } catch (Exception e) {
            return null ;
        }
    }
}
