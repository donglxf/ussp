/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: ResourceTreePageVo.java
 * Author:   谭荣巧
 * Date:     2018/1/27 14:34
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 资源树分页数据对象<br>
 * 用于角色资源维护界面<br>
 *
 * @author 谭荣巧
 * @Date 2018/1/27 14:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceTreePageVo {
    private List<ResourceTreeVo> rows; //行数据
    private int total; //数据总记录数，用于分页
    private String message; //获取数据失败后的消息
    private boolean flag; //数据是否获取成功
}
