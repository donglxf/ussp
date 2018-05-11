package com.ht.ussp.ouc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ht.ussp.ouc.app.domain.HtBoaOutLogin;

@Repository
public interface HtBoaOutLoginRepository extends JpaSpecificationExecutor<HtBoaOutLogin>, JpaRepository<HtBoaOutLogin, Long> {

	HtBoaOutLogin findByUserId(String userId);

    
}
