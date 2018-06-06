package com.ht.ussp.ouc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ht.ussp.ouc.app.domain.HtBoaOutClientDetail;

@Repository
public interface HtBoaOutClientDetailRepository extends JpaRepository<HtBoaOutClientDetail, Long> {
	public HtBoaOutClientDetail findByAppCode(String appCode);
}
