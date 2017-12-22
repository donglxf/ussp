package com.ht.ussp.hystrix.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.stream.EnableTurbineStream;

/**
 * 摘要:Hystrix监控面板
 * 此处合并turbine
 *
 * @author xyt
 * @create 2017-12-19 下午3:17
 **/
@EnableHystrix
@EnableTurbineStream
@EnableHystrixDashboard
@SpringCloudApplication
//@EnableTurbineAmqp
public class HystrixDashboardApplication {
    private static final Logger LOG = LoggerFactory.getLogger(HystrixDashboardApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(HystrixDashboardApplication.class, args);
        LOG.info("HystrixDashboardApplication-> HystrixDashboardApplication start  Success !!!");
    }
}