package com.ht.ussp.gateway.app.service;

import java.util.Optional;


/**
 * 
* @ClassName: UserRepository
* @Description: TODO
* @author wim qiuwenwu@hongte.info
* @date 2018年1月6日 上午11:52:48
 */
public interface UserRepository
//extends JpaRepository<User, Long>
{
//    @Query("select u from User u left join fetch u.roles r where u.username=:username")
    public Optional<User> findByUsername( String username);
}
