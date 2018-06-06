package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.ht.ussp.uc.app.domain.HtBoaInServiceCall;
import com.ht.ussp.uc.app.model.BoaInServiceInfo;

public interface HtBoaInServiceCallRepository  extends JpaSpecificationExecutor<HtBoaInServiceCall>, JpaRepository<HtBoaInServiceCall, Long> {

	//获取可以调用的微服务
	List<HtBoaInServiceCall> findByMainServiceCode(String mainServiceCode);

	@Query("SELECT new com.ht.ussp.uc.app.model.BoaInServiceInfo(u.id, s.app, s.serviceCode, s.applicationService, s.applicationServiceName, u.authServiceCode,u.callServiceCode,sc.applicationService,sc.applicationServiceName,u.status, u.createOperator, u.createdDatetime, u.updateOperator, u.updateDatetime,sc.app)  FROM HtBoaInServiceCall u ,HtBoaInService s,HtBoaInService sc   WHERE  u.mainServiceCode=?1 AND (sc.applicationServiceName like ?2  or sc.applicationService like ?2 )  AND u.mainServiceCode=s.serviceCode AND u.callServiceCode = sc.serviceCode  GROUP BY u Order by sc.app")
	Page<BoaInServiceInfo> listBoaInServiceInfoByPageWeb(Pageable arg0, String search ,String keyword);

	List<HtBoaInServiceCall> findByMainServiceCodeAndCallServiceCode(String mainServiceCode, String callServiceCode);
	
	
	
	 
}