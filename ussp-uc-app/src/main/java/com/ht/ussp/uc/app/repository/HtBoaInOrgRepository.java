package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.uc.app.domain.HtBoaInOrg;

/**
 * 
 * @ClassName: HtBoaInOrgRepository
 * @Description: 组织机构表持久层
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月11日 上午9:19:32
 */
public interface HtBoaInOrgRepository
        extends JpaRepository<HtBoaInOrg, Long> {

    public HtBoaInOrg findById(Long id);
    
    /**
     * 根据父组织机构代码查询组织机构信息<br>
     *
     * @param parentOrgCode 父组织机构代码
     * @return 组织机构信息集合
     * @author 谭荣巧
     * @Date 2018/1/13 10:49
     */
    List<HtBoaInOrg> findByParentOrgCode(String parentOrgCode, Sort sort);

}
