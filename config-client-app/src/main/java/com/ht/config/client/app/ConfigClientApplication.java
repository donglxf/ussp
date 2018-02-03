package com.ht.config.client.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 摘要:
 *
 * @author xyt
 * @create 2017-12-14 下午8:30
 **/
@EnableWebMvc
@SpringBootApplication
public class ConfigClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApplication.class, args);
    }
}

