package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.uc.app.domain.HtBoaInUserApp;

/**
 *
 * @ClassName: HtBoaInUserAppRepository
 * @Description: 用户和系统关联持久层
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月9日 下午5:40:24
 */
public interface HtBoaInUserAppRepository extends JpaRepository<HtBoaInUserApp,Long>{

	/**
	 *
	 * @Title: findByuserId
	 * @Description: 通过用户ID查找系统信息
	 * @return HtBoaInUserApp
	 * @throws
	 */
	public HtBoaInUserApp findByuserId(String userId);

}