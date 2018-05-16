package com.ht.ussp.ouc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.ouc.app.domain.HtBoaOutPwdHist;


public interface HtBoaOutPwdHistRepository
        extends JpaRepository<HtBoaOutPwdHist, Long> {

    public HtBoaOutPwdHist findById(Long id);

}
