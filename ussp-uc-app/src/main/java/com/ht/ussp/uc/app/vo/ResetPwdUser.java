/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: RelevanceApiVo.java
 * Author:   谭荣巧
 * Date:     2018/1/20 15:23
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.vo;

import com.ht.ussp.uc.app.domain.HtBoaInResource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 关联API资源时需要的自定义数据对象<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/1/20 15:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPwdUser {
	List<UserMessageVo> resetPwdUserdata;   
}
