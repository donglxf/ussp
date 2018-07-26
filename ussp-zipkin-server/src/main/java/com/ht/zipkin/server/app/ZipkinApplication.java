package com.ht.zipkin.server.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.sleuth.zipkin.stream.EnableZipkinStreamServer;

/**
 * 摘要:zipkin路径跟踪
 *
 * @author xyt
 * @create 2017-12-18 下午4:04
 **/

@SpringBootApplication
@EnableZipkinStreamServer
@EnableEurekaClient
public class ZipkinApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ZipkinApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(ZipkinApplication.class, args);
        LOG.info("ZipkinApplication-> ZipkinApplication start  Success !!!");
    }
}
