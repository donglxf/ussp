package com.ht.ussp.turbine.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.turbine.stream.EnableTurbineStream;

/**
 * 摘要:Hystrix监控数据聚合
 *
 * @author xyt
 * @create 2017-12-19 下午4:59
 **/

@EnableTurbineStream
@EnableDiscoveryClient
@SpringCloudApplication
public class TurbineApplication {
    private static final Logger LOG = LoggerFactory.getLogger(TurbineApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(TurbineApplication.class, args);
        LOG.info("TurbineApplication-> TurbineApplication start  Success !!!");
    }
}
