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

	HtBoaInUserApp findById(Long id);
	
	@Query("SELECT new com.ht.ussp.uc.app.model.BoaInAppInfo(u.app, u.name, u.nameCn,  u.status, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,ur.delFlag,ur.id) "
			+ "FROM HtBoaInUserApp ur ,HtBoaInApp u    WHERE  ur.app = u.app AND  (u.app LIKE ?1 OR u.name LIKE ?1 OR u.nameCn LIKE ?1 ) and ur.userId=?2 GROUP BY u")
	Page<BoaInAppInfo> listUserAppByPageWeb(Pageable arg0, String search,String userId);
	
	@Query("SELECT new com.ht.ussp.uc.app.model.BoaInAppInfo(u.app, u.name, u.nameCn,  u.status, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,u.delFlag,u.id) "
			+ "FROM HtBoaInApp u    WHERE ( u.app LIKE ?1 OR u.name LIKE ?1 OR u.nameCn LIKE ?1 ) AND u.status=0 AND  u.delFlag=0  GROUP BY u")
	Page<BoaInAppInfo> listAllUserAppByPage(Pageable arg0, String search);

	/**
     * 通过用户编码和系统编码查找他们的关系<br>
     *
     * @param userID 用户编码
     * @param app    系统编码
     * @return 用户与系统的关系
     * @author 谭荣巧
     * @Date 2018/1/22 21:41
     */
    HtBoaInUserApp findByUserIdAndApp(String userID, String app);
}