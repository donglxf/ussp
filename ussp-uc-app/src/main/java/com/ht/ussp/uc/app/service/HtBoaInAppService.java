package com.ht.ussp.uc.app.service;

import com.ht.ussp.uc.app.vo.AppAndAuthVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInApp;
import com.ht.ussp.uc.app.repository.HtBoaInAppRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class HtBoaInAppService {
    @Autowired
    private HtBoaInAppRepository htBoaInAppRepository;

    public HtBoaInApp findById(Long id) {
        return htBoaInAppRepository.findById(id);
    }

    /**
     * 加载系统与权限资源的组合树数据<br>
     *
     * @author 谭荣巧
     * @Date 2018/1/17 15:30
     */
    public List<AppAndAuthVo> loadAppAndAuthVo() {
        List<Object[]> list = htBoaInAppRepository.queryAppAndAuthTree();
        List<AppAndAuthVo> aaaList = new ArrayList<>();
        AppAndAuthVo aaa;
        for (Object[] objects : list) {
            aaa = new AppAndAuthVo(objects[0], objects[1], objects[2], objects[3], objects[4], objects[5], objects[6]);
            aaaList.add(aaa);
        }
        return aaaList;
    }
}
