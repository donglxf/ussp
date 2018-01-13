package com.ht.ussp.gateway.app.jwt;

import java.util.Optional;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

/**
 * 
 * @ClassName: RefreshToken
 * @Description: TODO
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月6日 上午11:50:30
 */
@SuppressWarnings("unchecked")
public class RefreshToken implements JwtToken {
    private Jws<Claims> claims;

    private RefreshToken(Jws<Claims> claims) {
        this.claims = claims;
    }

    /**
     * 
      * @Title: create 
      * @Description: 创建并验证refresh token 
      * @return Optional<RefreshToken>
      * @throws
     */
    public static Optional<RefreshToken> create(RawAccessJwtToken token, String signingKey) {
        Jws<Claims> claims = token.parseClaims(signingKey);
//        String userId=claims.getBody().get("userId").toString();
//        String controller=claims.getBody().get("controller").toString();
//        List<String> scopes = claims.getBody().get("role", List.class);
//        if (scopes == null || scopes.isEmpty() 
//                || !scopes.stream().filter(scope -> Scopes.REFRESH_TOKEN.authority().equals(scope)).findFirst().isPresent()) {
//            return Optional.empty();
//        }

        return Optional.of(new RefreshToken(claims));
    }

    @Override
    public String getToken() {
        return null;
    }

    public Jws<Claims> getClaims() {
        return claims;
    }
    
    public String getJti() {
        return claims.getBody().getId();
    }
    
    public String getSubject() {
        return claims.getBody().getSubject();
    }
    
    public String getUserId() {
    	return claims.getBody().get("userId").toString();
    }
    
    public String getController() {
    	return claims.getBody().get("controller").toString();
    }
}
