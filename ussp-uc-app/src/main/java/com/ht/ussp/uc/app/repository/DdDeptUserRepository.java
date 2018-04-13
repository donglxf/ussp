package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.uc.app.domain.DdUser;
 
public interface DdDeptUserRepository
        extends JpaRepository<DdUser, Long> {

}
