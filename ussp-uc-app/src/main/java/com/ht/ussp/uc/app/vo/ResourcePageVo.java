/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: ResourcePageVo.java
 * Author:   谭荣巧
 * Date:     2018/1/18 22:02
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权限资源分页查询参数对象<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/1/18 22:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourcePageVo extends PageVo {
    private String app;
    private String parentCode;
    private String resType;
}
