/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: MenuVo.java
 * Author:   谭荣巧
 * Date:     2018/1/22 11:51
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
 * 菜单数据对象<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/1/22 11:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuVo {
    private String id;//菜单编码
    private String icon;//菜单图标
    private boolean spread;//是否展开
    private String title;//菜单名称
    private String url;//菜单链接
    private List<MenuVo> children;//下级菜单
}
