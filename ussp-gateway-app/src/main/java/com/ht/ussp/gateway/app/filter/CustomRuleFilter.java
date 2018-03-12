package com.ht.ussp.gateway.app.filter;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 自定义路由过滤器<br>
 *
 * @author 谭荣巧
 * @Date 2018/3/8 20:39
 */
@Log4j2
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomRuleFilter extends ZuulFilter {
    public static final String INSTANCE_ID = "instanceId";
    public static final String DEFULT_INSTANCE_ID = "defultInstanceId";
    public static final String EXCLUDE_INSTANCE_ID = "excludeInstanceId";
    public static final String DEFULT_EXCLUDE_INSTANCE_ID = "defultExcludeInstanceId";
    public static final String IP_ADDR = "ip";
    public static final String SPLIT = ",";

    public static final HystrixRequestVariableDefault<List<String>> instanceIds = new HystrixRequestVariableDefault<>();//指定路由的实例名
    public static final HystrixRequestVariableDefault<List<String>> defultInstanceIds = new HystrixRequestVariableDefault<>();//默认指定路由的实例名
    public static final HystrixRequestVariableDefault<List<String>> excludeInstanceIds = new HystrixRequestVariableDefault<>();//排除指定路由的实例名
    public static final HystrixRequestVariableDefault<List<String>> defultExcludeInstanceIds = new HystrixRequestVariableDefault<>();//默认排除指定路由的实例名
    public static final HystrixRequestVariableDefault<List<String>> ipAddrs = new HystrixRequestVariableDefault<>();//指定路由的ip

    public static String test = "54354";

    @Value("${ht.rule.defultInstanceId:}")
    private String defultInstanceId;//默认指定路由的实例名
    @Value("${ht.rule.defultExcludeInstanceId:}")
    private String defultExcludeInstanceId;//默认排除指定路由的实例名

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String aInstanceId = request.getParameter(INSTANCE_ID);
        String hInstanceId = request.getHeader(INSTANCE_ID);
        String excludeInstanceId = request.getHeader(EXCLUDE_INSTANCE_ID);

//        Enumeration names = request.getHeaderNames();
//        log.debug("===========================打印http头信息===========================");
//        while (names.hasMoreElements()) {
//            String name = (String) names.nextElement();
//            log.debug(name + ":" + request.getHeader(name));
//        }
//        log.debug("===================================================================");


        String ipAddr = getIpAddress(request);
        log.debug(String.format("匹配规则：(instanceId：%s|%s)(defultInstanceId：%s)(excludeInstanceId：%s)(defultExcludeInstanceId：%s)(ipAddr：%s)", aInstanceId, hInstanceId, defultInstanceId, excludeInstanceId, defultExcludeInstanceId, ipAddr));
        CustomRuleFilter.initHystrixRequestContext(aInstanceId, hInstanceId, defultInstanceId, excludeInstanceId, defultExcludeInstanceId, ipAddr);
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            if (CustomRuleFilter.instanceIds.get() != null)
                ctx.addZuulRequestHeader(CustomRuleFilter.INSTANCE_ID, Arrays.toString(CustomRuleFilter.instanceIds.get().toArray())); // 传递给后续微服务
            if (CustomRuleFilter.defultInstanceIds.get() != null)
                ctx.addZuulRequestHeader(CustomRuleFilter.DEFULT_INSTANCE_ID, Arrays.toString(CustomRuleFilter.defultInstanceIds.get().toArray())); // 传递给后续微服务
            if (CustomRuleFilter.excludeInstanceIds.get() != null)
                ctx.addZuulRequestHeader(CustomRuleFilter.EXCLUDE_INSTANCE_ID, Arrays.toString(CustomRuleFilter.excludeInstanceIds.get().toArray())); // 传递给后续微服务
            if (CustomRuleFilter.defultExcludeInstanceIds.get() != null)
                ctx.addZuulRequestHeader(CustomRuleFilter.DEFULT_EXCLUDE_INSTANCE_ID, Arrays.toString(CustomRuleFilter.defultExcludeInstanceIds.get().toArray())); // 传递给后续微服务
            if (ipAddr != null && ipAddr.length() > 0)
                ctx.addZuulRequestHeader(CustomRuleFilter.IP_ADDR, ipAddr); // 传递给后续微服务
        }
        return null;
    }

    public static void initHystrixRequestContext(String aInstanceId, String hInstanceId, String defultInstanceId, String excludeInstanceId, String defultExcludeInstanceId, String ip) {
        if (!HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.initializeContext();
        }

        List<String> instanceIdList = new ArrayList<>();
        if (!StringUtils.isEmpty(aInstanceId)) {
            instanceIdList.addAll(Arrays.asList(aInstanceId.split(CustomRuleFilter.SPLIT)));
        }

        if (!StringUtils.isEmpty(hInstanceId)) {
            instanceIdList.addAll(Arrays.asList(hInstanceId.split(CustomRuleFilter.SPLIT)));
        }


        if (instanceIdList.size() > 0) {
            CustomRuleFilter.instanceIds.set(instanceIdList);
        } else {
            CustomRuleFilter.instanceIds.set(Collections.emptyList());
        }

        if (!StringUtils.isEmpty(defultInstanceId)) {
            CustomRuleFilter.defultInstanceIds.set(Arrays.asList(defultInstanceId.split(CustomRuleFilter.SPLIT)));
        } else {
            CustomRuleFilter.defultInstanceIds.set(Collections.emptyList());
        }

        if (!StringUtils.isEmpty(excludeInstanceId)) {
            CustomRuleFilter.excludeInstanceIds.set(Arrays.asList(excludeInstanceId.split(CustomRuleFilter.SPLIT)));
        } else {
            CustomRuleFilter.excludeInstanceIds.set(Collections.emptyList());
        }

        if (!StringUtils.isEmpty(defultExcludeInstanceId)) {
            CustomRuleFilter.defultExcludeInstanceIds.set(Arrays.asList(defultExcludeInstanceId.split(CustomRuleFilter.SPLIT)));
        } else {
            CustomRuleFilter.defultExcludeInstanceIds.set(Collections.emptyList());
        }

        if (!StringUtils.isEmpty(ip)) {
            CustomRuleFilter.ipAddrs.set(Arrays.asList(ip.split(CustomRuleFilter.SPLIT)));
        } else {
            CustomRuleFilter.ipAddrs.set(Collections.emptyList());
        }
    }

    public static void initHystrixRequestContext(String defultInstanceId, String defultExcludeInstanceId) {
        if (!HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.initializeContext();
        }


        if (!StringUtils.isEmpty(defultInstanceId)) {
            CustomRuleFilter.defultInstanceIds.set(Arrays.asList(defultInstanceId.split(CustomRuleFilter.SPLIT)));
        } else {
            CustomRuleFilter.defultInstanceIds.set(Collections.emptyList());
        }

        if (!StringUtils.isEmpty(defultExcludeInstanceId)) {
            CustomRuleFilter.defultExcludeInstanceIds.set(Arrays.asList(defultExcludeInstanceId.split(CustomRuleFilter.SPLIT)));
        } else {
            CustomRuleFilter.defultExcludeInstanceIds.set(Collections.emptyList());
        }
    }

    public static void shutdownHystrixRequestContext() {
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.getContextForCurrentThread().shutdown();
        }
    }

    public String getIpAddress(HttpServletRequest request) {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址

        String ip = request.getHeader("X-Forwarded-For");
        if (log.isInfoEnabled()) {
            log.info("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
                if (log.isInfoEnabled()) {
                    log.info("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
                if (log.isInfoEnabled()) {
                    log.info("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
                if (log.isInfoEnabled()) {
                    log.info("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                if (log.isInfoEnabled()) {
                    log.info("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
                    //根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ip = inet.getHostAddress();
                }
                if (log.isInfoEnabled()) {
                    log.info("getIpAddress(HttpServletRequest) - getLocalHost - String ip=" + ip + " - getRemoteAddr " + request.getRemoteAddr());
                }
            }
        } else if (ip != null && ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
//            if (ip.indexOf(",") > 0) {
//                ip = ip.substring(0, ip.indexOf(","));
//            }
        }
        return ip;
    }

}
