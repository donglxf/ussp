package com.ht.ussp.uc.app.repository;

import java.util.List;

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
}
