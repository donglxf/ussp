package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ht.ussp.uc.app.domain.HtBoaInCompanyAccount;

@Repository
public interface HtBoaInCompanyAccountRepository extends JpaRepository<HtBoaInCompanyAccount, Long>{

}
