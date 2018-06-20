package com.ht.ussp.ouc.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.ouc.app.domain.HtBoaInApp;

public interface HtBoaInAppRepository extends JpaRepository<HtBoaInApp, Long> {

	List<HtBoaInApp> findByApp(String app);
 
}
