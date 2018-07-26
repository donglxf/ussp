package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ht.ussp.uc.app.domain.HtBoaInCompanyAccount;

@Repository
public interface HtBoaInCompanyAccountRepository extends JpaRepository<HtBoaInCompanyAccount, Long> {

	public HtBoaInCompanyAccount findByAccountCode(String accountCode);

	//区间账户或还款账户，故list
	public List<HtBoaInCompanyAccount> findByCompanyCodeAndDelFlag(String companyCode, Boolean flag);

	public List<HtBoaInCompanyAccount> findByAccountCodeAndDelFlag(String accountCode, Boolean flag);

	@Modifying(clearAutomatically = true)
	@Query(value = "update HT_BOA_IN_COMPANY_ACCOUNT set DEL_FLAG=1 where ACCOUNT_CODE=:accountCode", nativeQuery = true)
	public int delComAcc(@Param("accountCode") String accountCode);
}
