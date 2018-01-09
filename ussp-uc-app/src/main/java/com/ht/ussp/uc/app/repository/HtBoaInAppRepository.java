package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.uc.app.domain.HtBoaInApp;

/**
 * 
* @ClassName: HtBoaInAppRepository
* @Description: 系统表持久层
* @author wim qiuwenwu@hongte.info
* @date 2018年1月5日 下午6:20:37
 */
public interface HtBoaInAppRepository extends JpaRepository<HtBoaInApp, Long>{
	
	public HtBoaInApp findById(Long id);

}
