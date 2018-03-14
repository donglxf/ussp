package com.ht.ussp.gateway.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import com.ht.ussp.gateway.app.filter.AccessFilter;

import lombok.extern.log4j.Log4j2;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: GatewayApplication
 * @Description: 网关
 * @date 2018年1月3日 下午6:14:42
 */
@Log4j2
@EnableZuulProxy
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class GatewayApplication {


    @Value("${ht.rule.enabled:false}")
    private boolean enabled;

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        log.info("网关启动成功！");

    }

//    @Bean
//    public CustomRuleFilter customRuleFilter() {
//        return new CustomRuleFilter();
//    }

    @Bean
    public AccessFilter accessFilter() {
        return new AccessFilter();
    }

//    @Bean
//    public IRule ribbonRule() {
//        //判断是否启用自定义负载策略
//        if (enabled) {
//            return new InstanceIdAndIPAddrRule();//这里配置策略，和配置文件对应
//        }
//        return new RoundRobinRule();
//    }
}
