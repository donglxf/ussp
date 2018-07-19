package com.ht.ussp.uc.app.resource;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
import com.ht.ussp.uc.app.dto.HtBoaInCompanyDTO;
import com.ht.ussp.uc.app.repository.HtBoaInUserExtRepository;
import com.ht.ussp.uc.app.service.HtBoaInCompanyService;
import com.ht.ussp.util.BeanUtils;

import cn.hutool.core.bean.BeanUtil;
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
	private HtBoaInUserExtRepository htBoaInUserExtRepository;

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

	@PostMapping(value = "/updateCompanyInfo")
	@ApiOperation(value = "修改分公司信息")
	public Result updateCompanyInfo(@RequestBody HtBoaInCompanyDTO htBoaInCompanyDTO,
			@RequestParam("userId") String userId) {
		if (null == htBoaInCompanyDTO && StringUtils.isEmpty(htBoaInCompanyDTO.getCompanyCode())) {
			return Result.buildFail(SysStatus.ERROR_PARAM);
		}
		try {
			htBoaInCompanyService.updateCompanyInfo(htBoaInCompanyDTO, userId);
		} catch (Exception e) {
			log.debug("-----修改分公司信息失败："+e.getStackTrace());
			return Result.buildFail();
		}
		return Result.buildSuccess();

	}

}
