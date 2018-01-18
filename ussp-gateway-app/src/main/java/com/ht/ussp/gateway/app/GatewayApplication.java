package com.ht.ussp.gateway.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import com.ht.ussp.gateway.app.filter.AccessFilter;

/**
 * 
* @ClassName: GatewayApplication
* @Description: 网关
* @author wim qiuwenwu@hongte.info
* @date 2018年1月3日 下午6:14:42
 */
@EnableZuulProxy
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class GatewayApplication {
    private static final Logger log = LoggerFactory.getLogger(GatewayApplication.class);
	public static void  main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
        log.info("网关启动成功！");

	}
	
	@Bean
    public AccessFilter accessFilter(){
    	return new AccessFilter();
    }
}
