package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.model.BoaInOrgInfo;
import com.ht.ussp.uc.app.model.PageConf;
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
    
    public Object findAllByPage(PageConf pageConf,Map<String, String> query) {
        Sort sort = null;
        Pageable pageable = null;
        List<Order> orders = new ArrayList<Order>();
        if (null != pageConf.getSortNames()) {
            for (int i = 0; i < pageConf.getSortNames().size(); i++) {
                orders.add(new Order(pageConf.getSortOrders().get(i), pageConf.getSortNames().get(i)));
            }
            sort = new Sort(orders);
        }
        if (null != pageConf.getPage() && null != pageConf.getSize())
            pageable = new PageRequest(pageConf.getPage(), pageConf.getSize(),  sort);
        String search = "";
        String orgPath = "";
        if (query != null && query.size() > 0 && query.get("orgCode") != null) {
        	orgPath = "%" +query.get("orgCode")+ "%";
        }
        if (query != null && query.size() > 0 && query.get("keyWord") != null) {
        	search = "%" +query.get("keyWord")+ "%";
        }
        
        if (null == search || 0 == search.trim().length())
            search = "%%";
    
        
        if (null != pageable) {
            Page<BoaInOrgInfo> p = this.htBoaInOrgRepository.lisOrgByPageWeb(pageable, search,orgPath);
            return p;
        } 
            return null;
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

	public List<HtBoaInOrg> findByOrgCode(String orgCode) {
		return this.htBoaInOrgRepository.findByOrgCode(orgCode);
	}

}
