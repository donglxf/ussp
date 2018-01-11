package com.ht.ussp.gateway.app.util;

import java.io.IOException;
import java.util.List;  

import com.fasterxml.jackson.core.JsonProcessingException;  
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;  

/**
 * 
 * @ClassName: FastJson
 * @Description: json工具类
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月9日 上午10:10:49
 */
public class FastJsonUtil {
	static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * 
	 * @Title: objectToJson 
	 * @Description: 将对象转换成json字符串 
	 * @return String
	 * @throws
	 */
	public static String objectToJson(Object data) {
		try {
			String string = MAPPER.writeValueAsString(data);
			return string;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @Title: jsonToPojo 
	 * @Description: 将json结果集转化为对象 
	 * @return T
	 * @throws
	 */
	public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
		try {
			T t = MAPPER.readValue(jsonData, beanType);
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	  * @Title: objectToPojo 
	  * @Description: 将对象转为POJO 
	  * @return T
	  * @throws
	 */
	public static <T> T objectToPojo(Object data, Class<T> beanType) {
		try {
			String str = MAPPER.writeValueAsString(data);
			T t = MAPPER.readValue(str, beanType);
			return t;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @Title: jsonToList 
	 * @Description: 将json数据转换成pojo对象list 
	 * @return List<T>
	 * @throws
	 */
	public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {
		JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
		try {
			List<T> list = MAPPER.readValue(jsonData, javaType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	

}
