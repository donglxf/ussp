package com.ht.ussp.uc.app.util;


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
	
}