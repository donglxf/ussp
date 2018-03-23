package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInContrast;
import com.ht.ussp.uc.app.repository.HtBoaInContrastRepository;

@Service
public class SynDataService {

    @Autowired
    private HtBoaInContrastRepository htBoaInContrastRepository;

    public List<HtBoaInContrast> findListByType(String type) {
    	return htBoaInContrastRepository.findByType(type);
    }
}
