package com.ht.ussp.gateway.app.security.point;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.gateway.app.config.JwtSettings;
import com.ht.ussp.gateway.app.config.WebSecurityConfig;
import com.ht.ussp.gateway.app.exception.InvalidJwtToken;
import com.ht.ussp.gateway.app.jwt.JwtToken;
import com.ht.ussp.gateway.app.jwt.JwtTokenFactory;
import com.ht.ussp.gateway.app.jwt.RawAccessJwtToken;
import com.ht.ussp.gateway.app.jwt.RefreshToken;
import com.ht.ussp.gateway.app.jwt.TokenExtractor;
import com.ht.ussp.gateway.app.jwt.TokenVerifier;
import com.ht.ussp.gateway.app.vo.UserVo;


/**
 * 
* @ClassName: RefreshTokenEndpoint
* @Description: TODO
* @author wim qiuwenwu@hongte.info
* @date 2018年1月8日 上午10:06:02
 */
@RestController
public class RefreshTokenEndpoint {
    @Autowired private JwtTokenFactory tokenFactory;
    @Autowired private JwtSettings jwtSettings;
    @Autowired private TokenVerifier tokenVerifier;
    @Autowired @Qualifier("jwtHeaderTokenExtractor") private TokenExtractor tokenExtractor;
    
    @RequestMapping(value="/api/auth/token", method=RequestMethod.GET, produces={ MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody JwtToken refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String tokenPayload = tokenExtractor.extract(request.getHeader(WebSecurityConfig.AUTHENTICATION_HEADER_NAME));
        
        RawAccessJwtToken rawToken = new RawAccessJwtToken(tokenPayload);
        RefreshToken refreshToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey()).orElseThrow(() -> new InvalidJwtToken());

        String jti = refreshToken.getJti();
        if (!tokenVerifier.verify(jti)) {
            throw new InvalidJwtToken();
        }

        UserVo userVo=new UserVo();
        userVo.setUserId(refreshToken.getUserId());
        userVo.setController(refreshToken.getController());
        return tokenFactory.createAccessJwtToken(userVo);
    }
}
