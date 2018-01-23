package com.ht.ussp.uc.app.model;

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
	INVALID_USER("9901","用户名或密码无效"),
	USER_NOT_FOUND("9902","用户不存在"),
	USER_HAS_DELETED("9903","您的用户已被删除"),
	USER_NOT_RELATE_APP("9904","该用户未与任何系统关联"),
	USER_NOT_MATCH_APP("9905","用户来源不正确"),
	
	
	PWD("9910", "密码错误"),
	PWD_ISNULL("9911", "密码为空"),
	PWD_INVALID("9912", "密码不正确"),
	PWD_EQUAL("9913", "和原密码相同"),
	PWD_LOCKING("9914","错误登录次数过多，账户24小时之内已被锁定"),
	
	NO_ROLE("9920","该用户没关联角色"),
	TOKEN_IS_EXPIRED("9921","TOKEN过期"),
	TOKEN_IS_VALID("9922","验签失败"),
	API_NOT_NULL("9923","API权限不能为空"),
	AUTH_FAILED("9924","授权失败"),

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

