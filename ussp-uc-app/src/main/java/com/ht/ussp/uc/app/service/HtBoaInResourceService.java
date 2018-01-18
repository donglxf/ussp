package com.ht.ussp.uc.app.service;

import java.util.List;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.repository.HtBoaInResourceRepository;
import com.ht.ussp.uc.app.vo.ResVo;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;

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

    public List<HtBoaInResource> getByApp(String app) {
        return htBoaInResourceRepository.findByApp(app);
    }

    public PageResult getPage(Pageable pageable, String app, String parentCode, String keyWord) {
        PageResult result = new PageResult();
        Page<HtBoaInResource> pageData = null;
        if (!StringUtils.isEmpty(keyWord)) {
            Specification<HtBoaInResource> specification = (root, query1, cb) -> {
                Predicate p1 = cb.like(root.get("jobNumber").as(String.class), "%" + keyWord + "%");
                Predicate p2 = cb.like(root.get("userName").as(String.class), "%" + keyWord + "%");
                Predicate p3 = cb.like(root.get("mobile").as(String.class), "%" + keyWord + "%");
                Predicate p4 = cb.equal(root.get("app").as(String.class), app);
                query1.where(cb.and(cb.or(p1, p2, p3), p4));
                return query1.getRestriction();
            };
            pageData = htBoaInResourceRepository.findAll(specification, pageable);
        }
        if (pageData != null) {
            result.count(pageData.getTotalElements()).data(pageData.getContent());
        }
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
        return result;
    }
}
