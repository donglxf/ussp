/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: 谭荣巧
 * Author:   tanrq
 * Date:     2018/1/12 18:52
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.vo;

import lombok.Data;

import java.util.Map;

/**
 * 分页查询参数对象<br>
 * 包含页码、每页记录数、关键字、查询条件<br>
 *
 * @author 谭荣巧
 * @Date 2018/1/12 18:52
 */
@Data
public class Page {
    int page;
    int limit;
    String keyWord;
    Map<String, String> query;

    public int getPage() {
        return page - 1;
    }
}
