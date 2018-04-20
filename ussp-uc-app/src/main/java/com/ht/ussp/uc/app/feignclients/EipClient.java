package com.ht.ussp.uc.app.feignclients;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.vo.EmailVo;
import com.ht.ussp.uc.app.vo.eip.DDGetAuthDeptReqDto;
import com.ht.ussp.uc.app.vo.eip.DDGetAuthDeptRespDto;
import com.ht.ussp.uc.app.vo.eip.DDGetDeptReqDto;
import com.ht.ussp.uc.app.vo.eip.DDGetDeptRespDto;
import com.ht.ussp.uc.app.vo.eip.DDGetDeptUserReqDto;
import com.ht.ussp.uc.app.vo.eip.DDGetDeptUserRespDto;
import com.ht.ussp.uc.app.vo.eip.DDGetTokenReqDto;
import com.ht.ussp.uc.app.vo.eip.DDGetTokenRespDto;

/**
 * 外联平台
 */
@FeignClient(value = "eip-out")
public interface EipClient {

	/**
	 * 调用外联平台发送邮件
	 * 
	 * @param emailVo
	 * @return
	 */
	@PostMapping(value = "/eip/common/sendEmail", headers = { "app=UC", "content-type=application/json" })
	public Result<EmailVo> sendEmail(@RequestBody EmailVo emailVo);

	@PostMapping(value = "/eip/common/getDDToken", headers = { "app=UC", "content-type=application/json" })
	public DDGetTokenRespDto getDDToken(@RequestBody DDGetTokenReqDto ddGetTokenReqDto);
	
	@PostMapping(value = "/eip/common/getDDAuthDept", headers = { "app=UC", "content-type=application/json" })
	public DDGetAuthDeptRespDto getDDAuthDept(@RequestBody DDGetAuthDeptReqDto ddGetAuthDeptReqDto);
	
	@PostMapping(value = "/eip/common/getDDDept", headers = { "app=UC", "content-type=application/json" })
	public DDGetDeptRespDto getDDDept(@RequestBody DDGetDeptReqDto ddGetDeptReqDto);
	
	@PostMapping(value = "/eip/common/getDDDeptUser", headers = { "app=UC", "content-type=application/json" })
	public DDGetDeptUserRespDto getDDDeptUser(@RequestBody DDGetDeptUserReqDto ddGetDeptUserReqDto);
}
