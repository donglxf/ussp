package com.ht.ussp.uaa.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 
 * @ClassName: UaaApplication
 * @Description: 用户鉴权中心
 * @author wim qiuwenwu@hongte.info
 * @date 2018年3月2日 下午2:21:35
 */

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.ht.ussp.client","com.ht.ussp.uaa.app.feignclient"})
@ComponentScan(basePackages = {"com.ht.ussp.bean", "com.ht.ussp.uaa.app","com.ht.ussp.uaa.app.security.ajax"})
public class UaaApplication 
{
    public static void main( String[] args )
    {
        SpringApplication.run(UaaApplication.class, args);
    }
}
