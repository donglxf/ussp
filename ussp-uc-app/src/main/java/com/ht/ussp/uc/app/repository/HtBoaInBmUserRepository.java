package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ht.ussp.uc.app.domain.HtBoaInBmUser;
import com.ht.ussp.uc.app.domain.HtBoaInUser;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInUserRepository
 * @Description: 用户表持久层
 * @date 2018年1月8日 上午11:35:49
 */
@Repository
public interface HtBoaInBmUserRepository extends JpaSpecificationExecutor<HtBoaInBmUser>, JpaRepository<HtBoaInBmUser, Long> {

    
}
