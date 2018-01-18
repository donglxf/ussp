package com.ht.ussp.uc.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: RegistryApplication
 * @Description: 用户中心
 * @date 2018年1月4日 下午9:07:10
 */
@EnableFeignClients(basePackages = {"com.ht.ussp.client"})
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.ht.ussp.init", "com.ht.ussp.uc.app"})
public class UcApplication {
    private static final Logger log = LoggerFactory.getLogger(UcApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(UcApplication.class, args);
        log.info("用户中心启动成功！");

    }
}
