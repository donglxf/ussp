package com.ht.ussp.uaa.app.jwt;

/**
 * 
 * @ClassName: TokenExtractor
 * @Description: TODO
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月6日 下午12:45:08
 */
public interface TokenExtractor {
    public String extract(String payload);
}
