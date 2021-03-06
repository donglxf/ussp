package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.ht.ussp.uc.app.domain.HtBoaInBusinessOrg;
import com.ht.ussp.uc.app.model.BoaInBusiOrgInfo;
import com.ht.ussp.uc.app.model.BoaInOrgInfo;

 
public interface HtBoaInOrgBusinessRepository
        extends JpaSpecificationExecutor<HtBoaInBusinessOrg>, JpaRepository<HtBoaInBusinessOrg, Long>   {

    public HtBoaInBusinessOrg findById(Long id);
    
    List<HtBoaInBusinessOrg> findByParentOrgCode(String parentOrgCode, Sort sort);
    
    List<HtBoaInBusinessOrg> findByBusinessOrgCode(String orgCode);

    List<HtBoaInBusinessOrg> findByBusinessOrgName(String orgName);
    
    List<HtBoaInBusinessOrg> findByParentOrgCode(String parentOrgCode);

    @Query(value="select * from HT_BOA_IN_ORG where LAST_MODIFIED_DATETIME BETWEEN ?1 AND ?2",nativeQuery=true)
	public List<HtBoaInBusinessOrg> getByLastModifiedDatetime(String startTime, String endTime);

    @Query(value = "SELECT MAX(org_code) from HT_BOA_IN_ORG where parent_org_code=?1", nativeQuery = true)
	public String getMaxOrgCode(String parentOrgCode);

	public List<HtBoaInBusinessOrg> findByOrgLevel(Integer orgLevel);

	public List<HtBoaInBusinessOrg> findByParentOrgCodeAndOrgLevel(String busiOrgCode, Integer orgLevel);

	@Query("SELECT new com.ht.ussp.uc.app.model.BoaInOrgInfo (u.businessOrgCode, u.businessOrgName, u.parentOrgCode, u.sequence,  u.status, u.branchCode, u.districtCode,u.orgLevel, u.province,u.city,u.id) FROM HtBoaInBusinessOrg u  WHERE   u.parentOrgCode = ?1 and u.status=0   GROUP BY u")
	public List<BoaInOrgInfo> listOrgByParentOrgCode(String parentOrg);

	@Query("SELECT new com.ht.ussp.uc.app.model.BoaInOrgInfo (u.businessOrgCode, u.businessOrgName, u.parentOrgCode, u.sequence,  u.status, u.branchCode, u.districtCode,u.orgLevel, u.province,u.city,u.id) FROM HtBoaInBusinessOrg u  WHERE  (u.businessOrgCode LIKE ?1 OR u.businessOrgName LIKE ?1 )  and u.status=0   GROUP BY u")
	public List<BoaInOrgInfo> listOrg(String search);
	
//	@Query("SELECT new com.ht.ussp.uc.app.model.BoaInBusiOrgInfo (u.businessOrgCode,u.businessOrgName,u.parentOrgCode,u.businessGroup,u.branchCode,u.districtCode ,u.financeCode,u.approvalCode,u.activityCode,u.bmOrgCode,u.orgLevel,u.status,u.province,u.city,u.county,u.dataSource,u.isHeadDept,u.isAppRovalDept,u.deptAddress,u.deptTel,u.deptUser,u.businessPhone,c.branchNameContract,c.branchAddressContract,c.branchPhoneContract,c.branchPledgeEmailContract,c.legalPerson,c.horizontalText,c.uniteCode,c.cosText,c.guid,c.highestDebtRatio)  FROM HtBoaInBusinessOrg u, HtBoaInCompany c WHERE u.businessOrgCode = c.branchCode   AND (u.businessOrgName like %?1% or u.businessOrgCode like %?1% ) GROUP BY u")
//	Page<BoaInBusiOrgInfo> listCompnayInfoByPageWeb(Pageable arg0,String search);
//
//	@Query("SELECT new com.ht.ussp.uc.app.model.BoaInBusiOrgInfo (u.businessOrgCode,u.businessOrgName,u.parentOrgCode,u.businessGroup,u.branchCode,u.districtCode ,u.financeCode,u.approvalCode,u.activityCode,u.bmOrgCode,u.orgLevel,u.status,u.province,u.city,u.county,u.dataSource,u.isHeadDept,u.isAppRovalDept,u.deptAddress,u.deptTel,u.deptUser,u.businessPhone,c.branchNameContract,c.branchAddressContract,c.branchPhoneContract,c.branchPledgeEmailContract,c.legalPerson,c.horizontalText,c.uniteCode,c.cosText,c.guid,c.highestDebtRatio)  FROM HtBoaInBusinessOrg u, HtBoaInCompany c WHERE u.businessOrgCode = c.branchCode   AND (u.businessOrgName like %?1% or u.businessOrgCode like %?1% ) AND u.parentOrgCode=?2 GROUP BY u")
//	Page<BoaInBusiOrgInfo> listCompnayInfoByPageWebByParent(Pageable arg0,String search,String parent);
}
