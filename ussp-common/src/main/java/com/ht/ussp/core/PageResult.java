/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: 谭荣巧
 * Author:   tanrq
 * Date:     2018/1/11 15:46
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页结构对象<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/1/11 15:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<D> extends Result<D> {

    private long count;

    /**
     * 设置总记录数<br>
     *
     * @param count 总记录数
     * @return com.ht.ussp.core.PageResult 分页结果对象
     * @author 谭荣巧
     * @Date 2018/1/11 15:58
     */
    public PageResult<D> count(long count) {
        this.count = count;
        return this;
    }

    /**
     * 构建成功结果对象<br>
     *
     * @param data  返回的数据
     * @param count 总记录数
     * @return 分页结果对象
     * @author 谭荣巧
     * @Date 2018/1/25 21:36
     */
    public static <D> PageResult<D> buildSuccess(D data, long count) {
        PageResult<D> result = new PageResult<>();
        result.count(count).returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc()).data(data);
        return result;
    }
}
