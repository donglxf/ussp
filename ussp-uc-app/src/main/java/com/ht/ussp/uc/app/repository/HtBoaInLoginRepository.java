package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.uc.app.domain.HtBoaInUser;

/**
 * 
 * @ClassName: HtBoaInUserRepository
 * @Description: 登录信息表持久层
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月8日 上午11:35:49
 */
public interface HtBoaInLoginRepository extends JpaRepository <HtBoaInUser, Long>{
	
  /**
   * 
   * @Title: findByUserId 
   * @Description: 通过用户ID查询登录信息 
   * @return HtBoaInUser
   * @throws
   */
	public HtBoaInUser findByUserId(String userId);
	
}
