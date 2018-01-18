package com.ht.ussp.uc.app.service;

import java.util.List;

import com.ht.ussp.uc.app.domain.HtBoaInResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.repository.HtBoaInResourceRepository;
import com.ht.ussp.uc.app.vo.ResVo;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInResourceService
 * @Description: 查找资源服务
 * @date 2018年1月15日 下午2:28:11
 */
@Service
public class HtBoaInResourceService {
    @Autowired
    private HtBoaInResourceRepository htBoaInResourceRepository;

    public List<ResVo> queryResForY(List<String> res_type, String app) {

        return htBoaInResourceRepository.queryResForY(res_type, app);
    }

    public List<ResVo> queryResForN(List<String> res_code, List<String> res_type, String app) {

        return htBoaInResourceRepository.queryResForN(res_code, res_type, app);
    }

    public List<HtBoaInResource> save(List<HtBoaInResource> htBoaInResources) {
        return htBoaInResourceRepository.save(htBoaInResources);
    }

    public HtBoaInResource save(HtBoaInResource htBoaInResource) {
        return htBoaInResourceRepository.save(htBoaInResource);
    }

    public HtBoaInResource getByRemark(String remark) {
        return htBoaInResourceRepository.findByRemark(remark);
    }

    public List<HtBoaInResource> getByApp(String app) {
        return htBoaInResourceRepository.findByApp(app);
    }
}
