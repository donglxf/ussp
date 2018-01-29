/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: ResourceTreeItemVo.java
 * Author:   谭荣巧
 * Date:     2018/1/27 10:38
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源树数据对象的子项<br>
 * 用于角色资源维护界面<br>
 *
 * @author 谭荣巧
 * @Date 2018/1/27 10:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceTreeItemVo {
    private String code;
    private String name;
    private String nameCn;
    private boolean ischecked;//是否勾选
}
