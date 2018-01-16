package com.ht.ussp.uc.app.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ht.ussp.uc.app.domain.HtBoaOutRole;
import com.ht.ussp.uc.app.domain.HtBoaOutUser;
import com.ht.ussp.uc.app.model.BoaOutRoleInfo;

/**
 * 
 * @ClassName: HtBoaOutRoleRepository
 * @Description: 角色表持久层
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月11日 上午9:19:32
 */
public interface HtBoaOutRoleRepository
        extends JpaRepository<HtBoaOutRole, Long> {

    public HtBoaOutRole findById(Long id);

    @Modifying
    @Query("UPDATE FROM HtBoaOutRole u SET u.delFlag = 1 WHERE u.roleCode IN ?1")
    void delete(Set<String> roleCodes);
    
    public List<HtBoaOutRole> findByRoleCodeIn(Set<String> roleCodes);
    
    @Query("SELECT new com.ht.ussp.uc.app.model.BoaOutRoleInfo(u.roleCode, u.roleName, u.roleNameCn, u.status, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime) FROM HtBoaOutRole u, HtBoaOutUserRole pu, HtBoaOutUser r WHERE u.roleCode LIKE ?1 OR u.roleName LIKE ?1 OR u.roleNameCn LIKE ?1 OR u.status LIKE ?1 OR u.createOperator LIKE ?1 OR cast(u.createdDatetime AS string) LIKE ?1 OR u.updateOperator LIKE ?1 OR cast(u.lastModifiedDatetime AS string) LIKE ?1 OR (r.userName LIKE ?1 AND u.roleCode = pu.roleCode AND pu.userId = r.userId) GROUP BY u")
    public List<BoaOutRoleInfo> listRoleInfo(String search);
    
    @Query("SELECT new com.ht.ussp.uc.app.model.BoaOutRoleInfo(u.roleCode, u.roleName, u.roleNameCn, u.status, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime) FROM HtBoaOutRole u, HtBoaOutUserRole pu, HtBoaOutUser r WHERE u.roleCode LIKE ?1 OR u.roleName LIKE ?1 OR u.roleNameCn LIKE ?1 OR u.status LIKE ?1 OR u.createOperator LIKE ?1 OR cast(u.createdDatetime AS string) LIKE ?1 OR u.updateOperator LIKE ?1 OR cast(u.lastModifiedDatetime AS string) LIKE ?1 OR (r.userName LIKE ?1 AND u.roleCode = pu.roleCode AND pu.userId = r.userId) GROUP BY u")
    public Page<BoaOutRoleInfo> listRoleInfo(Pageable arg0, String search);

    @Query("SELECT u FROM HtBoaOutUser u, HtBoaOutUserRole pu WHERE pu.roleCode = ?1 AND pu.userId = u.userId")
    public List<HtBoaOutUser> listHtBoaOutUser(String roleCode);

}