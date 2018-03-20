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

import com.ht.ussp.uc.app.domain.HtBoaInContrast;
import com.ht.ussp.uc.app.repository.HtBoaInContrastRepository;

/**
 * 对账表的业务逻辑层<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/3/4 20:31
 */
@Service
public class HtBoaInContrastService {
    @Autowired
    private HtBoaInContrastRepository htBoaInContrastRepository;

    public List<HtBoaInContrast> add(List<HtBoaInContrast> htBoaInContrastList) {
        return htBoaInContrastRepository.save(htBoaInContrastList);
    }
    
    public List<HtBoaInContrast> getHtBoaInContrastList() {
        return htBoaInContrastRepository.findAll();
    }
    
    
    public List<HtBoaInContrast> getHtBoaInContrastListByBmUserId(String bmBusinessId) {
        return htBoaInContrastRepository.findByBmBusinessIdAndType(bmBusinessId, "20");
    }
}
