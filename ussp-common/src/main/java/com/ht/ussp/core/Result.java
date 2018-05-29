/*
 * 文件名 Result.java
 * 版权 Copyright 2017 团贷网
 * 创建人 谭荣巧
 * 创建时间 2017年11月20日 下午3:40:50 
 */
package com.ht.ussp.core;

import java.io.Serializable;

import com.ht.ussp.common.SysStatus;

import lombok.Data;

/**
 * Rest接口请求结果对象<br>
 * 
 * @author 谭荣巧
 * @date 2017年11月20日 下午3:40:50
 */
@Data
public class Result<T> implements Serializable {
	/**
	 * 返回码
	 */
	private String returnCode;

	/**
	 * 返回码描述
	 */
	private String codeDesc;

	/**
	 * 详情
	 */
	private String msg;
	/**
	 * 传递给请求者的数据
	 */
	private T data;

	public Result() {
		super();
	}

    /**
     * 构建成功报文
     * @param data
     * @param <T>
     * @return
     */
	public static <T> Result<T> buildSuccess(T data){
        Result<T> result= new Result<T>();
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc()).data(data);
	    return result;
    }

    public static Result buildSuccess(){
        Result result= new Result();
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
        return result;
    }

    /**
     * 构建失败报文
     * @param <T>
     * @return
     */
    public static <T> Result<T> buildFail(){
        Result<T> result= new Result<T>();
        result.returnCode(ReturnCodeEnum.FAIL.getReturnCode()).codeDesc(ReturnCodeEnum.FAIL.getCodeDesc());
        return result;
    }
    
    /**
     * 构建失败报文
     * @param <T>
     * @return
     */
    public static <T> Result<T> buildFail(String codeDesc,String msg){
        Result<T> result= new Result<T>();
        result.returnCode(ReturnCodeEnum.FAIL.getReturnCode()).codeDesc(ReturnCodeEnum.FAIL.getCodeDesc());
        result.codeDesc(codeDesc);
        result.msg(msg);
        return result;
    }
    
    /**
     * 
     * @Title: buildFailConvert 
     * @Description: 返回码使用returnCode 
     * @return Result<T>
     * @throws
     * @author wim qiuwenwu@hongte.info 
     * @date 2018年5月28日 下午4:37:11
     */
    public static <T> Result<T> buildFailConvert(String returnCode,String msg){
        Result<T> result= new Result<T>();
        result.setReturnCode(returnCode);
        result.setMsg(msg);
        return result;
    }
    
    /**
     * 
     * @Title: buildFail 
     * @Description: 直接使用SysStatus枚举类 
     * @return Result<T>
     * @throws
     * @author wim qiuwenwu@hongte.info 
     * @date 2018年5月28日 下午4:36:43
     */
    public static <T> Result<T> buildFail(SysStatus sysStatus){
        Result<T> result= new Result<T>();
        result.setReturnCode(sysStatus.getStatus());
        result.setMsg(sysStatus.getMsg());
        return result;
    }

    public static  Result build(ReturnCodeEnum codeEnum){
        Result result= new Result();
        result.returnCode(codeEnum.getReturnCode()).codeDesc(codeEnum.getCodeDesc());
        return result;
    }


	public Result<T> returnCode(String errorCode){
		this.returnCode = errorCode;
		return this;
	}

	public Result<T> codeDesc(String codeDesc){
		this.codeDesc = codeDesc;
		return this;
	}

	public Result<T> msg(String msg){
		this.msg = msg;
		return this;
	}

	public Result<T> data(T data){
		this.data = data;
		return this;
	}
} 
