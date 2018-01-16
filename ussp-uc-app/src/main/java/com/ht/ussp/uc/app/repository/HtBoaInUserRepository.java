package com.ht.ussp.uc.app.repository;

import com.ht.ussp.uc.app.vo.UserMessageVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.uc.app.domain.HtBoaInUser;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
    HtBoaInUser findByUserName(String userName);

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

}
