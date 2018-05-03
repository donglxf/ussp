package com.ht.ussp.uc.app.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ht.ussp.uc.app.domain.HtBoaInPosition;
import com.ht.ussp.uc.app.domain.HtBoaInRole;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.model.BoaInPositionInfo;

/**
 * 
 * @ClassName: HtBoaInPositionRepository
 * @Description: 岗位表持久层
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月11日 上午9:19:32
 */
public interface HtBoaInPositionRepository
        extends JpaRepository<HtBoaInPosition, Long> {

    public HtBoaInPosition findById(Long id);

    @Modifying
    @Query("UPDATE FROM HtBoaInPosition u SET u.delFlag = 1 WHERE u.positionCode IN ?1")
    void delete(Set<String> positionCodes);
    
    public List<HtBoaInPosition> findByPositionCodeIn(Set<String> positionCodes);
    
    @Query("SELECT new com.ht.ussp.uc.app.model.BoaInPositionInfo (u.id,u.positionCode, u.positionName, u.positionNameCn, o0.orgCode, o0.orgName, o0.orgNameCn, o0.orgType, o1.orgCode, o1.orgName, o1.orgNameCn, o1.orgType, u.orgPath, u.sequence, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,u.delFlag,u.status) FROM HtBoaInPosition u, HtBoaInOrg o0, HtBoaInOrg o1, HtBoaInPositionUser pu, HtBoaInUser r, HtBoaInPositionRole pr, HtBoaInRole l WHERE (u.parentOrgCode = o0.orgCode AND u.rootOrgCode = o1.orgCode OR ( u.positionCode = pu.positionCode AND pu.userId = r.userId OR u.positionCode = pr.positionCode AND pr.roleCode = l.roleCode )) AND ( u.positionCode LIKE ?1 OR u.positionName LIKE ?1 OR u.positionNameCn LIKE ?1 OR o0.orgCode LIKE ?1 OR o0.orgName LIKE ?1 OR o0.orgNameCn LIKE ?1 OR o0.orgType LIKE ?1 OR o1.orgCode LIKE ?1 OR o1.orgName LIKE ?1 OR o1.orgNameCn LIKE ?1 OR o1.orgType LIKE ?1 OR u.orgPath LIKE ?1 OR u.sequence LIKE ?1 OR u.createOperator LIKE ?1 OR cast(u.createdDatetime as string) LIKE ?1 OR u.updateOperator LIKE ?1 OR cast(u.lastModifiedDatetime as string) LIKE ?1 OR r.userName LIKE ?1 OR l.roleCode LIKE ?1 OR l.roleName LIKE ?1 OR l.roleNameCn LIKE ?1 ) GROUP BY u")
    public List<BoaInPositionInfo> listPositionInfo(String search);
    
    @Query("SELECT new com.ht.ussp.uc.app.model.BoaInPositionInfo (u.id,u.positionCode, u.positionName, u.positionNameCn, o0.orgCode, o0.orgName, o0.orgNameCn, o0.orgType, o1.orgCode, o1.orgName, o1.orgNameCn, o1.orgType, u.orgPath, u.sequence, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,u.delFlag,u.status) FROM HtBoaInPosition u, HtBoaInOrg o0, HtBoaInOrg o1, HtBoaInPositionUser pu, HtBoaInUser r, HtBoaInPositionRole pr, HtBoaInRole l WHERE (u.parentOrgCode = o0.orgCode AND u.rootOrgCode = o1.orgCode OR ( u.positionCode = pu.positionCode AND pu.userId = r.userId OR u.positionCode = pr.positionCode AND pr.roleCode = l.roleCode )) AND ( u.positionCode LIKE ?1 OR u.positionName LIKE ?1 OR u.positionNameCn LIKE ?1 OR o0.orgCode LIKE ?1 OR o0.orgName LIKE ?1 OR o0.orgNameCn LIKE ?1 OR o0.orgType LIKE ?1 OR o1.orgCode LIKE ?1 OR o1.orgName LIKE ?1 OR o1.orgNameCn LIKE ?1 OR o1.orgType LIKE ?1 OR u.orgPath LIKE ?1 OR u.sequence LIKE ?1 OR u.createOperator LIKE ?1 OR cast(u.createdDatetime as string) LIKE ?1 OR u.updateOperator LIKE ?1 OR cast(u.lastModifiedDatetime as string) LIKE ?1 OR r.userName LIKE ?1 OR l.roleCode LIKE ?1 OR l.roleName LIKE ?1 OR l.roleNameCn LIKE ?1 ) GROUP BY u")
    public Page<BoaInPositionInfo> listPositionInfo(Pageable arg0, String search);
    
    @Query("SELECT new com.ht.ussp.uc.app.model.BoaInPositionInfo (u.id,u.positionCode, u.positionName, u.positionNameCn, u.orgPath, u.sequence, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,u.delFlag,u.status,u.parentOrgCode,o0.orgNameCn) FROM HtBoaInPosition u, HtBoaInOrg o0    WHERE u.parentOrgCode = o0.orgCode    AND ( u.positionCode LIKE ?1 OR u.positionName LIKE ?1 OR u.positionNameCn LIKE ?1 ) AND u.parentOrgCode = ?2 AND u.delFlag=0  GROUP BY u")
    public Page<BoaInPositionInfo> listPositionByPageWeb(Pageable arg0, String search,String orgPath);

    @Query("SELECT u FROM HtBoaInUser u, HtBoaInPositionUser pu WHERE pu.positionCode = ?1 AND pu.userId = u.userId")
    public List<HtBoaInUser> listHtBoaInUser(String positionCode);
    
    @Query("SELECT u FROM HtBoaInRole u, HtBoaInPositionRole pr WHERE pr.positionCode = ?1 AND pr.roleCode = u.roleCode")
    public List<HtBoaInRole> listHtBoaInRole(String positionCode);
    

    /**
     * @return HtBoaInPosition
     * @Title: findByPositionCode
     * @Description: 通过岗位编码查询岗位信息
     */
    List<HtBoaInPosition> findByPositionCode(String positionCode);

	public List<HtBoaInPosition> findByPositionNameCnAndParentOrgCode(String positionName, String parentOrgCode);

	@Query(value = "SELECT count(POSITION_code) from HT_BOA_IN_POSITION where parent_org_code=?1", nativeQuery = true)
	public Integer getMaxPositionCode(String parentOrgCode);

}