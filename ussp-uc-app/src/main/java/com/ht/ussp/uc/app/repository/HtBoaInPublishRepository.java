package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.uc.app.domain.HtBoaInPublish;

 

public interface HtBoaInPublishRepository extends JpaRepository <HtBoaInPublish, Long>{

	List<HtBoaInPublish> findByPublishCode(String publishCode);
	
   
}
