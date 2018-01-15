package com.ht.ussp.uc.app.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.model.SelfBoaInUserInfo;
/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInUserRepository
 * @Description: 用户表持久层
 * @date 2018年1月8日 上午11:35:49
 */
public interface HtBoaInUserRepository extends JpaSpecificationExecutor<HtBoaInUser>, JpaRepository<HtBoaInUser, Long> {

    /**
     * @return HtBoaInUser
     * @throws
     * @Title: findByUserName
     * @Description: 通过用户名查询用户信息
     */
    public HtBoaInUser findByUserName(String userName);

    @Query("SELECT new com.ht.ussp.uc.app.model.SelfBoaInUserInfo(u.userId, u.userName, u.email, u.idNo, u.mobile, u.orgCode, o.orgName, o.orgNameCn, u.rootOrgCode, r.orgName, r.orgNameCn, o.orgPath, o.orgType) FROM HtBoaInUser u, HtBoaInOrg o, HtBoaInOrg r WHERE u.userId = ?1 AND u.orgCode = o.orgCode AND u.rootOrgCode = r.orgCode")
    public List<SelfBoaInUserInfo> listSelfUserInfo(String userId);
    
    @Query("SELECT new map(r.roleCode, r.roleName, r.roleNameCn) FROM HtBoaInUser u, HtBoaInRole r, HtBoaInUserRole ur WHERE u.userId = ?1 AND u.userId = ur.userId AND ur.roleCode = r.roleCode")
    public List<Map<String, Object>> listSelfUserInfo4Role(String userId);

    @Query("SELECT new map(p.positionCode, p.positionName, p.positionNameCn) FROM HtBoaInUser u, HtBoaInPosition p, HtBoaInPositionUser pu WHERE u.userId = ?1 AND u.userId = pu.userId AND pu.positionCode = p.positionCode")
    public List<Map<String, Object>> listSelfUserInfo4Position(String userId);
}
