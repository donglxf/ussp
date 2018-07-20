package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ht.ussp.common.SysStatus;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInCompanyAccount;
import com.ht.ussp.uc.app.dto.HtBoaInCompanyAccountDTO;
import com.ht.ussp.uc.app.repository.HtBoaInCompanyAccountRepository;

import cn.hutool.core.bean.BeanUtil;

@Service
public class HtBoaInCompanyAccountService {
	@Autowired
	private HtBoaInCompanyAccountRepository htBoaInCompanyAccountRepository;

	/**
	 * 
	 * @Title: updateComAcc 
	 * @Description: 修改公司账户 
	 * @return Result
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年7月20日 上午11:14:23
	 */
	public Result updateComAcc(HtBoaInCompanyAccountDTO htBoaInCompanyAccountDTO) {

		return Result.buildSuccess();
	}

	/**
	 * 
	 * @Title: delComAcc 
	 * @Description: 删除公司账户----逻辑删除 
	 * @return Result
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年7月20日 上午11:14:12
	 */
	public Result delComAcc(String accountCode) {
		htBoaInCompanyAccountRepository.delComAcc(accountCode);
		return Result.buildSuccess();
	}

	/**
	 * 
	 * @Title: addComAcc 
	 * @Description: 新增公司账户 
	 * @return Result
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年7月20日 上午11:14:00
	 */
	@Transactional(rollbackFor = Exception.class)
	public Result addComAcc(HtBoaInCompanyAccountDTO htBoaInCompanyAccountDTO, String userId) {
		// 如果有记录，且标记为未删除，不可新增
		try {
			List<HtBoaInCompanyAccount> oldhtBoaInCompanyAccount = htBoaInCompanyAccountRepository
					.findByAccountCodeAndDelFlag(htBoaInCompanyAccountDTO.getAccountCode(), false);
			if (null != oldhtBoaInCompanyAccount&&!oldhtBoaInCompanyAccount.isEmpty()) {
				return Result.buildFail(SysStatus.RECORD_HAS_EXIST);
			}
			HtBoaInCompanyAccount result=new HtBoaInCompanyAccount();
			
			BeanUtil.copyProperties(htBoaInCompanyAccountDTO, result);
			result.setCreateOperator(userId);
			result.setDelFlag(false);
			htBoaInCompanyAccountRepository.save(result);
		} catch (Exception e) {
			return Result.buildFail();
		}
		return Result.buildSuccess();
	}

	/**
	 * 
	 * @Title: getComAccByCompanyCode 
	 * @Description: 通过分公司编码查询账户 
	 * @return HtBoaInCompanyAccount
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年7月20日 上午11:13:47
	 */
	public List<HtBoaInCompanyAccount> getComAccByCompanyCode(String companyCode) {
		return htBoaInCompanyAccountRepository.findByCompanyCodeAndDelFlag(companyCode, false);
	}
}
