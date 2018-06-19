package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ht.ussp.uc.app.domain.TbDepartment;

 
@Repository
public interface TbDepartmentRepository extends  JpaRepository<TbDepartment, Long>,JpaSpecificationExecutor<TbDepartment>{

}
