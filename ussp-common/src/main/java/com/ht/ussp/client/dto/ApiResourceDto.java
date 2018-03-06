/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: ApiResourceDto.java
 * Author:   谭荣巧
 * Date:     2018/1/18 19:32
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * API资源数据传输自定义对象<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/1/18 19:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResourceDto {
    private boolean isDeleteOld = false;
    private String app;
    private List<ApiInfoDto> apiInfoList = new ArrayList<>();

    public void add(String mapping, String method, String apiDescribe) {
        apiInfoList.add(new ApiInfoDto(mapping, method, apiDescribe));
    }

}