package com.ht.ussp.uaa.app.jwt;

import org.springframework.stereotype.Component;

/**
 * 
 * @ClassName: BloomFilterTokenVerifier
 * @Description: 验证JTI
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月6日 上午11:49:40
 */
@Component
public class BloomFilterTokenVerifier implements TokenVerifier {
    @Override
    public boolean verify(String jti) {
        return true;
    }
}
