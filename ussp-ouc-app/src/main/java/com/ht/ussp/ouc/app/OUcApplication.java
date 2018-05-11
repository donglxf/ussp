package com.ht.ussp.ouc.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 
 * @ClassName: OUcApplication
 * @Description: 外部用户中心
 * @author wim qiuwenwu@hongte.info
 * @date 2018年5月11日 下午5:56:47
 */
@EnableEurekaClient
@SpringBootApplication
@ServletComponentScan
@EnableFeignClients(basePackages = {"com.ht.ussp.client","com.ht.ussp.ouc.app.feignclients"})
@ComponentScan(basePackages = {"com.ht.ussp.bean", "com.ht.ussp.ouc.app"})
public class OUcApplication {
    private static final Logger log = LoggerFactory.getLogger(OUcApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(OUcApplication.class, args);
        log.debug("外部用户中心启动成功！");

    }
}
