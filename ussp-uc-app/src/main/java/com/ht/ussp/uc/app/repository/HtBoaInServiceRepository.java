package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.ht.ussp.uc.app.domain.HtBoaInService;

public interface HtBoaInServiceRepository  extends JpaSpecificationExecutor<HtBoaInService>, JpaRepository<HtBoaInService, Long> {

	 @Query(value = "SELECT * FROM ( " +
	            "SELECT " +
	            "service_code,application_service_name,application_service, app parentapp,app " +
	            "FROM ht_boa_in_service " +
	            "WHERE STATUS='0' " +
	            "UNION ALL " +
	            "SELECT " +
	            "APP,NAME,NAME_CN,'0',APP " +
	            "FROM HT_BOA_IN_APP " +
	            "WHERE STATUS='0' " +
	            "AND DEL_FLAG='0' " +
	            ") A ORDER BY application_service_name ", nativeQuery = true)
	List<Object[]> queryAppAndServiceTree();

	
}