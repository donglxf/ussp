package com.ht.ussp.uc.app.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ht.ussp.uc.app.domain.HtBoaOutUser;
import com.ht.ussp.uc.app.model.SelfBoaOutUserInfo;

/**
 * 
 * @ClassName: HtBoaOutUserRepository
 * @Description: 用户表持久层
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月9日 上午10:16:45
 */
public interface HtBoaOutUserRepository
        extends JpaRepository<HtBoaOutUser, Long> {

    public HtBoaOutUser findById(Long id);
    
    @Query("SELECT new com.ht.ussp.uc.app.model.SelfBoaOutUserInfo (u.userId, u.userName, u.email, u.idNo, u.mobile) FROM HtBoaOutUser u WHERE u.userId = ?1")
    public List<SelfBoaOutUserInfo> listSelfUserInfo(String userId);
    
    @Query("SELECT new map(r.roleCode, r.roleName, r.roleNameCn) FROM HtBoaOutUser u, HtBoaOutRole r, HtBoaOutUserRole ur WHERE u.userId = ?1 AND u.userId = ur.userId AND ur.roleCode = r.roleCode")
    public List<Map<String, Object>> listSelfUserInfo4Role(String userId);

}
