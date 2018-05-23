package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ht.ussp.uc.app.domain.HtBoaInServiceApi;

public interface HtBoaInServiceApiRepository  extends JpaSpecificationExecutor<HtBoaInServiceApi>, JpaRepository<HtBoaInServiceApi, Long> {

	List<HtBoaInServiceApi> findByAuthServiceCodeAndApiContentAndStatus(String authServiceCode, String mainApi,
			String status);


}