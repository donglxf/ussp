package com.ht.ussp.ouc.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: RegistryApplication
 * @Description: 用户中心
 * @date 2018年1月4日 下午9:07:10
 */
@EnableEurekaClient
@SpringBootApplication
@ServletComponentScan 
public class OUcApplication {
    private static final Logger log = LoggerFactory.getLogger(OUcApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(OUcApplication.class, args);
        log.debug("用户中心启动成功！");

    }
}
