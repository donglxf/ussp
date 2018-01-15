package com.ht.ussp.uc.app.service;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.repository.HtBoaInLoginRepository;
import com.ht.ussp.util.DtoUtil;
import com.ht.ussp.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.repository.HtBoaInUserRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Map;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInUserService
 * @Description: 用户信息服务层
 * @date 2018年1月8日 下午9:47:25
 */
@Service
public class HtBoaInUserService {
    @Autowired
    private HtBoaInUserRepository htBoaInUserRepository;
    @Autowired
    private HtBoaInLoginRepository htBoaInLoginRepository;
    @Autowired
    private EntityManager entityManager;

    public HtBoaInUser findByUserName(String userName) {

        return htBoaInUserRepository.findByUserName(userName);
    }

    /**
     * 用户信息分页查询<br>
     *
     * @param pageRequest 分页对象
     * @param keyWord     关键字查询
     * @param query       高级查询
     * @return 用户信息列表
     * @author 谭荣巧
     * @Date 2018/1/12 8:58
     */
    public PageResult<HtBoaInUser> getUserListPage(PageRequest pageRequest, String keyWord, Map<String, String> query) {
        PageResult result = new PageResult();
        Page<HtBoaInUser> pageData = null;
        if (query != null && query.size() > 0 && query.get("orgCode") != null) {
            if (!StringUtil.isEmpty(keyWord)) {
                Specification<HtBoaInUser> specification = (root, query1, cb) -> {
                    Predicate p1 = cb.like(root.get("jobNumber").as(String.class), "%" + keyWord + "%");
                    Predicate p2 = cb.like(root.get("userName").as(String.class), "%" + keyWord + "%");
                    Predicate p3 = cb.like(root.get("mobile").as(String.class), "%" + keyWord + "%");
                    Predicate p4 = cb.equal(root.get("orgCode").as(String.class), query.get("orgCode"));
                    //把Predicate应用到CriteriaQuery中去,因为还可以给CriteriaQuery添加其他的功能，比如排序、分组啥的
                    query1.where(cb.and(cb.or(p1, p2, p3), p4));
                    return query1.getRestriction();
                };
                pageData = htBoaInUserRepository.findAll(specification, pageRequest);
            } else {//高级查询
                //创建查询条件数据对象
                HtBoaInUser customer = DtoUtil.mapToEntity(query, new HtBoaInUser());
                //创建匹配器，即如何使用查询条件
                ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                        // 忽略 id 和 createTime 字段。
                        .withIgnorePaths("id", "createdDatetime", "orgPath", "jpaVersion")
                        // 忽略为空字段。
                        .withIgnoreNullValues();
                //创建实例
                Example<HtBoaInUser> ex = Example.of(customer, matcher);
                pageData = htBoaInUserRepository.findAll(ex, pageRequest);
            }
        }
        if (pageData != null) {
            result.count(pageData.getTotalElements()).data(pageData.getContent());
        }
        result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
        return result;
    }

    /**
     * 新增用户信息<br>
     *
     * @param user 用户信息
     * @return 新的用户信息
     * @author 谭荣巧
     * @Date 2018/1/13 16:49
     */
    @Transactional()
    public boolean saveUserInfoAndLoginInfo(HtBoaInUser user, HtBoaInLogin logininfo) {
        htBoaInUserRepository.save(user);
        htBoaInLoginRepository.save(logininfo);
        return true;
    }
}
