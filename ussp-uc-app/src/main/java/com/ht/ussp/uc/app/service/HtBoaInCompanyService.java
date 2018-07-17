package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInCompany;
import com.ht.ussp.uc.app.model.BoaInBusiOrgInfo;
import com.ht.ussp.uc.app.repository.HtBoaInCompanyRepository;

@Service
public class HtBoaInCompanyService {

	@Autowired
	private HtBoaInCompanyRepository htBoaInCompanyRepository;

	public List<HtBoaInCompany> findAll(HtBoaInCompany u) {
		ExampleMatcher matcher = ExampleMatcher.matching();
		Example<HtBoaInCompany> ex = Example.of(u, matcher);
		return this.htBoaInCompanyRepository.findAll(ex);
	}

	public List<HtBoaInCompany> findAll() {
		return this.htBoaInCompanyRepository.findAll();
	}

	public List<HtBoaInCompany> add(List<HtBoaInCompany> orgList) {
		return this.htBoaInCompanyRepository.save(orgList);
	}

	public HtBoaInCompany save(HtBoaInCompany u) {
		return this.htBoaInCompanyRepository.save(u);
	}

	public List<BoaInBusiOrgInfo> getCompnayInfo(String branchCode) {
		return htBoaInCompanyRepository.listCompnayInfo(branchCode);
	}

}
