package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.model.BoaInOrgInfo;

/**
 * 
 * @ClassName: HtBoaInOrgRepository
 * @Description: 组织机构表持久层
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月11日 上午9:19:32
 */
public interface HtBoaInOrgRepository
        extends JpaRepository<HtBoaInOrg, Long> {

    public HtBoaInOrg findById(Long id);
    
    /**
     * 根据父组织机构代码查询组织机构信息<br>
     *
     * @param parentOrgCode 父组织机构代码
     * @return 组织机构信息集合
     * @author 谭荣巧
     * @Date 2018/1/13 10:49
     */
    List<HtBoaInOrg> findByParentOrgCode(String parentOrgCode, Sort sort);
    
    @Query("SELECT new com.ht.ussp.uc.app.model.BoaInOrgInfo (u.orgCode, u.orgName, u.orgNameCn, u.parentOrgCode, u.rootOrgCode, u.orgPath, u.orgType, u.sequence,  u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,u.delFlag,u.id) FROM HtBoaInOrg u  WHERE  (u.orgCode LIKE ?1 OR u.orgName LIKE ?1 OR u.orgNameCn LIKE ?1) AND (u.parentOrgCode = ?2 or u.orgCode=?2)    GROUP BY u")
    public Page<BoaInOrgInfo> lisOrgByPageWeb(Pageable arg0, String search,String orgPath);
    
    @Query("SELECT new com.ht.ussp.uc.app.model.BoaInOrgInfo (u.orgCode, u.orgName, u.orgNameCn, u.parentOrgCode, u.rootOrgCode, u.orgPath, u.orgType, u.sequence,  u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,u.delFlag,u.id) FROM HtBoaInOrg u  WHERE  u.orgCode LIKE ?1 OR u.orgName LIKE ?1 OR u.orgNameCn LIKE ?1 GROUP BY u")
    public Page<BoaInOrgInfo> lisOrgByPageWebAll(Pageable arg0, String search );

    List<HtBoaInOrg> findByOrgCode(String orgCode);
    
    List<HtBoaInOrg> findByParentOrgCode(String parentOrgCode);
    
    //行政机构没有分公司 片区
    @Query("SELECT new com.ht.ussp.uc.app.model.BoaInOrgInfo (u.orgCode, u.orgNameCn, u.parentOrgCode, u.sequence,  u.delFlag, u.rootOrgCode, u.rootOrgCode,u.delFlag, u.rootOrgCode,u.rootOrgCode,u.id) FROM HtBoaInOrg u  WHERE   u.parentOrgCode = ?1 and u.delFlag=0   GROUP BY u")
    public List<BoaInOrgInfo> listOrgByParentOrgCode(String parentOrg);

    @Query("SELECT new com.ht.ussp.uc.app.model.BoaInOrgInfo (u.orgCode, u.orgNameCn, u.parentOrgCode, u.sequence,  u.delFlag, u.rootOrgCode, u.rootOrgCode,u.delFlag, u.rootOrgCode,u.rootOrgCode,u.id) FROM HtBoaInOrg u  WHERE  (u.orgCode LIKE ?1 OR u.orgName LIKE ?1 OR u.orgNameCn LIKE ?1)  and u.delFlag=0   GROUP BY u")
    public List<BoaInOrgInfo> listOrg(String search);
    
    @Query(value="select * from HT_BOA_IN_ORG where LAST_MODIFIED_DATETIME BETWEEN ?1 AND ?2",nativeQuery=true)
	public List<HtBoaInOrg> getByLastModifiedDatetime(String startTime, String endTime);

    @Query(value = "SELECT MAX(org_code) from HT_BOA_IN_ORG where parent_org_code=?1", nativeQuery = true)
	public String getMaxOrgCode(String parentOrgCode);
    
    
/*    @Query("SELECT new com.ht.ussp.uc.app.model.BoaInOrgInfo (u.orgCode, u.orgName, u.orgNameCn, u.parentOrgCode, u.rootOrgCode, u.orgPath, u.orgType, u.sequence,  u.createOperator) FROM HtBoaInOrg u  WHERE  u.orgCode LIKE ?1 OR u.orgName LIKE ?1 OR u.orgNameCn LIKE ?1    GROUP BY u")
    public Page<BoaInOrgInfo> lisOrgByPageWeb(Pageable arg0, String search);*/

}
