package com.ht.config.client.app;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * 摘要:
 *
 * @author xyt
 * @create 2017-12-14 下午8:30
 **/

@SpringCloudApplication
public class ConfigClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApplication.class, args);
    }
}

