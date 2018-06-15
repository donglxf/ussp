package com.ht.ussp.ouc.app.service;

import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.ouc.app.domain.HtBoaOutUser;
import com.ht.ussp.ouc.app.repository.HtBoaOutUserRepository;

@Service
public class HtBoaOutUserService {
	@Autowired
	private HtBoaOutUserRepository htBoaOutUserRepository;

	public HtBoaOutUser saveUser(HtBoaOutUser htBoaOutUser) {
		return htBoaOutUserRepository.save(htBoaOutUser);
	}

	public HtBoaOutUser findByEmailOrMobile(String userName, String userName2) {
		return htBoaOutUserRepository.findByEmailOrMobile(userName,userName2);
	}

	public HtBoaOutUser findByEmail(String userName) {
		return htBoaOutUserRepository.findByEmail(userName);
	}
	
	public HtBoaOutUser findByMobile(String userName) {
		return htBoaOutUserRepository.findByMobile(userName);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PageResult<List<HtBoaOutUser>> getUserListPage(PageRequest pageRequest, Map<String, String> query) {
		PageResult result = new PageResult();
		String keyWord = "";
		Specification<HtBoaOutUser> specification = (root, query1, cb) -> {
			Predicate p1 = cb.like(root.get("userName").as(String.class), "%" + keyWord + "%");
			Predicate p2 = cb.like(root.get("email").as(String.class), "%" + keyWord + "%");
			Predicate p3 = cb.like(root.get("mobile").as(String.class), "%" + keyWord + "%");
			// 把Predicate应用到CriteriaQuery中去,因为还可以给CriteriaQuery添加其他的功能，比如排序、分组啥的
			query1.where(cb.or(p1, p2, p3));
			return query1.getRestriction();
		};
		Page<HtBoaOutUser> pageData = htBoaOutUserRepository.findAll(specification, pageRequest);
		if (pageData != null) {
			result.count(pageData.getTotalElements()).data(pageData.getContent());
		}
		result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
		return result;
	}

	public HtBoaOutUser findByUserId(String userId) {
		return htBoaOutUserRepository.findByUserId(userId);
	}

	public void delete(HtBoaOutUser user) {
		htBoaOutUserRepository.delete(user);
	}
}
