package com.ht.ussp.ouc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.ht.ussp.ouc.app.domain.HtBoaOutPwdHist;
import com.ht.ussp.ouc.app.repository.HtBoaOutPwdHistRepository;

@Service
public class HtBoaOutPwdHistService {
    @Autowired
    private HtBoaOutPwdHistRepository htBoaOutPwdHistRepository;

    public HtBoaOutPwdHist findById(Long id) {
        return this.htBoaOutPwdHistRepository.findById(id);
    }

    public List<HtBoaOutPwdHist> findAll(HtBoaOutPwdHist u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaOutPwdHist> ex = Example.of(u, matcher);
        return this.htBoaOutPwdHistRepository.findAll(ex);
    }

    public HtBoaOutPwdHist save(HtBoaOutPwdHist u) {
        return this.htBoaOutPwdHistRepository.save(u);
    }

}
