package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ht.ussp.uc.app.domain.HtBoaInServiceApi;

public interface HtBoaInServiceApiRepository  extends JpaSpecificationExecutor<HtBoaInServiceApi>, JpaRepository<HtBoaInServiceApi, Long> {


}