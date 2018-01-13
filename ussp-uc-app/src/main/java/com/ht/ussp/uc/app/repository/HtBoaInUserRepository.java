package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.uc.app.domain.HtBoaInUser;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInUserRepository
 * @Description: 用户表持久层
 * @date 2018年1月8日 上午11:35:49
 */
public interface HtBoaInUserRepository extends JpaSpecificationExecutor<HtBoaInUser>, JpaRepository<HtBoaInUser, Long> {

    /**
     * @return HtBoaInUser
     * @throws
     * @Title: findByUserName
     * @Description: 通过用户名查询用户信息
     */
    public HtBoaInUser findByUserName(String userName);

}
