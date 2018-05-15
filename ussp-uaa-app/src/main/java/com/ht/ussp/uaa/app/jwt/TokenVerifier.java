package com.ht.ussp.uaa.app.jwt;

/**
 * 
 * @ClassName: TokenVerifier
 * @Description: TODO
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月8日 上午10:04:41
 */
public interface TokenVerifier {
    public boolean verify(String jti);
}
