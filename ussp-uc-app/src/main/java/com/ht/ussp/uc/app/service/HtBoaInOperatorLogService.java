package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInOperatorLog;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInOperatorLogRepository;
import com.ht.ussp.util.BeanUtils;

@Service
public class HtBoaInOperatorLogService {

    @Autowired
    private HtBoaInOperatorLogRepository htBoaInOperatorLogRepository;

    public Object findAllByPage(PageConf pageConf) {
        Sort sort = null;
        Pageable pageable = null;
        List<Order> orders = new ArrayList<Order>();
        if (null != pageConf.getSortNames()) {
            for (int i = 0; i < pageConf.getSortNames().size(); i++) {
                orders.add(new Order(pageConf.getSortOrders().get(i),
                        pageConf.getSortNames().get(i)));
            }
            sort = new Sort(orders);
        }
        Specification<HtBoaInOperatorLog> querySpecifi = null;
        if (null != pageConf.getSearch()
                && 0 < pageConf.getSearch().trim().length()) {
            String search = pageConf.getSearch().trim();
            querySpecifi = new Specification<HtBoaInOperatorLog>() {
                @Override
                public Predicate toPredicate(Root<HtBoaInOperatorLog> root,
                        CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    // 模糊查找
                    predicates.add(cb.like(root.get("userId").as(String.class),
                            "%" + search + "%"));
                    predicates
                            .add(cb.like(root.get("userName").as(String.class),
                                    "%" + search + "%"));
                    predicates.add(
                            cb.like(root.get("actionName").as(String.class),
                                    "%" + search + "%"));
                    predicates.add(
                            cb.like(root.get("deviceType").as(String.class),
                                    "%" + search + "%"));
                    predicates.add(cb.like(root.get("result").as(String.class),
                            "%" + search + "%"));
                    predicates
                            .add(cb.like(root.get("clientIp").as(String.class),
                                    "%" + search + "%"));
                    predicates
                            .add(cb.like(root.get("deviceNo").as(String.class),
                                    "%" + search + "%"));
                    predicates.add(cb.like(root.get("app").as(String.class),
                            "%" + search + "%"));
                    predicates.add(cb.like(root.get("type").as(String.class),
                            "%" + search + "%"));
                    predicates.add(cb.like(root.get("orgCode").as(String.class),
                            "%" + search + "%"));
                    predicates
                            .add(cb.like(root.get("jobNumber").as(String.class),
                                    "%" + search + "%"));
                    String dt = BeanUtils.matchDateString(search);
                    if (dt.equals(search))
                        predicates.add(
                                cb.like(root.get("actionTime").as(String.class),
                                        "%" + search + "%"));
                    // and到一起的话所有条件就是且关系，or就是或关系
                    return cb.or(predicates
                            .toArray(new Predicate[predicates.size()]));
                }
            };
        }
        if (null != pageConf.getPage() && null != pageConf.getSize()
                && null != sort)
            pageable = new PageRequest(pageConf.getPage(), pageConf.getSize(),
                    sort);
        else if (null != pageConf.getPage() && null != pageConf.getSize())
            pageable = new PageRequest(pageConf.getPage(), pageConf.getSize());
        if (null != pageable && null != querySpecifi)
            return this.htBoaInOperatorLogRepository.findAll(querySpecifi,
                    pageable);
        else if (null != pageable)
            return this.htBoaInOperatorLogRepository.findAll(pageable);
        else if (null != querySpecifi && null != sort)
            return this.htBoaInOperatorLogRepository.findAll(querySpecifi,
                    sort);
        else if (null != sort)
            return this.htBoaInOperatorLogRepository.findAll(sort);
        else
            return this.htBoaInOperatorLogRepository.findAll();
    }

    public HtBoaInOperatorLog add(HtBoaInOperatorLog u) {
        return this.htBoaInOperatorLogRepository.saveAndFlush(u);
    }

}
