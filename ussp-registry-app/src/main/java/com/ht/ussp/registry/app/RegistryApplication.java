/*
 * 文件名 Test.java
 * 版权 Copyright 2017 团贷网
 * 创建人 谭荣巧
 * 创建时间 2017年11月17日 下午2:53:33
 */
package com.ht.ussp.registry.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 服务注册中心（生产环境需要高可用）<br>
 * 
 * @author 谭荣巧
 * @date 2017年11月17日 下午2:53:33
 */
@EnableEurekaServer
@SpringBootApplication 
public class RegistryApplication {
    private static final Logger LOG = LoggerFactory.getLogger(RegistryApplication.class);
	public static void  main(String[] args) {
		SpringApplication.run(RegistryApplication.class, args);
        LOG.info("RegistryApplication-> RegistryApplication start  Success !!!");

	}
}
