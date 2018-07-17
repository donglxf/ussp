package com.ht.ussp.admin.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import de.codecentric.boot.admin.config.EnableAdminServer;

/**
 * 
 * @ClassName: AdminApplication
 * @Description: spring boot admin用户监控cloud中所有boot项目
 * @author wim qiuwenwu@hongte.info
 * @date 2018年7月13日 上午9:38:32
 */

@EnableAdminServer
@EnableDiscoveryClient
@SpringBootApplication
public class AdminApplication{
     public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
    
}
