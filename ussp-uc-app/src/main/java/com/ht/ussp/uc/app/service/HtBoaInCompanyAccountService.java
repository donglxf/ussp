package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esotericsoftware.minlog.Log;
import com.ht.ussp.common.SysStatus;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInCompanyAccount;
import com.ht.ussp.uc.app.dto.HtBoaInCompanyAccountDTO;
import com.ht.ussp.uc.app.repository.HtBoaInCompanyAccountRepository;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;

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
	@Transactional(rollbackFor = Exception.class)
	public Result updateComAcc(HtBoaInCompanyAccountDTO htBoaInCompanyAccountDTO, String userId) {
		if (null != htBoaInCompanyAccountDTO && null != htBoaInCompanyAccountDTO.getAccountCode()
				&& htBoaInCompanyAccountDTO.getAccountCode().length() > 0) {
			HtBoaInCompanyAccount htBoaInCompanyAccount = htBoaInCompanyAccountRepository
					.findByAccountCode(htBoaInCompanyAccountDTO.getAccountCode());
			if (null == htBoaInCompanyAccount) {
				return Result.buildFail(SysStatus.NO_RESULT);
			} else if (null != htBoaInCompanyAccount.getDelFlag() && htBoaInCompanyAccount.getDelFlag() == true) {
				return Result.buildFail(SysStatus.RECORD_HAS_DELETED);
			} else if (null == htBoaInCompanyAccount.getDelFlag()) {
				htBoaInCompanyAccount.setDelFlag(false);
			}
			htBoaInCompanyAccount.setUpdateOperator(userId);

			BeanUtil.copyProperties(htBoaInCompanyAccountDTO, htBoaInCompanyAccount, new CopyOptions() {
				{
					setIgnoreNullValue(true);
				}
			});

			htBoaInCompanyAccountRepository.save(htBoaInCompanyAccount);
			return Result.buildSuccess();

		} else {
			return Result.buildFail(SysStatus.ERROR_PARAM);
		}
	}

	/**
	 * 
	 * @Title: delComAcc 
	 * @Description: 删除公司账户----逻辑删除 
	 * @return Result
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年7月20日 上午11:14:12s
	 */
	@Transactional(rollbackFor = Exception.class)
	public Result delComAcc(String accountCode) {
		HtBoaInCompanyAccount htBoaInCompanyAccount = htBoaInCompanyAccountRepository.findByAccountCode(accountCode);
		if (null == htBoaInCompanyAccount || htBoaInCompanyAccount.getDelFlag() == true) {
			Log.debug("无记录或被标记为已删除：" + htBoaInCompanyAccount.getDelFlag());
			return Result.buildFail(SysStatus.NO_RESULT);
		}
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

		try {
			// 如果有记录，且标记为未删除，不可新增
			List<HtBoaInCompanyAccount> oldhtBoaInCompanyAccount = htBoaInCompanyAccountRepository
					.findByAccountCodeAndDelFlag(htBoaInCompanyAccountDTO.getAccountCode(), false);
			if (null != oldhtBoaInCompanyAccount && !oldhtBoaInCompanyAccount.isEmpty()) {
				return Result.buildFail(SysStatus.RECORD_HAS_EXIST);
			}
			HtBoaInCompanyAccount result = new HtBoaInCompanyAccount();

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
