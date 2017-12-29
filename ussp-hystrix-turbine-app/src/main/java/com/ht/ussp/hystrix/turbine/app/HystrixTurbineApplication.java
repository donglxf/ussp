package com.ht.ussp.hystrix.turbine.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.context.annotation.Configuration;

/**
 * 摘要:turbine Hystrix  监控面板
 *
 * @author xyt
 * @create 2017-12-22 上午9:06
 **/

@Configuration
@EnableAutoConfiguration
@EnableTurbine
@EnableDiscoveryClient
@EnableHystrixDashboard
public class HystrixTurbineApplication {
    private static final Logger LOG = LoggerFactory.getLogger(HystrixTurbineApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(HystrixTurbineApplication.class, args);
        LOG.info("HystrixTurbineApplication-> HystrixTurbineApplication start  Success !!!");
    }
}
