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
import com.ht.ussp.uc.app.model.BoaInRoleInfo;

/**
 * @author adol yaojiehong@hongte.info
 * @ClassName: HtBoaInRoleRepository
 * @Description: 角色表持久层
 * @date 2018年1月11日 上午9:19:32
 */
public interface HtBoaInRoleRepository
        extends JpaRepository<HtBoaInRole, Long> {

    HtBoaInRole findById(Long id);

    @Modifying
    @Query("UPDATE FROM HtBoaInRole u SET u.delFlag = 1 WHERE u.roleCode IN ?1")
    void delete(Set<String> roleCodes);

    List<HtBoaInRole> findByRoleCodeIn(Set<String> roleCodes);

    @Query("SELECT new com.ht.ussp.uc.app.model.BoaInRoleInfo(u.roleCode, u.roleName, u.roleNameCn, o.orgCode, o.orgName, o.orgNameCn, o.orgType, u.status, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,u.delFlag,u.id) FROM HtBoaInRole u, HtBoaInOrg o, HtBoaInUserRole pu, HtBoaInUser r, HtBoaInPositionRole pr, HtBoaInPosition l WHERE (u.rootOrgCode = o.orgCode OR (u.roleCode = pu.roleCode AND pu.userId = r.userId OR u.roleCode = pr.roleCode AND pr.positionCode = l.positionCode)) AND (u.roleCode LIKE ?1 OR u.roleName LIKE ?1 OR u.roleNameCn LIKE ?1 OR o.orgCode LIKE ?1 OR o.orgName LIKE ?1 OR o.orgNameCn LIKE ?1 OR o.orgType LIKE ?1 OR u.status LIKE ?1 OR u.createOperator LIKE ?1 OR cast(u.createdDatetime AS string) LIKE ?1 OR u.updateOperator LIKE ?1 OR cast(u.lastModifiedDatetime AS string) LIKE ?1 OR r.userName LIKE ?1 OR l.positionCode LIKE ?1 OR l.positionName LIKE ?1 OR l.positionNameCn LIKE ?1) GROUP BY u")
    List<BoaInRoleInfo> listRoleInfo(String search);

    @Query("SELECT new com.ht.ussp.uc.app.model.BoaInRoleInfo(u.roleCode, u.roleName, u.roleNameCn, o.orgCode, o.orgName, o.orgNameCn, o.orgType, u.status, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,u.delFlag,u.id) FROM HtBoaInRole u, HtBoaInOrg o, HtBoaInUserRole pu, HtBoaInUser r, HtBoaInPositionRole pr, HtBoaInPosition l WHERE (u.rootOrgCode = o.orgCode OR (u.roleCode = pu.roleCode AND pu.userId = r.userId OR u.roleCode = pr.roleCode AND pr.positionCode = l.positionCode)) AND (u.roleCode LIKE ?1 OR u.roleName LIKE ?1 OR u.roleNameCn LIKE ?1 OR o.orgCode LIKE ?1 OR o.orgName LIKE ?1 OR o.orgNameCn LIKE ?1 OR o.orgType LIKE ?1 OR u.status LIKE ?1 OR u.createOperator LIKE ?1 OR cast(u.createdDatetime AS string) LIKE ?1 OR u.updateOperator LIKE ?1 OR cast(u.lastModifiedDatetime AS string) LIKE ?1 OR r.userName LIKE ?1 OR l.positionCode LIKE ?1 OR l.positionName LIKE ?1 OR l.positionNameCn LIKE ?1) GROUP BY u")
    Page<BoaInRoleInfo> listRoleInfo(Pageable arg0, String search);

    @Query("SELECT new com.ht.ussp.uc.app.model.BoaInRoleInfo(u.roleCode, u.roleName, u.roleNameCn,  u.status, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,u.delFlag,u.id) FROM HtBoaInRole u    WHERE  (u.roleCode LIKE ?1 OR u.roleName LIKE ?1 OR u.roleNameCn LIKE ?1    ) GROUP BY u")
    Page<BoaInRoleInfo> listRoleInfoByPageWeb(Pageable arg0, String search);

    @Query("SELECT u FROM HtBoaInUser u, HtBoaInUserRole pu WHERE pu.roleCode = ?1 AND pu.userId = u.userId")
    List<HtBoaInUser> listHtBoaInUser(String roleCode);

    @Query("SELECT u FROM HtBoaInPosition u, HtBoaInPositionRole pr WHERE pr.roleCode = ?1 AND pr.positionCode = u.positionCode")
    List<HtBoaInPosition> listHtBoaInPosition(String roleCode);

    @Query(value = "SELECT roleAll.ROLE_CODE " +
            "FROM ( " +
            "SELECT ur.ROLE_CODE\n" +
            "FROM boa.ht_boa_in_user_role ur " +
            "WHERE ur.USER_ID=?1  " +
            "UNION " +
            "SELECT pr.ROLE_CODE " +
            "FROM ht_boa_in_position_role pr " +
            "LEFT JOIN ht_boa_in_position_user pu ON pr.POSITION_CODE=pu.POSITION_CODE " +
            "WHERE pu.USER_ID=?1) roleAll " +
            "LEFT JOIN ht_boa_in_role role ON roleAll.ROLE_CODE=role.ROLE_CODE " +
            "WHERE role.STATUS='0'", nativeQuery = true)
    List<String> findRoleCodeByUserId(String userId);

}