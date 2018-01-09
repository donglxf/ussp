package com.ht.ussp.gateway.app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;



/**
 * 
* @ClassName: DatabaseUserService
* @Description: TODO
* @author wim qiuwenwu@hongte.info
* @date 2018年1月6日 上午11:41:29
 */
//@Service
public class DatabaseUserService implements UserService {
    private final UserRepository userRepository;
    
    @Autowired
    public DatabaseUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public UserRepository getUserRepository() {
        return userRepository;
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }
}
