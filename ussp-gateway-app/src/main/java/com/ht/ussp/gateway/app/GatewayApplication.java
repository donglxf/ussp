package com.ht.ussp.gateway.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
* @ClassName: GatewayApplication
* @Description: 网关
* @author wim qiuwenwu@hongte.info
* @date 2018年1月3日 下午6:14:42
 */
@SpringBootApplication
public class GatewayApplication {
    private static final Logger log = LoggerFactory.getLogger(GatewayApplication.class);
	public static void  main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
        log.info("网关启动成功！");

	}
}
