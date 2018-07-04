package com.ht.ussp.common;

/**
 * 
 * @ClassName: SysStatus
 * @Description: 状态码信息
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月8日 下午8:21:24
 */
public enum SysStatus {
	SUCCESS("0000", "执行成功"),
	
	
	METHOD_NOT_SUPPORTED("9900","不支持该请求方式"),
	INVALID_USER("9901","用户名或密码错误"),
	USER_NOT_FOUND("9902","用户状态异常"),
	USER_HAS_DELETED("9903","您的用户已被删除"),
	USER_NOT_RELATE_APP("9904","该用户未与系统关联"),
	USER_NOT_MATCH_APP("9905","用户来源不正确"),
	USER_HAS_LOCKED("9906","您的用户已被锁定"),
	
	PWD("9910", "密码错误"),
	PWD_ISNULL("9911", "密码为空"),
	PWD_INVALID("9912", "密码不正确"),
	PWD_EQUAL("9913", "和原密码相同"),
	PWD_LOCKING("9914","错误登录次数过多，账户24小时之内已被锁定"),
	PWD_FIRST_MODIFY("0009","密码太简单，请重新设置"),
	
	NO_ROLE("9920","该用户没关联角色"),
	TOKEN_IS_EXPIRED("9921","TOKEN过期"),
	TOKEN_IS_VALID("9922","TOKEN无效"),
	API_NOT_NULL("9923","API权限不能为空"),
	AUTH_FAILED("9924","授权失败"),
	TOKEN_IS_NULL("9925","TOKEN不能为空"),
	HAS_NO_ACCESS("9926","您没有访问权限"),
	HEADER_CANNOT_NULL("9927","HEADER不能为空"),
	APP_CANNOT_NULL("9928","APP不能为空"),
	SMS_CODE_VALID("9930","验证码无效"),
	SMS_CODE_FAIL("9931","验证码错误"),
	CLIENT_NOT_REGISTERED("9932","该客户端未注册"),
	CLIENT_IS_VALID("9933","客户端信息错误"),
	TOKEN_AND_IEME_NOT_NULL("9934","TOKEN和IEME不能为空"),
	ORIGIN_VALID("9935","来源非法"),
	REDIS_TOKEN_NULL("9936","缓存中没有该TOKEN"),	
	REDIS_TOKEN_VALID("9937","TOKEN与缓存中的不匹配"),
	
	
	NO_RESULT("9996","查无数据"),
	ERROR_PARAM("9997", "参数错误"),
	EXCEPTION("9998", "执行异常"),
	FAIL("9999", "执行失败"); 
	
	private SysStatus(String status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	private String status;

	private String msg;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public SysStatus setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public SysStatus appendMsg(String msg) {
		this.msg = null;
		this.msg = msg;
		return this;
	}
}

