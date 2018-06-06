package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.ht.ussp.uc.app.domain.HtBoaInUserRole;
import com.ht.ussp.uc.app.model.BoaInRoleInfo;

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
	
	public HtBoaInUserRole findById(Long id);
	 
	@Query("SELECT new com.ht.ussp.uc.app.model.BoaInRoleInfo(u.roleCode, u.roleName, u.roleNameCn,  u.status, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,ur.delFlag,ur.id,u.app) "
			+ "FROM HtBoaInUserRole ur ,HtBoaInRole u    WHERE  ur.roleCode = u.roleCode AND  (u.roleCode LIKE ?1 OR u.roleName LIKE ?1 OR u.roleNameCn LIKE ?1 ) and ur.userId=?2 GROUP BY u")
	public Page<BoaInRoleInfo> listUserRoleByPageWeb(Pageable arg0, String search,String userId);

	@Query("SELECT new com.ht.ussp.uc.app.model.BoaInRoleInfo(u.roleCode, u.roleName, u.roleNameCn,  u.status, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,ur.delFlag,ur.id,u.app) "
			+ "FROM HtBoaInUserRole ur ,HtBoaInRole u    WHERE  ur.roleCode = u.roleCode AND  u.roleCode =?1 and ur.userId=?2 GROUP BY u")
	public List<BoaInRoleInfo> getUserRoleList(String roleCode, String userId);

	public void deleteByRoleCode(String roleCode);

	public List<HtBoaInUserRole> findByRoleCode(String roleCode);

	public void deleteByUserId(String userId);

	@Transactional
	public void deleteByUserIdAndRoleCode(String userId, String roleCode);
	
	
	/*@Query(value="select  r.id,r.role_code, r.role_Name, r.role_Name_Cn,  r.status, r.create_Operator, r.created_Datetime, r.update_Operator, r.last_Modified_Datetime,r.del_Flag"
			+ " from HT_BOA_IN_USER_ROLE ur left join HT_BOA_IN_ROLE r on ur.role_Code = r.role_Code  WHERE r.role_Name_cn like ?1 and ur.user_Id=?2", 
			nativeQuery=true)
	public List  listUserRoleByPageWeb( String search,String userId,String userRoleCodeStr);*/
}
