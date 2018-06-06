package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ht.ussp.uc.app.domain.DdDeptOperator;

 
public interface DdDeptOperatorRepository
        extends JpaRepository<DdDeptOperator, Long>,JpaSpecificationExecutor<DdDeptOperator>  {

}
