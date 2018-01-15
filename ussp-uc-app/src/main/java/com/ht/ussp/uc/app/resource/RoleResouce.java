package com.ht.ussp.uc.app.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;

/**
 * 
 * @ClassName: RoleResouce
 * @Description: 与资源相关
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月13日 下午6:22:39
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/res")
@Log4j2
public class RoleResouce {

	@Autowired
	protected RedisTemplate<String, String> redis;
}
