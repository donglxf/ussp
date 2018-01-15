package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInPwdHist;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInPwdHistRepository;

@Service
public class HtBoaInPwdHistService {

    @Autowired
    private HtBoaInPwdHistRepository htBoaInPwdHistRepository;

    public HtBoaInPwdHist findById(Long id) {
        return this.htBoaInPwdHistRepository.findById(id);
    }

    public List<HtBoaInPwdHist> findAll(HtBoaInPwdHist u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaInPwdHist> ex = Example.of(u, matcher);
        return this.htBoaInPwdHistRepository.findAll(ex);
    }

    @SuppressWarnings("unchecked")
    public Object findAllByPage(PageConf pageConf, Object object) {
        if (null != object && object instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) object;
            Pageable pageable = null;
            Sort sort = null;
            if (null != pageConf.getSortNames()
                    && !pageConf.getSortNames().isEmpty()) {
                List<Order> orders = new ArrayList<Order>();
                for (int i = 0; i < pageConf.getSortNames().size(); i++) {
                    orders.add(new Order(pageConf.getSortOrders().get(i),
                            pageConf.getSortNames().get(i)));
                }
                sort = new Sort(orders);
            }
            if (-1 < pageConf.getPage() && 0 < pageConf.getSize()
                    && null != sort) {
                pageable = new PageRequest(pageConf.getPage(),
                        pageConf.getSize(), sort);
            } else if (-1 < pageConf.getPage() && 0 < pageConf.getSize()) {
                pageable = new PageRequest(pageConf.getPage(),
                        pageConf.getSize());
            }
            if (null != map.get("userId")) {
                String userId = map.get("userId").toString();
                HtBoaInPwdHist u = new HtBoaInPwdHist();
                u.setUserId(userId);
                ExampleMatcher matcher = ExampleMatcher.matching();
                Example<HtBoaInPwdHist> ex = Example.of(u, matcher);
                if (null != pageable)
                    return this.htBoaInPwdHistRepository.findAll(ex, pageable);
                else if (null != sort)
                    return this.htBoaInPwdHistRepository.findAll(ex, sort);
                else
                    return this.htBoaInPwdHistRepository.findAll(ex);
            }
        }
        return null;
    }

    public HtBoaInPwdHist add(HtBoaInPwdHist u) {
        return this.htBoaInPwdHistRepository.saveAndFlush(u);
    }

    public HtBoaInPwdHist update(HtBoaInPwdHist u) {
        return this.htBoaInPwdHistRepository.save(u);
    }

}
