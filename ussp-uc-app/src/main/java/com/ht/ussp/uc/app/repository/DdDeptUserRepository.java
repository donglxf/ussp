package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.uc.app.domain.DdDeptUser;
 
public interface DdDeptUserRepository
        extends JpaRepository<DdDeptUser, Long> {

}