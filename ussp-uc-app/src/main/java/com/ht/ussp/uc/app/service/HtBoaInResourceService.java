package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInResource;
import com.ht.ussp.uc.app.domain.HtBoaInUserApp;
import com.ht.ussp.uc.app.repository.HtBoaInRoleRepository;
import com.ht.ussp.uc.app.repository.HtBoaInUserAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.repository.HtBoaInResourceRepository;
import com.ht.ussp.uc.app.vo.ResVo;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
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
    @Autowired
    private HtBoaInUserAppRepository htBoaInUserAppRepository;
    @Autowired
    private HtBoaInRoleRepository htBoaInRoleRepository;

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

    public HtBoaInResource getOne(long id) {
        return htBoaInResourceRepository.findOne(id);
    }

    public void delete(long id) {
        htBoaInResourceRepository.delete(id);
    }

    public List<HtBoaInResource> getByResParent(String resParent) {
        return htBoaInResourceRepository.findByResParent(resParent);
    }

    /**
     * 资源管理分页查询<br>
     *
     * @param pageable   分页对象
     * @param app        系统编码
     * @param parentCode 父资源编码
     * @param resType    资源类型
     * @param keyWord    关键字
     * @return 分页结果对象
     * @author 谭荣巧
     * @Date 2018/1/22 21:10
     */
    public PageResult getPage(Pageable pageable, String app, String parentCode, String resType, String keyWord) {
        PageResult result = new PageResult();
        Page<HtBoaInResource> pageData = null;
        if (!StringUtils.isEmpty(app) && !StringUtils.isEmpty(resType)) {
            Specification<HtBoaInResource> specification = (root, query1, cb) -> {
                Predicate where;
                Predicate p5 = cb.equal(root.get("app").as(String.class), app);
                Predicate p6 = null;
                if (StringUtils.isEmpty(parentCode)) {
                    if (!"api".equals(resType)) {
                        p6 = cb.isNull(root.get("resParent").as(String.class));
                    }
                } else {
                    Predicate p61 = cb.equal(root.get("resParent").as(String.class), parentCode);
                    Predicate p62 = cb.equal(root.get("resCode").as(String.class), parentCode);
                    p6 = cb.or(p61, p62);
                }
                String[] resTypes = resType.split(",");
                CriteriaBuilder.In p7 = cb.in(root.get("resType").as(String.class));
                for (int i = 0; i < resTypes.length; i++) {
                    p7.value(resTypes[i]);
                }
                if (!StringUtils.isEmpty(keyWord)) {
                    Predicate p1 = cb.like(root.get("resNameCn").as(String.class), "%" + keyWord + "%");
                    Predicate p2 = cb.like(root.get("resContent").as(String.class), "%" + keyWord + "%");
                    Predicate p3 = cb.like(root.get("resCode").as(String.class), "%" + keyWord + "%");
                    Predicate p4 = cb.like(root.get("remark").as(String.class), "%" + keyWord + "%");
                    Predicate kewPredicate = cb.or(p1, p2, p3, p4);
                    if (p6 != null) {
                        where = cb.and(p5, p6, p7, kewPredicate);
                    } else {
                        where = cb.and(p5, p7, kewPredicate);
                    }
                } else {
                    if (p6 != null) {
                        where = cb.and(p5, p6, p7);
                    } else {
                        where = cb.and(p5, p7);
                    }
                }
                query1.where(where);
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

    /**
     * 加载权限资源<br>
     *
     * @param userId   用户编码
     * @param app      系统编码
     * @param resTypes 资源类型（多个）（module/view/group/btn/tab/api）
     * @return 资源集合
     * @author 谭荣巧
     * @Date 2018/1/22 21:11
     */
    public List<ResVo> loadByUserIdAndApp(String userId, String app, String[] resTypes) {
        List<ResVo> resVoList = null;
        HtBoaInUserApp userApp = htBoaInUserAppRepository.findByUserIdAndApp(userId, app);
        //判断是否有系统访问权限
        if (userApp != null) {
            //判断是否是管理员
            if ("Y".equals(userApp.getController())) {//是管理员，则获取该系统的所有资源
                resVoList = htBoaInResourceRepository.queryResForY(Arrays.asList(resTypes), app);
            } else {//不是管理员，则通过岗位、角色获取资源
                //整合所有角色
                List<String> roleList = htBoaInRoleRepository.findRoleCodeByUserId(userId);
                //通过角色查询资源
                resVoList = htBoaInResourceRepository.queryResForN(roleList, Arrays.asList(resTypes), app);
            }
        }
        return resVoList;
    }

    /**
     * 加载系统所有资源<br>
     *
     * @param app 系统编码
     * @return 资源列表
     * @author 谭荣巧
     * @Date 2018/1/29 21:37
     */
    public List<HtBoaInResource> getByApp(String app) {
        return htBoaInResourceRepository.findByAppAndStatusAndDelFlag(app, "0", 0);
    }

    /**
     * 通过父编码获取资源个数，用于构造资源编号<br>
     *
     * @param app        系统编号
     * @param resPanrent 父资源编码
     * @param resTypes   资源类型
     * @return
     * @author 谭荣巧
     * @Date 2018/1/30 20:02
     */
    public int getItemCountByResParentAndResType(String app, String resPanrent, String... resTypes) {
        List<HtBoaInResource> list = htBoaInResourceRepository.findByAppAndResParentAndResTypeIn(app, resPanrent, Arrays.asList(resTypes));
        return list == null ? 0 : list.size();
    }
}
