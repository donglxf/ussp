package com.ht.ussp.uc.app.resource;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.common.SysStatus;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInCompany;
import com.ht.ussp.uc.app.domain.HtBoaInCompanyAccount;
import com.ht.ussp.uc.app.dto.HtBoaInCompanyAccountDTO;
import com.ht.ussp.uc.app.dto.HtBoaInCompanyDTO;
import com.ht.ussp.uc.app.service.HtBoaInCompanyAccountService;
import com.ht.ussp.uc.app.service.HtBoaInCompanyService;
import com.ht.ussp.util.BeanUtils;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

/**
 * 
 * @ClassName: CompanyResource
 * @Description: 公司相关接口
 * @author wim qiuwenwu@hongte.info
 * @date 2018年7月19日 下午5:04:02
 */

@RestController
@RequestMapping(value = "/company")
@Log4j2
public class CompanyResource {
	@Autowired
	private HtBoaInCompanyService htBoaInCompanyService;

	@Autowired
	private HtBoaInCompanyAccountService HtBoaInCompanyAccountService;

	/**
	 * 
	 * @Title: getCompanyInfoByUserId 
	 * @Description: 通过用户ID获取company信息 
	 * @return Result
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年7月19日 下午5:07:41
	 */
	@GetMapping(value = "/getCompanyInfoByUserId")
	@ApiOperation(value = "通过userId获取分公司详情")
	public Result getCompanyInfoByUserId(@RequestParam("userId") String userId) {
		if (null != userId && userId.length() > 0) {
			// 通过用户ID获取业务机构码
			String businessOrgCode = htBoaInCompanyService.getBusinessOrgCode(userId);
			if (StringUtils.isEmpty(businessOrgCode)) {
				return Result.buildFail(SysStatus.NO_RESULT);
			}
			// 通过业务机构码获取分公司机构码
			String branchCode = htBoaInCompanyService.getBranchCode(businessOrgCode);
			if (StringUtils.isEmpty(branchCode)) {
				return Result.buildFail(SysStatus.NO_RESULT);
			}
			// 分公司机构码获取公司信息
			HtBoaInCompany htBoaInCompany = htBoaInCompanyService.findByCompanyCode(branchCode);
			if (null == htBoaInCompany) {
				return Result.buildFail(SysStatus.NO_RESULT);
			}
			HtBoaInCompanyDTO htBoaInCompanyDTO = new HtBoaInCompanyDTO();
			BeanUtils.deepCopy(htBoaInCompany, htBoaInCompanyDTO);
			return Result.buildSuccess(htBoaInCompanyDTO);
		} else {
			return Result.buildFail(SysStatus.ERROR_PARAM);
		}
	}

	/**
	 * 
	 * @Title: updateCompanyInfo 
	 * @Description: 修改分公司信息 
	 * @return Result
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年7月20日 上午9:03:37
	 */
	@PostMapping(value = "/updateCompanyInfo")
	@ApiOperation(value = "修改分公司信息")
	public Result updateCompanyInfo(@RequestBody HtBoaInCompanyDTO htBoaInCompanyDTO,
			@RequestParam("userId") String userId) {
		if (null == htBoaInCompanyDTO || StringUtils.isEmpty(htBoaInCompanyDTO.getCompanyCode())
				|| StringUtils.isBlank(userId)) {
			return Result.buildFail(SysStatus.ERROR_PARAM);
		}
		try {
			Result result = htBoaInCompanyService.updateCompanyInfo(htBoaInCompanyDTO, userId);
			if ("9995".equals(result.getReturnCode())) {
				return Result.buildFail(SysStatus.RECORD_HAS_DELETED);
			}
			if ("9996".equals(result.getReturnCode())) {
				return Result.buildFail(SysStatus.NO_RESULT);
			}
		} catch (Exception e) {
			log.debug("-----修改分公司信息失败：" + e.getStackTrace());
			return Result.buildFail();
		}
		return Result.buildSuccess();

	}

	/**
	 * 
	 * @Title: getComAccByCompanyCode 
	 * @Description: 通过分公司编码查询账户 
	 * @return Result
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年7月20日 上午11:34:24
	 */
	@GetMapping(value = "/getComAccByCompanyCode")
	@ApiOperation(value = "通过分公司编码查询账户")
	public Result getComAccByCompanyCode(@RequestParam("companyCode") String companyCode) {
		if (null == companyCode || companyCode.length() == 0) {
			return Result.buildFail(SysStatus.ERROR_PARAM);
		}
		try {
			List<HtBoaInCompanyAccount> HtBoaInCompanyAccount = HtBoaInCompanyAccountService
					.getComAccByCompanyCode(companyCode);
			if (null == HtBoaInCompanyAccount||HtBoaInCompanyAccount.isEmpty()) {
				return Result.buildFail(SysStatus.NO_RESULT);
			}
			if(HtBoaInCompanyAccount.size()>1) {
				log.debug("---记录有多条，机构编码是："+companyCode);
				return Result.buildFail(SysStatus.EXCEPTION);
			}
			HtBoaInCompanyAccountDTO htBoaInCompanyAccountDTO=new HtBoaInCompanyAccountDTO();
			BeanUtils.deepCopy(HtBoaInCompanyAccount.get(0),htBoaInCompanyAccountDTO);
			return Result.buildSuccess(htBoaInCompanyAccountDTO);
		} catch (Exception e) {
			log.debug("---查询分公司账户失败---" + e.getMessage());
			return Result.buildFail();
		}
	}

	/**
	 * 
	 * @Title: addComAcc 
	 * @Description: 新增公司账户 
	 * @return Result
	 * @throws
	 * @author wim qiuwenwu@hongte.info 
	 * @date 2018年7月20日 下午2:23:52
	 */
	@PostMapping(value = "/addComAcc")
	@ApiOperation(value = "新增公司账户")
	public Result addComAcc(@RequestBody HtBoaInCompanyAccountDTO htBoaInCompanyAccountDTO,
			@RequestParam("userId") String userId) {
		if (null == htBoaInCompanyAccountDTO || StringUtils.isEmpty(htBoaInCompanyAccountDTO.getAccountCode())
				|| StringUtils.isBlank(userId)) {
			return Result.buildFail(SysStatus.ERROR_PARAM);
		}
		Result result = HtBoaInCompanyAccountService.addComAcc(htBoaInCompanyAccountDTO, userId);
			System.out.println(result);
			if ("0000".equals(result.getReturnCode())) {
				return Result.buildSuccess();
			} else if ("9994".equals(result.getReturnCode())) {
				return Result.buildFail(SysStatus.RECORD_HAS_EXIST);
			} else {
				return Result.buildFail();
			}
	}
	

}
