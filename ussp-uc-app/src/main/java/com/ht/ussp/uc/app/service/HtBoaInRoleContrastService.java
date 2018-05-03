/*
 * FileName: HtBoaInContrastService.java
 * Author:   谭荣巧
 * Date:     2018/3/4 20:31
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInRoleContrast;
import com.ht.ussp.uc.app.repository.HtBoaInRoleContrastRepository;

/**
 * 信贷用户
 * @author tangxs
 *
 */
@Service
public class HtBoaInRoleContrastService {
    @Autowired
    private HtBoaInRoleContrastRepository htBoaInRoleContrastRepository;

    public List<HtBoaInRoleContrast> geHtBoaInRoleContrastList() {
        return htBoaInRoleContrastRepository.findAll();
    }
    
}
