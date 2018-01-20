package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ht.ussp.uc.app.domain.HtBoaInUserApp;
import com.ht.ussp.uc.app.model.BoaInAppInfo;

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
	public List<HtBoaInUserApp> findByuserId(String userId);

	public HtBoaInUserApp findById(Long id);
	
	@Query("SELECT new com.ht.ussp.uc.app.model.BoaInAppInfo(u.app, u.name, u.nameCn,  u.status, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,ur.delFlag,ur.id) "
			+ "FROM HtBoaInUserApp ur ,HtBoaInApp u    WHERE  ur.app = u.app AND  (u.app LIKE ?1 OR u.name LIKE ?1 OR u.nameCn LIKE ?1 ) and ur.userId=?2 GROUP BY u")
	public Page<BoaInAppInfo> listUserAppByPageWeb(Pageable arg0, String search,String userId);
	
	@Query("SELECT new com.ht.ussp.uc.app.model.BoaInAppInfo(u.app, u.name, u.nameCn,  u.status, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,ur.delFlag,ur.id) "
			+ "FROM HtBoaInUserApp ur ,HtBoaInApp u    WHERE  ur.app = u.app AND  (u.app LIKE ?1 OR u.name LIKE ?1 OR u.nameCn LIKE ?1 )  GROUP BY u")
	public Page<BoaInAppInfo> listAllUserAppByPage(Pageable arg0, String search);
	
}