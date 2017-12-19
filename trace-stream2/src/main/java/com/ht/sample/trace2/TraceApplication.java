package com.ht.sample.trace2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * 摘要:测试微服务路径跟踪
 *
 * @author xyt
 * @create 2017-12-18 下午8:09
 **/

@RestController
@EnableDiscoveryClient
@SpringBootApplication
public class TraceApplication {
    private static final Logger LOG = LoggerFactory.getLogger(TraceApplication.class);

    @Bean
    public AlwaysSampler defaultSampler() {
        return new AlwaysSampler();
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }



    @RequestMapping(value = "/trace-2", method = RequestMethod.GET)
    public String trace(HttpServletRequest request) {
        LOG.info("===<call trace-2, TraceId={}, SpanId={}>===",
                request.getHeader("X-B3-TraceId"), request.getHeader("X-B3-SpanId"));
        return "Trace";
    }

    public static void main(String[] args) {
        SpringApplication.run(TraceApplication.class, args);
    }

}
