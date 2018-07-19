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
public interface HtBoaInUserBusinessRepository
		extends JpaSpecificationExecutor<HtBoaInUser>, JpaRepository<HtBoaInUser, Long> {

	@Query(value = "SELECT " + "new com.ht.ussp.uc.app.vo.UserMessageVo(" + "hbiUser.id," + "hbiUser.userId,"
			+ "hbiUser.jobNumber," + "hbiUser.userName," + "hbiUser.orgCode," + "org.businessOrgName,"
			+ "hbiUser.mobile," + "hbiUser.email," + "hbiUser.idNo," + "hbiUser.delFlag," + "hbiUser.createOperator,"
			+ "hbiUser.createdDatetime," + "hbiUser.updateOperator," + "hbiUser.lastModifiedDatetime,"
			+ "hbiUser.status," + "login.failedCount," + "login.pwdExpDate," + "login.effectiveDate,"
			+ "login.loginId,hbiUser.orgPath,hbiUser.rootOrgCode,hbiUser.userId,hbiUserext.busiOrgCode,org.branchCode,org.districtCode,org.province,org.city) "
			+ "FROM HtBoaInUser hbiUser ,HtBoaInLogin login  ,HtBoaInUserExt hbiUserext,HtBoaInBusinessOrg org "
			+ "WHERE hbiUser.userId=login.userId and hbiUser.userId=hbiUserext.userId  AND hbiUserext.busiOrgCode=org.businessOrgCode "
			+ "AND (hbiUser.userName LIKE %?2% OR hbiUser.mobile LIKE %?2% OR hbiUser.jobNumber LIKE %?2% OR login.loginId LIKE %?2% OR ?2 is null OR ?2='') "
			+ "AND hbiUser.delFlag = 0 " + "AND hbiUserext.busiOrgCode =?1 ", countQuery = "SELECT "
					+ "COUNT(hbiUser.id) "
					+ "FROM HtBoaInUser hbiUser ,HtBoaInLogin login ,HtBoaInUserExt hbiUserext,HtBoaInBusinessOrg org "
					+ "WHERE hbiUser.userId=login.userId and hbiUser.userId=hbiUserext.userId AND hbiUserext.busiOrgCode=org.businessOrgCode  AND (hbiUser.userName LIKE %?2% OR hbiUser.mobile LIKE %?2% OR hbiUser.jobNumber LIKE %?2% OR login.loginId LIKE %?2% OR  ?2 is null OR ?2='') "
					+ "AND hbiUser.delFlag = 0 " + "AND hbiUserext.busiOrgCode =?1 ")
	Page<UserMessageVo> queryUserPage(String orgCode, String keyWord, Pageable pageable);

	@Query(value = "SELECT " + "new com.ht.ussp.uc.app.vo.UserMessageVo(" + "hbiUser.id," + "hbiUser.userId,"
			+ "hbiUser.jobNumber," + "hbiUser.userName," + "hbiUser.orgCode," + "hbiUser.orgCode," + "hbiUser.mobile,"
			+ "hbiUser.email," + "hbiUser.idNo," + "hbiUser.delFlag," + "hbiUser.createOperator,"
			+ "hbiUser.createdDatetime," + "hbiUser.updateOperator," + "hbiUser.lastModifiedDatetime,"
			+ "hbiUser.status," + "login.failedCount," + "login.pwdExpDate," + "login.effectiveDate,"
			+ "login.loginId,hbiUser.orgPath,hbiUser.rootOrgCode,hbiUser.userId,hbiUserext.busiOrgCode,org.branchCode,org.districtCode,org.province,org.city) "
			+ "FROM HtBoaInUser hbiUser ,HtBoaInUserExt hbiUserext, HtBoaInLogin login ,HtBoaInBusinessOrg org  "
			+ "WHERE hbiUser.userId=login.userId and hbiUser.userId=hbiUserext.userId  AND hbiUserext.busiOrgCode=org.businessOrgCode "
			+ "AND (hbiUser.userName LIKE %?1% OR hbiUser.mobile LIKE %?1% OR hbiUser.jobNumber LIKE %?1% OR login.loginId LIKE %?1% OR ?1 is null OR ?1='') "
			+ "AND hbiUser.delFlag = 0 ", countQuery = "SELECT " + "COUNT(hbiUser.id) "
					+ "FROM HtBoaInUser hbiUser ,HtBoaInLogin login  "
					+ "WHERE  hbiUser.userId=login.userId  AND  (hbiUser.userName LIKE %?1% OR hbiUser.mobile LIKE %?1% OR hbiUser.jobNumber LIKE %?1% OR login.loginId LIKE %?1% OR ?1 is null OR ?1='') "
					+ "AND hbiUser.delFlag = 0 ")
	Page<UserMessageVo> queryUserPageAll(String keyWord, Pageable pageable);

	@Query(value = "SELECT " + "new com.ht.ussp.uc.app.vo.UserMessageVo(" + "hbiUser.id," + "hbiUser.userId,"
			+ "hbiUser.jobNumber," + "hbiUser.userName," + "hbiUser.orgCode," + "org.businessOrgName,"
			+ "hbiUser.mobile," + "hbiUser.email," + "hbiUser.idNo," + "hbiUser.delFlag," + "hbiUser.createOperator,"
			+ "hbiUser.createdDatetime," + "hbiUser.updateOperator," + "hbiUser.lastModifiedDatetime,"
			+ "hbiUser.status," + "login.failedCount," + "login.pwdExpDate," + "login.effectiveDate,"
			+ "login.loginId,hbiUser.orgPath,hbiUser.rootOrgCode,hbiUser.userId,hbiUserext.busiOrgCode,org.branchCode,org.districtCode,org.province,org.city) "
			+ "FROM HtBoaInUser hbiUser ,HtBoaInLogin login  ,HtBoaInUserExt hbiUserext,HtBoaInBusinessOrg org "
			+ "WHERE hbiUser.userId=login.userId and hbiUser.userId=hbiUserext.userId  AND hbiUserext.busiOrgCode=org.businessOrgCode "
			+ "AND hbiUser.delFlag = 0 " + "AND hbiUser.userId = ?1 ")
	UserMessageVo getUserBusiByUserId(String userId);

	@Query(value = "SELECT " + "new com.ht.ussp.uc.app.vo.UserMessageVo(" + "hbiUser.id," + "hbiUser.userId,"
			+ "hbiUser.jobNumber," + "hbiUser.userName," + "hbiUser.orgCode," + "org.businessOrgName,"
			+ "hbiUser.mobile," + "hbiUser.email," + "hbiUser.idNo," + "hbiUser.delFlag," + "hbiUser.createOperator,"
			+ "hbiUser.createdDatetime," + "hbiUser.updateOperator," + "hbiUser.lastModifiedDatetime,"
			+ "hbiUser.status," + "login.failedCount," + "login.pwdExpDate," + "login.effectiveDate,"
			+ "login.loginId,hbiUser.orgPath,hbiUser.rootOrgCode,hbiUser.userId,hbiUserext.busiOrgCode,org.branchCode,org.districtCode,org.province,org.city) "
			+ "FROM HtBoaInUser hbiUser ,HtBoaInLogin login  ,HtBoaInUserExt hbiUserext,HtBoaInBusinessOrg org "
			+ "WHERE hbiUser.userId=login.userId and hbiUser.userId=hbiUserext.userId  AND hbiUserext.busiOrgCode=org.businessOrgCode "
			+ "AND hbiUser.delFlag = 0 " + "AND hbiUserext.busiOrgCode in (?1) ")
	List<UserMessageVo> getUserBusiListByBusiOrgCode(List<String> busiOrgCode);

	// 通过业务机构码获取分公司机构码
	@Query(value = "select BRANCH_CODE from HT_BOA_IN_BUSINESS_ORG where BUSINESS_ORG_CODE=:busiOrgCode", nativeQuery = true)
	public String getBranchCode(@Param("busiOrgCode") String busiOrgCode);

}
