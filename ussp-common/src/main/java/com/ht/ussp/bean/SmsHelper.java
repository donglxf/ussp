package com.ht.ussp.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ht.ussp.client.OucClient;
import com.ht.ussp.client.SmsClient;
import com.ht.ussp.client.dto.MsgReqDtoIn;
import com.ht.ussp.client.dto.MsgResDtoOut;
import com.ht.ussp.core.Result;
import com.ht.ussp.util.LogicUtil;

import lombok.extern.log4j.Log4j2;

/**
 * 
 * @ClassName: SmsHelper
 * @Description: 短信相关接口
 * @author wim qiuwenwu@hongte.info
 * @date 2018年5月8日 下午3:52:39
 */
@Log4j2
@Component
public class SmsHelper {

	@Autowired
	private SmsClient smsClient;

	@Autowired(required = false)
	private OucClient oucClient;

	public Boolean sendMsg(MsgReqDtoIn msgReqDtoIn) {
		if (LogicUtil.isNull(msgReqDtoIn)) {
			return false;
		}
		if (StringUtils.isEmpty(msgReqDtoIn.getMsgBody()) && StringUtils.isEmpty(msgReqDtoIn.getMsgModelId())
				&& StringUtils.isEmpty(msgReqDtoIn.getMsgTo()) && StringUtils.isEmpty(msgReqDtoIn.getApp())) {
			log.error("--------参数不能为空！----------");
			return false;
		}

		Result<MsgResDtoOut> result = smsClient.sendMsg(msgReqDtoIn);
		if (result == null || !"0000".equals(result.getReturnCode())) {
			return false;
		}

		Boolean saveSms = oucClient.saveSmsToRedis(msgReqDtoIn.getMsgTo(), msgReqDtoIn.getMsgBody(),
				msgReqDtoIn.getApp());

		if (saveSms == false) {
			log.error("--------保存短信失败！----------");
			return false;
		}
		return true;
	}

	public Boolean validateSmsCode(String telephone, String code, String app) {
		if (StringUtils.isEmpty(code)) {
			return false;
		}
		Boolean validateSmscode = oucClient.validateSmsCode(telephone, code, app);
		if (validateSmscode == false) {
			log.error("--------验证码有误！----------");
			return false;
		}
		return true;
	}

}
