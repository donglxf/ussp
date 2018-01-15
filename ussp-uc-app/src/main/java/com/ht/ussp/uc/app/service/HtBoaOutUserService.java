package com.ht.ussp.uc.app.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaOutUser;
import com.ht.ussp.uc.app.model.SelfBoaOutUserInfo;
import com.ht.ussp.uc.app.repository.HtBoaOutUserRepository;

@Service
public class HtBoaOutUserService {

    @Autowired
    private HtBoaOutUserRepository htBoaOutUserRepository;

    public HtBoaOutUser findById(Long id) {
        return this.htBoaOutUserRepository.findById(id);
    }

    public List<HtBoaOutUser> findAll(HtBoaOutUser u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaOutUser> ex = Example.of(u, matcher);
        return this.htBoaOutUserRepository.findAll(ex);
    }

    public List<SelfBoaOutUserInfo> findAll(SelfBoaOutUserInfo u) {
        List<SelfBoaOutUserInfo> list = this.htBoaOutUserRepository
                .listSelfUserInfo(u.getUserId());
        for (SelfBoaOutUserInfo s : list) {
            List<Map<String, Object>> roles = this.htBoaOutUserRepository
                    .listSelfUserInfo4Role(s.getUserId());
            for (Map<String, Object> o : roles) {
                s.getRoleCodes()
                        .add(null != o.get("0") ? o.get("0").toString() : null);
                s.getRoleNames()
                        .add(null != o.get("1") ? o.get("1").toString() : null);
                s.getRoleNameChs()
                        .add(null != o.get("2") ? o.get("2").toString() : null);
            }
        }
        return list;
    }

    public HtBoaOutUser add(HtBoaOutUser u) {
        return this.htBoaOutUserRepository.saveAndFlush(u);
    }

    public HtBoaOutUser update(HtBoaOutUser u) {
        return this.htBoaOutUserRepository.save(u);
    }

}
