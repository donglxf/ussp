package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.repository.HtBoaInOrgRepository;

@Service
public class HtBoaInOrgService {

    @Autowired
    private HtBoaInOrgRepository htBoaInOrgRepository;

    public HtBoaInOrg findById(Long id) {
        return this.htBoaInOrgRepository.findById(id);
    }

    public List<HtBoaInOrg> findAll(HtBoaInOrg u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaInOrg> ex = Example.of(u, matcher);
        return this.htBoaInOrgRepository.findAll(ex);
    }

    public HtBoaInOrg add(HtBoaInOrg u) {
        return this.htBoaInOrgRepository.saveAndFlush(u);
    }

    public HtBoaInOrg update(HtBoaInOrg u) {
        return this.htBoaInOrgRepository.save(u);
    }
    
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
