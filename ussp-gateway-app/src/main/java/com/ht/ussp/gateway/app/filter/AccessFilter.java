package com.ht.ussp.gateway.app.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.security.authentication.BadCredentialsException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.ussp.gateway.app.config.JwtSettings;
import com.ht.ussp.gateway.app.config.WebSecurityConfig;
import com.ht.ussp.gateway.app.exception.JwtExpiredTokenException;
import com.ht.ussp.gateway.app.feignClients.RoleClient;
import com.ht.ussp.gateway.app.jwt.RawAccessJwtToken;
import com.ht.ussp.gateway.app.jwt.TokenExtractor;
import com.ht.ussp.gateway.app.model.ResponseModal;
import com.ht.ussp.gateway.app.util.SysStatus;
import com.ht.ussp.util.PatternUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: AccessFilter
 * @Description: 请求过滤
 * @date 2018年1月17日 下午2:01:58
 */
public class AccessFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(AccessFilter.class);

    @Autowired
    private TokenExtractor tokenExtractor;

    @Autowired
    private JwtSettings jwtSettings;

    @Autowired
    private RoleClient roleClient;

    @Autowired
    private ObjectMapper mapper;

    @Value("${ht.ignoreUrl.web}")
    private String htIgnoreUrlWeb;
    @Value("${ht.ignoreUrl.http}")
    private String htIgnoreUrlHttp;
    @Autowired
    protected ZuulProperties zuulProperties;

    /**
     * 定义该过滤器是否要执行
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        Jws<Claims> jwsClaims;
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.getResponse().setCharacterEncoding("UTF-8");
        HttpServletRequest request = ctx.getRequest();
        String uri = request.getRequestURI().toString();
        String validateUrl = getUrl(uri);//uri.substring(uri.indexOf("/", uri.indexOf("/") + 1));
        //排除不验证的请求，支持通配符“*”
        if (isIgnoreUrl(uri, htIgnoreUrlWeb)) {
            return null;
        }

        // 必须带Authorization
        String tokenPayload = request.getHeader(WebSecurityConfig.AUTHENTICATION_HEADER_NAME);
        String app = request.getHeader("app");

        if (StringUtils.isEmpty(tokenPayload)) {
            ctx.setSendZuulResponse(false);
            try {
                mapper.writeValue(ctx.getResponse().getWriter(), new ResponseModal(SysStatus.TOKEN_IS_NULL));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        // 验证JWT
        RawAccessJwtToken AccessToken = new RawAccessJwtToken(tokenExtractor.extract(tokenPayload));
        try {
            jwsClaims = AccessToken.parseClaims(jwtSettings.getTokenSigningKey());
        } catch (BadCredentialsException ex) {
            ctx.setSendZuulResponse(false);
            try {
                mapper.writeValue(ctx.getResponse().getWriter(), new ResponseModal(SysStatus.TOKEN_IS_VALID));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;

        } catch (JwtExpiredTokenException e2) {

            try {
                mapper.writeValue(ctx.getResponse().getWriter(), new ResponseModal(SysStatus.TOKEN_IS_EXPIRED));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        String userId = jwsClaims.getBody().get("userId").toString();
        String orgCode = jwsClaims.getBody().get("orgCode").toString();

        ctx.addZuulRequestHeader("userId", userId);
        ctx.addZuulRequestHeader("orgCode", orgCode);

        ctx.getResponse().setHeader("Content-type", "application/json;charset=UTF-8");
        ctx.setSendZuulResponse(true);
        ctx.setResponseStatusCode(200);

        //排除不验证的请求，支持通配符“*”
        if (isIgnoreUrl(uri, htIgnoreUrlHttp)) {
            return null;
        }

        //验证api权限
        log.debug("------------validateUrl------" + validateUrl);
        String api_key = String.format("%s:%s:%s", userId, app, "api");
        if (!roleClient.IsHasAuth(api_key, validateUrl)) {
            ctx.setSendZuulResponse(false);
            try {
                mapper.writeValue(ctx.getResponse().getWriter(), new ResponseModal(SysStatus.HAS_NO_ACCESS));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }

    /**
     * 过滤器类型，
     * pre:表示在请求之前执行
     * routing: 表示请求时被调用
     * post:在route和error过滤器之后被调用
     * error:处理请求发生错误时被调用
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 过滤器的执行顺序
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 判断是否为过滤的url<br>
     *
     * @param url       待验证的url
     * @param ignoreUrl 需要过滤的url
     * @return true 为过滤的url，false 为不过滤的url
     * @author 谭荣巧
     * @Date 2018/2/7 14:07
     */
    private boolean isIgnoreUrl(String url, String ignoreUrl) {
        if (!StringUtils.isEmpty(ignoreUrl)) {
            String[] ignoreUrlHttps = ignoreUrl.split(",");
            for (String ignoreUrlHttp : ignoreUrlHttps) {
                if (PatternUtil.compile(ignoreUrlHttp).match(url)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据网关路由配置获取真实路径映射地址<br>
     *
     * @param uri 请求的url
     * @return 真实的url
     * @author 谭荣巧
     * @Date 2018/1/31 14:00
     */
    private String getUrl(String uri) {
        for (Map.Entry<String, ZuulProperties.ZuulRoute> entry : zuulProperties.getRoutes().entrySet()) {
            String path = entry.getValue().getPath();
            if (PatternUtil.compile(path).match(uri)) {
                path = path.replace("**", "");
                return uri.replace(path, "/");
            }
        }
        return uri;
    }
}
