package com.ht.ussp.gateway.app.filter;


import java.util.Arrays;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 
 * @ClassName: CustomCorsFilter
 * @Description: 处理ajax跨域Cross-Origin Resource Sharing
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月6日 上午10:30:07
 */

//保证优先执行，与BEAN注册顺序有关
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomCorsFilter extends CorsFilter {

    public CustomCorsFilter() {
        super(configurationSource());
    }

    private static UrlBasedCorsConfigurationSource configurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        //允许Cookie跨域
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        //检查请求的缓存时间，在此时间段内，相同的跨域请求不再做检查，单位秒（12小时）
        config.setMaxAge(43200L);
        //允许提交请求的方法
        config.setAllowedMethods(Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS"));
        //注册cors过滤器
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}