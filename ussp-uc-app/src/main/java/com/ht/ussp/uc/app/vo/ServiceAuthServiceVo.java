/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: RelevanceApiVo.java
 * Author:   谭荣巧
 * Date:     2018/1/20 15:23
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.vo;

import java.util.List;

import com.ht.ussp.uc.app.domain.HtBoaInResource;
import com.ht.ussp.uc.app.domain.HtBoaInService;
import com.ht.ussp.uc.app.domain.HtBoaInServiceApi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

 
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAuthServiceVo {
	String mainServiceCode;
    List<HtBoaInService> serviceList;
    
    String authServiceCode;
    List<HtBoaInServiceApi> serviceApiList;
    
    List<HtBoaInResource> recourceApiList;
}
