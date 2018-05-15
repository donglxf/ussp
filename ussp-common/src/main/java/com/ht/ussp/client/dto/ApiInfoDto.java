/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: ApiInfoDto.java
 * Author:   谭荣巧
 * Date:     2018/1/18 19:41
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API信息数据对象<br>
 * 包含API映射、方法名、API描述<br>
 *
 * @author 谭荣巧
 * @Date 2018/1/18 19:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiInfoDto {
    private String mapping;
    private String method;
    private String apiDescribe;
}
