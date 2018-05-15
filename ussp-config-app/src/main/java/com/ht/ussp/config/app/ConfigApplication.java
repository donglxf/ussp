package com.ht.ussp.config.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * 摘要:配置中心
 *
 * @author xyt
 * @create 2017-12-14 下午2:59
 **/
@EnableDiscoveryClient
@SpringBootApplication
@EnableConfigServer
public class ConfigApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class, args);
        LOG.info("ConfigApplication-> ConfigApplication start  Success !!!");
    }
}
