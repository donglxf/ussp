package com.ht.ussp.uc.app.vo;

import java.util.Set;

import lombok.Data;

@Data
public class EmailVo {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	// 主题
	private String subject;
	// 正文
	private String text;
	// 发送方邮件地址
	private String from;
	//收件方邮件地址列表
	private Set to;
	//密送
	private String bcc;
	//回复方邮件地址
	private String replyTo;
	





 

}
