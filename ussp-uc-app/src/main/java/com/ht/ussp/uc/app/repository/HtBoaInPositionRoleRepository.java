package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ht.ussp.uc.app.domain.HtBoaInPositionRole;

/**
 * 
 * @ClassName: HtBoaInPositionRoleRepository
 * @Description: 岗位角色持久层
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月11日 上午10:25:39
 */
public interface HtBoaInPositionRoleRepository extends JpaRepository <HtBoaInPositionRole, Long>{

	@Query(value="select ROLE_CODE from HT_BOA_IN_POSITION_ROLE where POSITION_CODE in (:positionCodes)",nativeQuery=true)
	public List<String> queryRoleCodeByPosition(@Param("positionCodes") List<String> positionCodes);
	
	public HtBoaInPositionRole findById(Long id);

    @Modifying
    @Query("UPDATE FROM HtBoaInPositionRole u SET u.delFlag = 1 WHERE u.positionCode = ?1 AND u.roleCode = ?2")
    void delete(String positionCode, String roleCode);
    
    
    @Query("SELECT new com.ht.ussp.uc.app.model.BoaInRoleInfo(u.roleCode, u.roleName, u.roleNameCn,  u.status, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,ur.delFlag,ur.id,u.app) "
			+ "FROM HtBoaInPositionRole ur ,HtBoaInRole u    WHERE  ur.roleCode = u.roleCode AND  (u.roleCode LIKE ?1 OR u.roleName LIKE ?1 OR u.roleNameCn LIKE ?1 ) and ur.positionCode=?2 GROUP BY u")
	public Page<HtBoaInPositionRole> listPositionRoleByPageWeb(Pageable arg0, String search,String positionCode);

    @Query("SELECT new com.ht.ussp.uc.app.model.BoaInRoleInfo(u.roleCode, u.roleName, u.roleNameCn,  u.status, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,ur.delFlag,ur.id,u.app) "
			+ "FROM HtBoaInPositionRole ur ,HtBoaInRole u    WHERE  ur.roleCode = u.roleCode AND  u.roleCode = ?1 and ur.positionCode=?2 GROUP BY u")
	public List<HtBoaInPositionRole> getPositionRoleList(String roleCode, String positionCode);
    
}
