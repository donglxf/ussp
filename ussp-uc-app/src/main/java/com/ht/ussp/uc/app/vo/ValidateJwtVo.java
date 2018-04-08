package com.ht.ussp.uc.app.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @ClassName: ValidateJwtVo
 * @Description: 用户验证JWT返回结果VO
 * @author wim qiuwenwu@hongte.info
 * @date 2018年3月13日 上午11:04:17
 */
@Getter
@Setter
public class ValidateJwtVo {

	private String userId;

	private String orgCode;
}
