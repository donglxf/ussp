package org.ussp.uaa.app.security.point;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.ussp.uaa.app.config.JwtSettings;
import org.ussp.uaa.app.config.WebSecurityConfig;
import org.ussp.uaa.app.exception.InvalidJwtToken;
import org.ussp.uaa.app.exception.JwtExpiredTokenException;
import org.ussp.uaa.app.jwt.JwtToken;
import org.ussp.uaa.app.jwt.JwtTokenFactory;
import org.ussp.uaa.app.jwt.RawAccessJwtToken;
import org.ussp.uaa.app.jwt.RefreshToken;
import org.ussp.uaa.app.jwt.TokenExtractor;
import org.ussp.uaa.app.jwt.TokenVerifier;
import org.ussp.uaa.app.model.ResponseModal;
import org.ussp.uaa.app.util.SysStatus;
import org.ussp.uaa.app.vo.UserVo;

import com.fasterxml.jackson.databind.ObjectMapper;


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
    @Autowired @Qualifier("jwtHeaderTokenExtractor")
    private TokenExtractor tokenExtractor;
    @Autowired
    private  ObjectMapper mapper;
    
    @RequestMapping(value="/uaa/auth/token", method=RequestMethod.GET, produces={ MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody JwtToken refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	response.setCharacterEncoding("UTF-8");
        String tokenPayload = tokenExtractor.extract(request.getHeader(WebSecurityConfig.AUTHENTICATION_HEADER_NAME));
        
        RawAccessJwtToken rawToken = new RawAccessJwtToken(tokenPayload);
        RefreshToken refreshToken=null;
        try {
          refreshToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey()).orElseThrow(() -> new InvalidJwtToken());
        } catch (BadCredentialsException ex) {
        	mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.TOKEN_IS_VALID));
        	return null;
        } catch (JwtExpiredTokenException expiredEx) {
        	mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.TOKEN_IS_EXPIRED));
        }
        String jti = refreshToken.getJti();
        if (!tokenVerifier.verify(jti)) {
            throw new InvalidJwtToken();
        }

        UserVo userVo=new UserVo();
        userVo.setUserId(refreshToken.getUserId());
        userVo.setOrgCode(refreshToken.getOrgCode());
        return tokenFactory.createAccessJwtToken(userVo);
    }
    
    @RequestMapping(value="/uaa/hello")
    public String hello(){
    	System.out.println("----hello");
    	return "hello";
    	
    }
}
