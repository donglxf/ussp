/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: RoleAndResVo.java
 * Author:   谭荣巧
 * Date:     2018/1/30 15:06
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色资源数据对象<br>
 * 保存角色与资源的关联关系时用到<br>
 *
 * @author 谭荣巧
 * @Date 2018/1/30 15:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleAndResVo {
    private String app;
    private String roleCode;
    private String[] resCodes;
}
