package com.ht.ussp.gateway.app.service;

import java.util.Optional;


/**
 * 
* @ClassName: UserService
* @Description: TODO
* @author wim qiuwenwu@hongte.info
* @date 2018年1月6日 上午11:52:57
 */
public interface UserService {
    public Optional<User> getByUsername(String username);
}
