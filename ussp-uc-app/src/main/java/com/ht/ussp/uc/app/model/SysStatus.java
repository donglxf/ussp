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
	ERROR("500", "错误"),
	ERROR_PARAM("9997", "参数错误"),
	USER_NOT_FOUND("410","用户不存在"),
	USER_HAS_DELETED("411","您的用户已被删除"),
	USER_NOT_RELATE_APP("412","该用户未与任何系统关联"),
	USER_NOT_MATCH_APP("413","用户来源不正确"),
	NO_ROLE("414","该用户没关联角色"),
	API_NOT_NULL("415","API权限不能为空"),
	PWD("709", "密码错误"),
	PWD_ISNULL("1000", "密码为空"),
	PWD_INVALID("1001", "密码不正确"),
	PWD_EQUAL("1002", "和原密码相同"),
	PWD_LOCKING("1003","错误登录次数过多，账户24小时之内已被锁定"),
	NO_RESULT("9997","查无数据"),
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

