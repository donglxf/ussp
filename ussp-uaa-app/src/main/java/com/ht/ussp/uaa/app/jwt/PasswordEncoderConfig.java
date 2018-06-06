package com.ht.ussp.uaa.app.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 
 * @ClassName: PasswordEncoderConfig
 * @Description: 配置加密算法
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月6日 上午10:42:40
 */
@Configuration
public class PasswordEncoderConfig {
    @Bean
    protected BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
