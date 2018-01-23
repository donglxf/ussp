package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInApp;
import com.ht.ussp.uc.app.model.BoaInAppInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInAppRepository;
import com.ht.ussp.uc.app.vo.AppAndResourceVo;

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
    public List<AppAndResourceVo> loadAppAndResourceVoList() {
        List<Object[]> list = htBoaInAppRepository.queryAppAndAuthTree();
        List<AppAndResourceVo> aaaList = new ArrayList<>();
        AppAndResourceVo aaa;
        for (Object[] objects : list) {
            aaa = new AppAndResourceVo(objects[0], objects[1], objects[2], objects[3], objects[4], objects[5], objects[6], objects[7]);
            aaaList.add(aaa);
        }
        return aaaList;
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
            pageable = new PageRequest(pageConf.getPage(), pageConf.getSize(), sort);
        
        String orgPath = "";
        if (query != null && query.size() > 0 && query.get("orgCode") != null) {
        	orgPath = "%" +query.get("orgCode")+ "%";
        }
        
        String search = pageConf.getSearch();
        if (null == search || 0 == search.trim().length())
            search = "%%";
        else
            search = "%" + search + "%";
        if (null != pageable) {
            Page<BoaInAppInfo> p = this.htBoaInAppRepository.listAllAppByPage(pageable, search );
            return p;
        } 
        return null;
    }
    
    public HtBoaInApp add(HtBoaInApp u) {
        return this.htBoaInAppRepository.saveAndFlush(u);
    }

    public HtBoaInApp update(HtBoaInApp u) {
        return this.htBoaInAppRepository.save(u);
    }
    
    public void delete(long id) {
        this.htBoaInAppRepository.delete(id);
    }
}
