package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.uc.app.domain.HtBoaInUser;

 /**
  * 
  * @ClassName: HtBoaInUserRepository
  * @Description: 用户表持久层
  * @author wim qiuwenwu@hongte.info
  * @date 2018年1月8日 上午11:35:49
  */
public interface HtBoaInUserRepository extends JpaRepository <HtBoaInUser, Long>{
	
   /**
	* 
	* @Title: findByUserName 
	* @Description: 通过用户名查询用户信息 
	* @return HtBoaInUser
	* @throws
	*/
	public HtBoaInUser findByUserName(String userName);

}
