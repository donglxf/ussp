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

import com.ht.ussp.uc.app.domain.HtBoaInBmUser;
import com.ht.ussp.uc.app.repository.HtBoaInBmUserRepository;

/**
 * 对账表的业务逻辑层<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/3/4 20:31
 */
@Service
public class HtBoaInBmUserService {
    @Autowired
    private HtBoaInBmUserRepository htBoaInBmUserRepository;

    public List<HtBoaInBmUser> getHtBoaInBmUserList() {
        return htBoaInBmUserRepository.findAll();
    }
    
}
