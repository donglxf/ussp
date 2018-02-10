/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: Page.java
 * Author:   谭荣巧
 * Date:     2018/1/12 18:52
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;

import java.util.Map;

/**
 * 分页查询参数对象<br>
 * 包含页码、每页记录数、关键字、查询条件<br>
 *
 * @author 谭荣巧
 * @Date 2018/1/12 18:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageVo {
    int page;
    int limit;
    String keyWord;
    String orgCode;
    Map<String, String> query;

    public int getPage() {
        return page - 1;
    }

    public PageRequest getPageRequest() {
        return new PageRequest(getPage(), limit);
    }
}
