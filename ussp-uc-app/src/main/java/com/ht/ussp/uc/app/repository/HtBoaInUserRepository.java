package com.ht.ussp.uc.app.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.model.SelfBoaInUserInfo;
import com.ht.ussp.uc.app.vo.UserMessageVo;

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
    
    /**
     * 通过userId email Mobile 工号登录
     * @param userId
     * @return
     */
    HtBoaInUser findByUserIdOrEmailOrMobileOrJobNumber(String userId,String email,String mobile,String jboNumber);

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
            "login.effectiveDate," +
             "login.loginId,hbiUser.orgPath,hbiUser.rootOrgCode)" +
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
    
    @Query(value = "SELECT " +
            "new com.ht.ussp.uc.app.vo.UserMessageVo(" +
            "hbiUser.id," +
            "hbiUser.userId," +
            "hbiUser.jobNumber," +
            "hbiUser.userName," +
            "hbiUser.orgCode," +
            "hbiUser.orgCode," +
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
            "login.effectiveDate," +
             "login.loginId,hbiUser.orgPath,hbiUser.rootOrgCode)" +
            "FROM HtBoaInUser hbiUser ,HtBoaInLogin login   " +
            "WHERE hbiUser.userId=login.userId " +
            "AND (hbiUser.userName LIKE %?1% OR hbiUser.mobile LIKE %?1% OR hbiUser.jobNumber LIKE %?1% OR ?1 is null OR ?1='') " +
            "AND hbiUser.delFlag = 0 " 
            , countQuery = "SELECT " +
            "COUNT(hbiUser.id) " +
            "FROM HtBoaInUser hbiUser " +
            "WHERE (hbiUser.userName LIKE %?1% OR hbiUser.mobile LIKE %?1% OR hbiUser.jobNumber LIKE %?1% OR ?1 is null OR ?1='') " +
            "AND hbiUser.delFlag = 0 " 
    )
    Page<UserMessageVo> queryUserPageAll(String keyWord, Pageable pageable);

    @Query("SELECT new com.ht.ussp.uc.app.model.SelfBoaInUserInfo(u.userId, u.userName, u.email, u.idNo, u.mobile, u.orgCode, o.orgName, o.orgNameCn, u.rootOrgCode, '', '', o.orgPath, o.orgType,u.jobNumber) FROM HtBoaInUser u, HtBoaInOrg o  WHERE u.userId = ?1 AND u.orgCode = o.orgCode ")
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
            "login.effectiveDate,"
            + "login.loginId,hbiUser.orgPath,hbiUser.rootOrgCode)" +
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

    @Query(value = "select hbiuser.id,hbiuser.user_id,hbiuser.job_number,hbiuser.user_name ,hbiuser.org_code ,"
    		+ "hbiuser.mobile,hbiuser.email,hbiuser.id_no,hbiuser.del_flag,org.org_name_cn  " 
    		+ " from HT_BOA_IN_USER hbiuser ,HT_BOA_IN_ORG org WHERE hbiuser.org_code= :orgCode and hbiuser.org_code=org.org_code and (hbiuser.user_name  like %:keyWord% or hbiuser.job_number like %:keyWord% or hbiuser.user_id like %:keyWord% or hbiuser.mobile like %:keyWord% ) and hbiuser.user_id in (select l.user_id from HT_BOA_IN_LOGIN l where l.password is null or l.password = '') /*#pageable*/",
    		countQuery = "SELECT count(id)  from HT_BOA_IN_USER hbiuser WHERE hbiuser.org_code= :orgCode and (hbiuser.user_name  like %:keyWord% or hbiuser.job_number like %:keyWord% or hbiuser.user_id like %:keyWord% or hbiuser.mobile like %:keyWord% ) and  hbiuser.user_id in (select l.user_id from HT_BOA_IN_LOGIN l where l.password is null or l.password = '') "
            ,nativeQuery=true
    )
    Page<Object[]> queryUserIsNullPwd(Pageable pageable,@Param("orgCode")String orgCode, @Param("keyWord")String keyWord);

	HtBoaInUser findByJobNumber(String jobnum);

	HtBoaInUser findByMobile(String mobile);

	HtBoaInUser findByEmail(String email);

    
}
