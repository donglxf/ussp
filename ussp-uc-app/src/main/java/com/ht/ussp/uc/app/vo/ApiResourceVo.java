/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: ApiResourceVo.java
 * Author:   谭荣巧
 * Date:     2018/2/8 14:38
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API资源信息自定义数据对象<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/2/8 14:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResourceVo {
    private long id;
    private String resNameCn;
    private String resContent;
    private String remark;
    private String resParent;
    private String resCode;
}
