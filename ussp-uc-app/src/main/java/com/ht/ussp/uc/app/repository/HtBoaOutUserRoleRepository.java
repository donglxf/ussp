package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ht.ussp.uc.app.domain.HtBoaOutUserRole;

/**
 * 
 * @ClassName: HtBoaOutUserRoleRepository
 * @Description: 用户角色关联表持久层
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月11日 上午9:19:32
 */
public interface HtBoaOutUserRoleRepository
        extends JpaRepository<HtBoaOutUserRole, Long> {

    public HtBoaOutUserRole findById(Long id);

}