package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.uc.app.domain.HtBoaInPwdHist;

/**
 * 
 * @ClassName: HtBoaInPwdHistRepository
 * @Description: 密码维护历史表持久层
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月9日 上午10:16:45
 */
public interface HtBoaInPwdHistRepository
        extends JpaRepository<HtBoaInPwdHist, Long> {

    public HtBoaInPwdHist findById(Long id);

	public List<HtBoaInPwdHist> findByUserId(String userId);

}
