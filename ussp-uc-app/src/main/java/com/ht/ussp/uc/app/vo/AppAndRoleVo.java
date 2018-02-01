/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: AppAndAuthVo.java
 * Author:   谭荣巧
 * Date:     2018/1/17 14:21
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统和角色组合树<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/1/17 14:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppAndRoleVo {
    private String code;
    private String name;
    private String nameCn;
    private String parentCode;
    private String app;

    public AppAndRoleVo(Object code, Object name, Object nameCn, Object parentCode, Object app) {
        this.code = code == null ? null : code.toString();
        this.name = name == null ? null : name.toString();
        this.nameCn = nameCn == null ? null : nameCn.toString();
        this.parentCode = parentCode == null ? null : parentCode.toString();
        this.app = app == null ? null : app.toString();

    }
}
