package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ht.ussp.uc.app.domain.HtBoaInUserRole;

/**
 * 
 * @ClassName: HtBoaInUserRoleRepository
 * @Description: 用户角色关联持久层
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月10日 下午10:21:01
 */
public interface HtBoaInUserRoleRepository extends JpaRepository<HtBoaInUserRole, Long>{
	
	/**
	 * 
	  * @Title: findRoleCodes 
	  * @Description: 根据用户ID查询用户编码 
	  * @return List<String>
	  * @throws
	 */
	@Query(value="select ROLE_CODE from HT_BOA_IN_USER_ROLE where USER_ID= :userId",nativeQuery=true)
	public List<String> queryRoleCodes(@Param("userId") String userId);
}
