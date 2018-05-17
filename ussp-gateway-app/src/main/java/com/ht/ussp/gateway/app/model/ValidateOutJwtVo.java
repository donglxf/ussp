package com.ht.ussp.gateway.app.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @ClassName: ValidateOutJwtVo
 * @Description: 外部用户鉴权解析结果
 * @author wim qiuwenwu@hongte.info
 * @date 2018年5月17日 下午5:19:58
 */
@Getter
@Setter
public class ValidateOutJwtVo {

	private String userId;

	private String ieme;
}
