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
 * 系统和权限资源组合树<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/1/17 14:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppAndAuthVo {
    private String code;
    private String name;
    private String nameCn;
    private String sequence;
    private String type;
    private String parentCode;
    private String icon;

    public AppAndAuthVo(Object code, Object name, Object nameCn, Object sequence, Object type, Object parentCode, Object icon) {
        this.code = code == null ? null : code.toString();
        this.name = name == null ? null : name.toString();
        this.nameCn = nameCn == null ? null : nameCn.toString();
        this.sequence = sequence == null ? null : sequence.toString();
        this.type = type == null ? null : type.toString();
        this.parentCode = parentCode == null ? null : parentCode.toString();
        this.icon = icon == null ? null : icon.toString();
    }
}
