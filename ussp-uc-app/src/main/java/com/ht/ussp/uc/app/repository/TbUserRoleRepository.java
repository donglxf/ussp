package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ht.ussp.uc.app.domain.TbUserRole;

 
@Repository
public interface TbUserRoleRepository extends JpaSpecificationExecutor<TbUserRole>,JpaRepository<TbUserRole, Long>   {

    List<TbUserRole> findByRoleId(String roleId);
}
