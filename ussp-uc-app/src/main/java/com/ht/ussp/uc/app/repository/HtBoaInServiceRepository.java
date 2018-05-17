package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ht.ussp.uc.app.domain.HtBoaInService;

public interface HtBoaInServiceRepository  extends JpaSpecificationExecutor<HtBoaInService>, JpaRepository<HtBoaInService, Long> {

	
}