package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.uc.app.domain.HtBoaOutPwdHist;

/**
 * 
 * @ClassName: HtBoaOutPwdHistRepository
 * @Description: 密码维护历史表持久层
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月9日 上午10:16:45
 */
public interface HtBoaOutPwdHistRepository
        extends JpaRepository<HtBoaOutPwdHist, Long> {

    public HtBoaOutPwdHist findById(Long id);

}
