package com.ht.ussp.gateway.app.util;

/**
 * 
 * @ClassName: SysStatus
 * @Description: 状态码信息
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月8日 下午8:21:24
 */
public enum SysStatus {
	SUCCESS(200, "成功"),
	ERROR(500, "错误"),
	PARAM_ERROR(700, "请求参数错误"),
	PWD(709, "密码错误"),
	PWD_ISNULL(7091, "密码为空"),
	PWD_INVALID(7092, "密码不正确"),
	PWD_EQUAL(7093, "和原密码相同"),
	PWD_LOCKING(7094,"错误登录次数过多，账户24小时之内已被锁定"),
	MAILPARAM_ERROR(9999, "服务器错误,缺少认证参数或服务器错误统一返回此参数");

	private SysStatus(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	private int status;

	private String msg;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
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

