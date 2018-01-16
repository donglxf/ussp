package com.ht.ussp.uc.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht.ussp.uc.app.repository.HtBoaInResourceRepository;
import com.ht.ussp.uc.app.vo.ResVo;

/**
 * 
 * @ClassName: HtBoaInResourceService
 * @Description: 查找资源服务
 * @author wim qiuwenwu@hongte.info
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
}
