package com.ht.ussp.client.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @ClassName: MsgReqDtoIn
 * @Description: 短信请求DTO
 * @author wim qiuwenwu@hongte.info
 * @date 2018年5月8日 下午3:45:50
 */
@Setter
@Getter
public class MsgReqDtoIn implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 系统编码
	 */
	private String app;

	/**
	 * 消息标题
	 */
	private String msgTitle;
	/**
	 * 消息密送bcc,可多个
	 */
	private String msgBcc;
	/**
	 * 消息抄送cc,可多个
	 */
	private String msgCc;
	/**
	 * 业务请求编号
	 */
	private Long msgRequestId;
	/**
	 * to接收人地址手机号，微信号，钉钉号等,可多个
	 */
	private String msgTo;
	/**
	 * 消息正文或者模版参数
	 */
	private Object msgBody;
	/**
	 * 消息模型id
	 */
	private Long msgModelId;
	/**
	 * 是否一次发送1分开发送2分开发送
	 */
	private Integer msgOnceSend;

}
