package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.uc.app.domain.HtBoaOutLogin;

/**
 * 
 * @ClassName: HtBoaOutLoginRepository
 * @Description: 系统表持久层
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月9日 上午10:16:45
 */
public interface HtBoaOutLoginRepository
        extends JpaRepository<HtBoaOutLogin, Long> {

    public HtBoaOutLogin findById(Long id);

}
