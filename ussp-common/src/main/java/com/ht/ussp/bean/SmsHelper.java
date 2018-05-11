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
	private OucClient OucClient;

	public Boolean sendMsg(MsgReqDtoIn msgReqDtoIn) {
		Boolean flag;
		if (LogicUtil.isNull(msgReqDtoIn)) {
			flag = false;
		}
		if (StringUtils.isEmpty(msgReqDtoIn.getMsgBody()) && StringUtils.isEmpty(msgReqDtoIn.getMsgModelId())
				&& StringUtils.isEmpty(msgReqDtoIn.getMsgTo())&&StringUtils.isEmpty(msgReqDtoIn.getApp())) {
			log.error("--------参数不能为空！----------");
			flag = false;
		}

		Result<MsgResDtoOut> result = smsClient.sendMsg(msgReqDtoIn);
		if (result == null || !"0000".equals(result.getReturnCode())) {
			flag = false;
		}

		Boolean saveSms = OucClient.saveSmsToRedis(msgReqDtoIn.getMsgTo(), msgReqDtoIn.getMsgBody().toString(),msgReqDtoIn.getApp());

		if (saveSms == false) {
			log.error("--------保存短信失败！----------");
			flag = false;
		}
		flag = true;
		return flag;
	}

}
