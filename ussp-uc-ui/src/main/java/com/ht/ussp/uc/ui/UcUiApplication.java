package com.ht.ussp.uc.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ht.ussp.bean"})
public class UcUiApplication {
    public static void main(String[] args) {
        SpringApplication.run(UcUiApplication.class, args);
    }
}
