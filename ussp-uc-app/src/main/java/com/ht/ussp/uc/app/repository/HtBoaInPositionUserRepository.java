package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ht.ussp.uc.app.domain.HtBoaInPositionUser;
import com.ht.ussp.uc.app.model.BoaInPositionInfo;

/**
 * 
 * @ClassName: HtBoaInPositionUserRepository
 * @Description: 用户岗位持久层
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月11日 上午10:22:45
 */
public interface HtBoaInPositionUserRepository extends JpaRepository<HtBoaInPositionUser, Long> {
	
	@Query(value="select POSITION_CODE from HT_BOA_IN_POSITION_USER where USER_ID= :userId",nativeQuery=true)
	public List<String> queryPositionCodes(@Param("userId") String userId);
	
	 public HtBoaInPositionUser findById(Long id);
	 
	
	 @Query("SELECT new com.ht.ussp.uc.app.model.BoaInPositionInfo (pu.id,u.positionCode, u.positionName, u.positionNameCn, o0.orgCode, o0.orgName, o0.orgNameCn, o0.orgType, o1.orgCode, o1.orgName, o1.orgNameCn, o1.orgType, u.orgPath, u.sequence, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,pu.delFlag,u.status) FROM HtBoaInPosition u, HtBoaInOrg o0, HtBoaInOrg o1, HtBoaInPositionUser pu, HtBoaInUser r, HtBoaInPositionRole pr, HtBoaInRole l WHERE (( u.positionCode = pu.positionCode AND pu.userId = r.userId OR u.positionCode = pr.positionCode AND pr.roleCode = l.roleCode )) AND ( u.positionCode LIKE ?1 OR u.positionName LIKE ?1 OR u.positionNameCn LIKE ?1 ) AND pu.userId = ?2  GROUP BY u")
	 public Page<BoaInPositionInfo> listPositionUserByPageWeb(Pageable arg0, String search,String userId);
	 
	 @Query("SELECT new com.ht.ussp.uc.app.model.BoaInPositionInfo (pu.id,u.positionCode, u.positionName, u.positionNameCn,  u.orgPath, u.sequence, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,pu.delFlag,u.status) FROM HtBoaInPosition u,   HtBoaInPositionUser pu WHERE u.positionCode = pu.positionCode   AND ( u.positionCode LIKE ?1 OR u.positionName LIKE ?1 OR u.positionNameCn LIKE ?1 ) AND pu.userId = ?2  GROUP BY u")
	 public Page<BoaInPositionInfo> listPositionUserByPage(Pageable arg0, String search,String userId);

	 @Query("SELECT new com.ht.ussp.uc.app.model.BoaInPositionInfo (pu.id,u.positionCode, u.positionName, u.positionNameCn,  u.orgPath, u.sequence, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,pu.delFlag,u.status) FROM HtBoaInPosition u,   HtBoaInPositionUser pu WHERE u.positionCode = pu.positionCode   AND u.positionCode = ?1 AND pu.userId = ?2  GROUP BY u")
	 public List<BoaInPositionInfo> getPositionUser(String positionCode, String userId);

}
