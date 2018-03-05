package com.ht.ussp.uaa.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * 
 * @ClassName: UaaApplication
 * @Description: 用户鉴权中心
 * @author wim qiuwenwu@hongte.info
 * @date 2018年3月2日 下午2:21:35
 */

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class UaaApplication 
{
    public static void main( String[] args )
    {
        SpringApplication.run(UaaApplication.class, args);
    }
}
