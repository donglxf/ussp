package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esotericsoftware.minlog.Log;
import com.ht.ussp.common.SysStatus;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInCompany;
import com.ht.ussp.uc.app.dto.HtBoaInCompanyDTO;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInCompanyRepository;
import com.ht.ussp.uc.app.repository.HtBoaInUserBusinessRepository;
import com.ht.ussp.uc.app.repository.HtBoaInUserExtRepository;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;

/**
 * 
 * @ClassName: HtBoaInCompanyService
 * @Description: 公司信息服务层
 * @author wim qiuwenwu@hongte.info
 * @date 2018年7月19日 下午6:23:13
 */
@Service
public class HtBoaInCompanyService {

	@Autowired
	private HtBoaInCompanyRepository htBoaInCompanyRepository;

	@Autowired
	private HtBoaInUserExtRepository htBoaInUserExtRepository;
	
	@Autowired
	private HtBoaInUserBusinessRepository htBoaInUserBusinessRepository;

	public List<HtBoaInCompany> findAll(HtBoaInCompany u) {
		ExampleMatcher matcher = ExampleMatcher.matching();
		Example<HtBoaInCompany> ex = Example.of(u, matcher);
		return this.htBoaInCompanyRepository.findAll(ex);
	}

	public List<HtBoaInCompany> findAll() {
		return this.htBoaInCompanyRepository.findAll();
	}

	public List<HtBoaInCompany> add(List<HtBoaInCompany> orgList) {
		return this.htBoaInCompanyRepository.save(orgList);
	}

	public HtBoaInCompany save(HtBoaInCompany u) {
		return this.htBoaInCompanyRepository.save(u);
	}

	// 通过公司机构码获取公司详情
	public HtBoaInCompany findByCompanyCode(String companyCode) {
		return htBoaInCompanyRepository.findByCompanyCode(companyCode);
	}

	// 通过userId获取用户业务机构
	public String getBusinessOrgCode(String userId) {
		return htBoaInUserExtRepository.getBusinessOrgCode(userId);
	}
	
	//通过业务机构码获取分公司编码
	public String getBranchCode(String busiOrgCode) {
		return htBoaInUserBusinessRepository.getBranchCode(busiOrgCode);
	}
	
	//修改分公司信息
	@SuppressWarnings("rawtypes")
	@Transactional(rollbackFor = Exception.class)
	public Result updateCompanyInfo(HtBoaInCompanyDTO htBoaInCompanyDTO,String userId) {
		HtBoaInCompany htBoaInCompanyOld=htBoaInCompanyRepository.findByCompanyCode(htBoaInCompanyDTO.getCompanyCode());
		if(null==htBoaInCompanyOld) {
			//如果无记录，则添加一条记录
			HtBoaInCompany htBoaInCompany=new HtBoaInCompany();
			BeanUtil.copyProperties(htBoaInCompanyDTO,htBoaInCompany);
			htBoaInCompany.setCreateOperator(userId);
			htBoaInCompany.setDelFlag(false);
			htBoaInCompanyRepository.save(htBoaInCompany);
			return Result.buildSuccess();
		}
		//如果删除标志为空，将其标志为有效
		if(null==htBoaInCompanyOld.getDelFlag()) {
			htBoaInCompanyOld.setDelFlag(false);
		}
		Log.debug(htBoaInCompanyOld.getCompanyCode()+"的删除标志是："+htBoaInCompanyOld.getDelFlag());
		if(null!=htBoaInCompanyOld.getDelFlag()&&htBoaInCompanyOld.getDelFlag()==true) {
			return Result.buildFail(SysStatus.RECORD_HAS_DELETED);
			
		}
		htBoaInCompanyOld.setUpdateOperator(userId);
		BeanUtil.copyProperties(htBoaInCompanyDTO,htBoaInCompanyOld, new CopyOptions() {{
            setIgnoreNullValue(true);
        }});
		htBoaInCompanyRepository.save(htBoaInCompanyOld);
		return Result.buildSuccess();
	}
	
	public Page<HtBoaInCompany> findAllByPage(PageConf pageConf){
		 //规格定义
        Specification<HtBoaInCompany> specification = new Specification<HtBoaInCompany>() {
            /**
             * 构造断言
             * @param root 实体对象引用
             * @param query 规则查询对象
             * @param cb 规则构建对象
             * @return 断言
             */
            @Override
            public Predicate toPredicate(Root<HtBoaInCompany> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>(); //所有的断言
                if(StringUtils.isNotBlank(pageConf.getSearch())){ //添加断言
                    Predicate likeNickName = cb.like(root.get("nickName").as(String.class),pageConf.getSearch()+"%");
                    predicates.add(likeNickName);
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
        //分页信息
        Pageable pageable = new PageRequest(pageConf.getPage(),pageConf.getSize()); //页码：前端从1开始，jpa从0开始，做个转换
        Page<HtBoaInCompany>  paga = htBoaInCompanyRepository.findAll(specification, pageable);
		return paga;
		
	}
}
