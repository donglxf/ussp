package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ht.ussp.uc.app.domain.HtBoaInServiceCall;

public interface HtBoaInServiceCallRepository  extends JpaSpecificationExecutor<HtBoaInServiceCall>, JpaRepository<HtBoaInServiceCall, Long> {


}