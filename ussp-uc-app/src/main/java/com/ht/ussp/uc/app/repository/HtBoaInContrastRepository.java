/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: HtBoaInContrastRepository.java
 * Author:   谭荣巧
 * Date:     2018/3/4 20:29
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.repository;

import com.ht.ussp.uc.app.domain.HtBoaInContrast;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 对照表持久层<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/3/4 20:29
 */
public interface HtBoaInContrastRepository extends JpaRepository<HtBoaInContrast, Long> {

	HtBoaInContrast findByUcBusinessIdAndType(String userId, String string);
}
