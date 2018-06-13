package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ht.ussp.uc.app.domain.HtBoaInUserExt;

@Repository
public interface HtBoaInUserExtRepository extends JpaSpecificationExecutor<HtBoaInUserExt>, JpaRepository<HtBoaInUserExt, Long> {


	List<HtBoaInUserExt> findByUserId(String userId );

}
