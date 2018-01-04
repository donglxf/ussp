package com.ht.ussp.uc.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
* @ClassName: RegistryApplication
* @Description: 用户中心
* @author wim qiuwenwu@hongte.info
* @date 2018年1月4日 下午9:07:10
 */
@SpringBootApplication 
public class UcApplication {
    private static final Logger log = LoggerFactory.getLogger(UcApplication.class);
	public static void  main(String[] args) {
		SpringApplication.run(UcApplication.class, args);
        log.info("用户中心启动成功！");

	}
}
