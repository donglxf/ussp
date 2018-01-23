package com.ht.ussp.uc.app.repository;

import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.model.SelfBoaInUserInfo;
import com.ht.ussp.uc.app.vo.UserMessageVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInUserRepository
 * @Description: 用户表持久层
 * @date 2018年1月8日 上午11:35:49
 */
@Repository
public interface HtBoaInUserRepository extends JpaSpecificationExecutor<HtBoaInUser>, JpaRepository<HtBoaInUser, Long> {

    /**
     * @return HtBoaInUser
     * @throws
     * @Title: findByUserName
     * @Description: 通过用户名查询用户信息
     */
    HtBoaInUser findByUserName(String userName);
    
    /**
     * 
     * @Title: findByUserId 
     * @Description: 通过用户名查询用户信息 
     * @return HtBoaInUser
     * @throws
     * @author wim qiuwenwu@hongte.info 
     * @date 2018年1月22日 下午9:55:52
     */
    HtBoaInUser findByUserId(String userId);

    @Query(value = "SELECT " +
            "new com.ht.ussp.uc.app.vo.UserMessageVo(" +
            "hbiUser.id," +
            "hbiUser.userId," +
            "hbiUser.jobNumber," +
            "hbiUser.userName," +
            "hbiUser.orgCode," +
            "org.orgNameCn," +
            "hbiUser.mobile," +
            "hbiUser.email," +
            "hbiUser.idNo," +
            "hbiUser.delFlag," +
            "hbiUser.createOperator," +
            "hbiUser.createdDatetime," +
            "hbiUser.updateOperator," +
            "hbiUser.lastModifiedDatetime," +
            "login.status," +
            "login.failedCount," +
            "login.pwdExpDate," +
            "login.effectiveDate)" +
            "FROM HtBoaInUser hbiUser ,HtBoaInLogin login  ,HtBoaInOrg org " +
            "WHERE hbiUser.userId=login.userId " +
            "AND hbiUser.orgCode=org.orgCode " +
            "AND (hbiUser.userName LIKE %?2% OR hbiUser.mobile LIKE %?2% OR hbiUser.jobNumber LIKE %?2% OR ?2 is null OR ?2='') " +
            "AND hbiUser.delFlag = 0 " +
            "AND hbiUser.orgCode =?1 " +
            "AND ?1<>'' AND ?1 is not null "
            , countQuery = "SELECT " +
            "COUNT(hbiUser.id) " +
            "FROM HtBoaInUser hbiUser " +
            "WHERE (hbiUser.userName LIKE %?2% OR hbiUser.mobile LIKE %?2% OR hbiUser.jobNumber LIKE %?2% OR ?2 is null OR ?2='') " +
            "AND hbiUser.delFlag = 0 " +
            "AND hbiUser.orgCode =?1 " +
            "AND ?1<>'' AND ?1 is not null"
    )
    Page<UserMessageVo> queryUserPage(String orgCode, String keyWord, Pageable pageable);

    @Query("SELECT new com.ht.ussp.uc.app.model.SelfBoaInUserInfo(u.userId, u.userName, u.email, u.idNo, u.mobile, u.orgCode, o.orgName, o.orgNameCn, u.rootOrgCode, r.orgName, r.orgNameCn, o.orgPath, o.orgType,u.jobNumber) FROM HtBoaInUser u, HtBoaInOrg o, HtBoaInOrg r WHERE u.userId = ?1 AND u.orgCode = o.orgCode AND u.rootOrgCode = r.orgCode")
    public List<SelfBoaInUserInfo> listSelfUserInfo(String userId);

    @Query("SELECT new map(r.roleCode, r.roleName, r.roleNameCn) FROM HtBoaInUser u, HtBoaInRole r, HtBoaInUserRole ur WHERE u.userId = ?1 AND u.userId = ur.userId AND ur.roleCode = r.roleCode")
    public List<Map<String, Object>> listSelfUserInfo4Role(String userId);

    @Query("SELECT new map(p.positionCode, p.positionName, p.positionNameCn) FROM HtBoaInUser u, HtBoaInPosition p, HtBoaInPositionUser pu WHERE u.userId = ?1 AND u.userId = pu.userId AND pu.positionCode = p.positionCode")
    public List<Map<String, Object>> listSelfUserInfo4Position(String userId);

    @Modifying(clearAutomatically = true)
    @Query("update HtBoaInUser u set u.delFlag=1 where u.userId = ?1")
    int setDelFlagByUserId(String userId);

    @Query(value = "SELECT " +
            "new com.ht.ussp.uc.app.vo.UserMessageVo(" +
            "hbiUser.id," +
            "hbiUser.userId," +
            "hbiUser.jobNumber," +
            "hbiUser.userName," +
            "hbiUser.orgCode," +
            "org.orgNameCn," +
            "hbiUser.mobile," +
            "hbiUser.email," +
            "hbiUser.idNo," +
            "hbiUser.delFlag," +
            "hbiUser.createOperator," +
            "hbiUser.createdDatetime," +
            "hbiUser.updateOperator," +
            "hbiUser.lastModifiedDatetime," +
            "login.status," +
            "login.failedCount," +
            "login.pwdExpDate," +
            "login.effectiveDate)" +
            "FROM HtBoaInUser hbiUser ,HtBoaInLogin login  ,HtBoaInOrg org " +
            "WHERE hbiUser.userId=login.userId " +
            "AND hbiUser.orgCode=org.orgCode " +
            "AND hbiUser.delFlag = 0 " +
            "AND hbiUser.userId = ?1 "
    )
    UserMessageVo queryUserByUserId(String userId);

    @Modifying(clearAutomatically = true)
    @Query("update HtBoaInUser u set " +
            "u.userName=?2 " +
            ",u.jobNumber=?3  " +
            ",u.mobile=?4  " +
            ",u.idNo=?5  " +
            ",u.email=?6  " +
            ",u.updateOperator=?7  " +
            "where u.userId = ?1 ")
    int updateUserByUserId(String userId, String userName, String jobNumber, String mobile, String idNo, String email, String updateOperator);
}
