/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: HtBoaInOrgRepository.java
 * Author:   谭荣巧
 * Date:     2018/1/13 10:35
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.repository;


import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 组织机构持久层操作类<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/1/13 10:35
 */
public interface HtBoaInOrgRepository extends JpaSpecificationExecutor<HtBoaInOrg>, JpaRepository<HtBoaInOrg, Long> {

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
