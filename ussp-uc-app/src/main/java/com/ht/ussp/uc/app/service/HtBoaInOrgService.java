/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: HtBoaInOrgService.java
 * Author:   谭荣巧
 * Date:     2018/1/13 10:50
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.service;

import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.repository.HtBoaInOrgRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 组织机构业务逻辑类<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/1/13 10:50
 */
@Log4j2
@Service
public class HtBoaInOrgService {
    @Autowired
    private HtBoaInOrgRepository htBoaInOrgRepository;

    /**
     * 根据父组织机构代码查询组织机构，并转化成Tree<br>
     *
     * @param parentOrgCode
     * @return Listst<Tree>
     * @author 谭荣巧
     * @Date 2018/1/13 10:52
     */
    public List<HtBoaInOrg> getOrgTreeList(String parentOrgCode) {
        HtBoaInOrg queryOrg = new HtBoaInOrg();
        queryOrg.setParentOrgCode(parentOrgCode);
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                // 忽略 id 和 createTime 字段。
                .withIgnorePaths("id", "createdDatetime", "orgPath", "jpaVersion", "sequence")
                // 忽略为空字段。
                .withIgnoreNullValues();
        //创建实例
        Example<HtBoaInOrg> ex = Example.of(queryOrg, matcher);
        return htBoaInOrgRepository.findAll(ex, new Sort(Sort.Direction.ASC, "parentOrgCode", "sequence"));
    }
}
