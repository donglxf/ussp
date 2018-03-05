package com.ht.ussp.uc.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: RegistryApplication
 * @Description: 用户中心
 * @date 2018年1月4日 下午9:07:10
 */
@EnableFeignClients(basePackages = {"com.ht.ussp.client","com.ht.ussp.uc.app.feignclients"})
@EnableEurekaClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.ht.ussp.bean", "com.ht.ussp.uc.app"})
public class UcApplication {
    private static final Logger log = LoggerFactory.getLogger(UcApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(UcApplication.class, args);
        log.debug("用户中心启动成功！");

    }
}
