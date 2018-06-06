package com.ht.ussp.ouc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ht.ussp.ouc.app.domain.HtBoaOutUser;

@Repository
public interface HtBoaOutUserRepository extends JpaSpecificationExecutor<HtBoaOutUser>, JpaRepository<HtBoaOutUser, Long> {

	HtBoaOutUser findByEmailOrMobile(String userName, String userName2);

	HtBoaOutUser findByEmail(String email);

	HtBoaOutUser findByMobile(String mobile);

	HtBoaOutUser findByUserId(String userId);

    
}
