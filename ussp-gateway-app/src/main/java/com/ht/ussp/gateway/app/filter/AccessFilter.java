package com.ht.ussp.gateway.app.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.ussp.common.SysStatus;
import com.ht.ussp.gateway.app.feignClients.RoleClient;
import com.ht.ussp.gateway.app.feignClients.UaaClient;
import com.ht.ussp.gateway.app.model.ResponseModal;
import com.ht.ussp.gateway.app.model.ValidateJwtVo;
import com.ht.ussp.util.FastJsonUtil;
import com.ht.ussp.util.PatternUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import lombok.extern.log4j.Log4j2;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: AccessFilter
 * @Description: 内部用户和系统级鉴权过滤器
 * @date 2018年3月12日 上午11:50:44
 */
@Log4j2
public class AccessFilter extends ZuulFilter {

    @Value("${ht.ignoreUrl.app}")
    private String htIgnoreApp;

    @Value("${ht.ignoreUrl.web}")
    private String htIgnoreUrlWeb;

    @Value("${ht.ignoreUrl.http}")
    private String htIgnoreUrlHttp;

    @Autowired
    private UaaClient UaaClient;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    protected ZuulProperties zuulProperties;

    @Autowired
    private RoleClient roleClient;

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Object obj = ctx.get("isNotOutSystem");
        if (obj == null) {
            return true;
        }
        return (boolean) obj;
    }

    @Override
    public Object run() {

        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.getResponse().setCharacterEncoding("UTF-8");
        HttpServletRequest request = ctx.getRequest();
        String bestMatchingPattern = request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingPattern").toString();
        String uri = request.getRequestURI().toString();
        // 必须带Authorization
        String tokenPayload = request.getHeader("Authorization");
        String app = request.getHeader("app");
        String validateUrl = getUrl(uri, bestMatchingPattern);
        log.info("----------validateUrl------------" + validateUrl);
        // 不鉴权的URL直接路由
        if (isIgnoreUrl(uri, htIgnoreUrlWeb)) {
            return null;
        }
        // 鉴权TOKEN不能为空
        if (StringUtils.isEmpty(tokenPayload)) {
            ctx.setSendZuulResponse(false);
            try {
                mapper.writeValue(ctx.getResponse().getWriter(), new ResponseModal(SysStatus.TOKEN_IS_NULL));
            } catch (IOException e) {
                log.debug("write response result token is null:" + e.getMessage());
            } finally {
                try {
                    ctx.getResponse().getWriter().close();
                } catch (IOException e) {
                    log.debug("close io exception:" + e.getMessage());
                }
            }
            return null;
        }
        String userId = null;
        // 如果系统级别URL不为空，进行系统级别鉴权
        if (!StringUtils.isEmpty(htIgnoreApp)) {
            String[] htIgnoreAppUrls = htIgnoreApp.split(",");
            for (String htIgnoreAppUrl : htIgnoreAppUrls) {
                if (PatternUtil.compile(htIgnoreAppUrl).match(uri)) {
                    log.info("----------validate SYSTEM LEVEL JWT START------------");
                    //app不能为空
                    if (StringUtils.isEmpty(app)) {
                        ctx.setSendZuulResponse(false);
                        try {
                            mapper.writeValue(ctx.getResponse().getWriter(), new ResponseModal(SysStatus.APP_CANNOT_NULL));
                        } catch (IOException e) {
                            log.debug("write response result app cannot null:" + e.getMessage());
                        } finally {
                            try {
                                ctx.getResponse().getWriter().close();
                            } catch (IOException e) {
                                log.debug("close io exception:" + e.getMessage());
                            }
                        }
                        return null;
                    }
                    ResponseModal rm = UaaClient.validateAppJwt(tokenPayload, app);
                    if ("0000".equals(rm.getStatus_code())) {
                        log.info("----------validate SYSTEM LEVEL JWT SUCCESSFUL------------");
                        ctx.setSendZuulResponse(true);
                        return null;
                    } else if ("9922".equals(rm.getStatus_code())) {
                        ctx.setSendZuulResponse(false);
                        try {
                            mapper.writeValue(ctx.getResponse().getWriter(), new ResponseModal(SysStatus.TOKEN_IS_VALID));
                        } catch (IOException e) {
                            log.debug("write response result token is valid:" + e.getMessage());
                        } finally {
                            try {
                                ctx.getResponse().getWriter().close();
                            } catch (IOException e) {
                                log.debug("close io exception:" + e.getMessage());
                            }
                        }
                        return null;
                    } else if ("9921".equals(rm.getStatus_code())) {
                        ctx.setSendZuulResponse(false);
                        try {
                            mapper.writeValue(ctx.getResponse().getWriter(), new ResponseModal(SysStatus.TOKEN_IS_EXPIRED));
                        } catch (IOException e) {
                            log.debug("write response result token is expired:" + e.getMessage());
                        } finally {
                            try {
                                ctx.getResponse().getWriter().close();
                            } catch (IOException e) {
                                log.debug("close io exception:" + e.getMessage());
                            }
                        }
                        return null;
                    }

                }


            }
        }
        // 如果请求的URL与htIgnoreApp不匹配，验证内部系统JWT
        try {
            log.info("----------validate JWT START------------" + tokenPayload);
            ResponseModal rm = UaaClient.validateJwt(tokenPayload);
            if ("0000".equals(rm.getStatus_code())) {
                String str = FastJsonUtil.objectToJson(rm.getResult());
                ValidateJwtVo vdt = FastJsonUtil.jsonToPojo(str, ValidateJwtVo.class);
                userId = vdt.getUserId();
                ctx.addZuulRequestHeader("userId", userId);
                ctx.addZuulRequestHeader("orgCode", vdt.getOrgCode());
                log.info("----------validate JWT SUCCESSFUL------------");
            } else if ("9922".equals(rm.getStatus_code()) || "9997".equals(rm.getStatus_code())) {
                ctx.setSendZuulResponse(false);
                try {
                    mapper.writeValue(ctx.getResponse().getWriter(), new ResponseModal(SysStatus.TOKEN_IS_VALID));
                } catch (IOException e) {
                    log.debug("write response result token is valid:" + e.getMessage());
                } finally {
                    try {
                        ctx.getResponse().getWriter().close();
                    } catch (IOException e) {
                        log.debug("close io exception:" + e.getMessage());
                    }
                }
                return null;
            } else if ("9921".equals(rm.getResult_msg())) {
                ctx.setSendZuulResponse(false);
                try {
                    mapper.writeValue(ctx.getResponse().getWriter(), new ResponseModal(SysStatus.TOKEN_IS_EXPIRED));
                } catch (IOException e) {
                    log.debug("write response result token is expired:" + e.getMessage());
                } finally {
                    try {
                        ctx.getResponse().getWriter().close();
                    } catch (IOException e) {
                        log.debug("close io exception:" + e.getMessage());
                    }
                }
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                mapper.writeValue(ctx.getResponse().getWriter(), new ResponseModal(SysStatus.FAIL));
            } catch (IOException e) {
                log.debug("write response result fail:" + e.getMessage());
            } finally {
                try {
                    ctx.getResponse().getWriter().close();
                } catch (IOException e) {
                    log.debug("close io exception:" + e.getMessage());
                }
            }
        }
        ctx.getResponse().setHeader("Content-type", "application/json;charset=UTF-8");
        ctx.setSendZuulResponse(true);
        ctx.setResponseStatusCode(200);
        // 验证token，不验证API
        if (isIgnoreUrl(uri, htIgnoreUrlHttp)) {
            return null;
        }
        log.info("----------validate api start------------");
        // 验证api权限
        String api_key = String.format("%s:%s:%s", userId, app, "api");
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(app) || !roleClient.isHasAuth(api_key, validateUrl)) {
            ctx.setSendZuulResponse(false);
            try {
                mapper.writeValue(ctx.getResponse().getWriter(), new ResponseModal(SysStatus.HAS_NO_ACCESS));
            } catch (IOException e) {
                log.debug("write response result has no access:" + e.getMessage());
            } finally {
                try {
                    ctx.getResponse().getWriter().close();
                } catch (IOException e) {
                    log.debug("close io exception:" + e.getMessage());
                }
            }
            return null;
        }
        log.info("----------validate api END------------");
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
        return 1;
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
     * @param uri                 请求的url
     * @param bestMatchingPattern 请求的服务名或服务映射
     * @return 真实的url
     * @author 谭荣巧
     * @Date 2018/1/31 14:00
     */
    private String getUrl(String uri, String bestMatchingPattern) {
        if (PatternUtil.compile(bestMatchingPattern).match(uri)) {
            bestMatchingPattern = bestMatchingPattern.replace("**", "");
            return uri.replace(bestMatchingPattern, "/");
        }
        for (Map.Entry<String, ZuulProperties.ZuulRoute> entry : zuulProperties.getRoutes().entrySet()) {
            String path = entry.getValue().getPath();
            if (PatternUtil.compile(path).match(uri)) {
                path = path.replace("**", "");
                return uri.replace(path, "/");
            }
        }
        log.warn("未能过滤服务名或服务映射地址的请求URL：" + uri + "，bestMatchingPattern：" + bestMatchingPattern);
        return uri;
    }
}
