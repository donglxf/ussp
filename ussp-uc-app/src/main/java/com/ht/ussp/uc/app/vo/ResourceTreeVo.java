/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: ResourceTreeVo.java
 * Author:   谭荣巧
 * Date:     2018/1/27 10:37
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 资源树数据对象<br>
 * 用于角色资源维护界面<br>
 *
 * @author 谭荣巧
 * @Date 2018/1/27 10:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceTreeVo {
    private String code;
    private String name;
    private String nameCn;
    private String parentCode;
    private String ischecked;//是否勾选
    private String app;
    private List<ResourceTreeItemVo> btns;//按钮权限资源
    private List<ResourceTreeItemVo> tabs;//tab权限资源
    private List<ResourceTreeItemVo> apis;//api权限资源
}
