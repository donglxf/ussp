package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ht.ussp.uc.app.domain.HtBoaInUserExt;

@Repository
public interface HtBoaInUserExtRepository extends JpaSpecificationExecutor<HtBoaInUserExt>, JpaRepository<HtBoaInUserExt, Long> {


	List<HtBoaInUserExt> findByUserId(String userId );

	List<HtBoaInUserExt> findByBusiOrgCode(String busiOrgCode);
	
	//通用户ID获取业务机构
	@Query(value="select BUSI_ORG_CODE from HT_BOA_IN_USER_EXT where USER_ID=:userId",nativeQuery=true)
	public String getBusinessOrgCode(@Param("userId") String userId);
	
}
