package com.ht.ussp.gateway.app.rule;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.client.config.IClientConfig;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * 自定义路由策略<br>
 * 可以通过
 *
 * @author 谭荣巧
 * @Date 2018/3/8 20:21
 */
@Log4j2
public class InstanceIdAndIPAddrRule extends AbstractLoadBalancerRule {

    public final String META_DATA_KEY_WEIGHT = "weight";

    public final String INSTANCE_ID = "instanceId";
    public final String DEFULT_INSTANCE_ID = "defultInstanceId";
    public final String EXCLUDE_INSTANCE_ID = "excludeInstanceId";
    public final String DEFULT_EXCLUDE_INSTANCE_ID = "defultExcludeInstanceId";
    public final String IP_ADDR = "ip";
    public final String SPLIT = ",";

    public final HystrixRequestVariableDefault<List<String>> instanceIds = new HystrixRequestVariableDefault<>();//指定路由的实例名
    public final HystrixRequestVariableDefault<List<String>> defultInstanceIds = new HystrixRequestVariableDefault<>();//默认指定路由的实例名
    public final HystrixRequestVariableDefault<List<String>> excludeInstanceIds = new HystrixRequestVariableDefault<>();//排除指定路由的实例名
    public final HystrixRequestVariableDefault<List<String>> defultExcludeInstanceIds = new HystrixRequestVariableDefault<>();//默认排除指定路由的实例名
    public final HystrixRequestVariableDefault<List<String>> ipAddrs = new HystrixRequestVariableDefault<>();//指定路由的ip

    private Random random = new Random();

    @Value("${ht.rule.defultInstanceId:}")
    private String defultInstanceId;//默认指定路由的实例名
    @Value("${ht.rule.defultExcludeInstanceId:}")
    private String defultExcludeInstanceId;//默认排除指定路由的实例名

    @Override
    public Server choose(Object key) {
        return choose(getLoadBalancer(), key);
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        // TODO Auto-generated method stub
    }

    public Server choose(ILoadBalancer lb, Object key) {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String path = "";
        if (request != null) {
            path = request.getRequestURI();
        }
        inti();
        List<Server> serverList = lb.getAllServers();
//        log.debug(String.format("%-23s %-30s", "待匹配的服务实例名", "待匹配的服务IP地址"));
//        for (Server server : serverList) {
//            InstanceInfo instanceInfo = ((DiscoveryEnabledServer) server).getInstanceInfo();
//            log.debug(String.format("%-30s %-30s", instanceInfo.getInstanceId(), instanceInfo.getIPAddr()));
//        }

        if (CollectionUtils.isEmpty(serverList)) {
            return null;
        }

        // 计算总值并剔除0权重节点
        int totalWeight = 0;
        Map<Server, Integer> serverWeightMap = new HashMap<>();
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            for (Server server : serverList) {
                InstanceInfo instanceInfo = ((DiscoveryEnabledServer) server).getInstanceInfo();
                String instanceId = instanceInfo.getInstanceId();//待匹配的服务实例名

                // 优先匹配自定义服务实例名
                if (instanceIds.get() != null && instanceIds.get().contains(instanceId)) {
                    log.debug(String.format("【%-50s->匹配服务成功    ：%s】", path, instanceId));
                    shutdownHystrixRequestContext();
                    return server;
                }
            }
            for (Server server : serverList) {
                InstanceInfo instanceInfo = ((DiscoveryEnabledServer) server).getInstanceInfo();
                String instanceId = instanceInfo.getInstanceId();//待匹配的服务实例名

                // 排除的路由实例
                if (excludeInstanceIds.get() != null && excludeInstanceIds.get().contains(instanceId)) {
                    //log.debug(String.format("【%-50s->排除服务成功：%s】" , path, instanceId));
                    continue;
                }

                // 匹配默认服务实例名
                if (defultInstanceIds.get() != null && defultInstanceIds.get().contains(instanceId)) {
                    log.debug(String.format("【%-50s->匹配默认服务成功：%s】", path, instanceId));
                    shutdownHystrixRequestContext();
                    return server;
                }
            }

            for (Server server : serverList) {
                InstanceInfo instanceInfo = ((DiscoveryEnabledServer) server).getInstanceInfo();
                String instanceId = instanceInfo.getInstanceId();//待匹配的服务实例名
                String ipAddr = instanceInfo.getIPAddr();//待匹配的服务IP地址

                // 排除的路由实例
                if (excludeInstanceIds.get() != null && excludeInstanceIds.get().contains(instanceId)) {
                    log.debug(String.format("【%-50s->排除服务成功    ：%s】", path, instanceId));
                    continue;
                }

                // 排除默认指定的路由实例名
                if (defultExcludeInstanceIds.get() != null && defultExcludeInstanceIds.get().contains(instanceId)) {
                    log.debug(String.format("【%-50s->排除默认服务成功：%s】", path, instanceId));
                    continue;
                }
                // 匹配IP
                if (ipAddrs.get() != null && ipAddrs.get().contains(ipAddr)) {
                    log.debug(String.format("【%-50s->匹配IP成功     ：%s】", path, ipAddr));
                    shutdownHystrixRequestContext();
                    return server;
                }

                Map<String, String> metadata = instanceInfo.getMetadata();
                String strWeight = metadata.get(META_DATA_KEY_WEIGHT);

                int weight = 100;
                try {
                    weight = Integer.parseInt(strWeight);
                } catch (Exception e) {
                    // 无需处理
                }

                //剔除0权重节点
                if (weight <= 0) {
                    log.debug(String.format("【%-50s->剔除0权重服务：%s】", path, instanceId));
                    continue;
                }

                serverWeightMap.put(server, weight);
                totalWeight += weight;
            }

            shutdownHystrixRequestContext();
        } else {
            for (Server server : serverList) {
                InstanceInfo instanceInfo = ((DiscoveryEnabledServer) server).getInstanceInfo();
                String instanceId = instanceInfo.getInstanceId();//待匹配的服务实例名
                Map<String, String> metadata = instanceInfo.getMetadata();
                String strWeight = metadata.get(META_DATA_KEY_WEIGHT);

                int weight = 100;
                try {
                    weight = Integer.parseInt(strWeight);
                } catch (Exception e) {
                    // 无需处理
                }

                //剔除0权重节点
                if (weight <= 0) {
                    log.debug(String.format("【%-50s->剔除0权重服务：%s】", path, instanceId));
                    continue;
                }
                serverWeightMap.put(server, weight);
                totalWeight += weight;
            }
        }

        // 权重随机
        int randomWight = this.random.nextInt(totalWeight);
        int current = 0;
        for (Map.Entry<Server, Integer> entry : serverWeightMap.entrySet()) {
            current += entry.getValue();
            if (randomWight <= current) {
                if (!StringUtils.isEmpty(path))
                    log.debug(String.format("【%-50s->权重随机        ：%s】", path, entry.getKey().getId()));
                return entry.getKey();
            }
        }
        return null;
    }

    public void inti() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        if (request != null) {
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
            initHystrixRequestContext(aInstanceId, hInstanceId, defultInstanceId, excludeInstanceId, defultExcludeInstanceId, ipAddr);
            // 传递给后续微服务
            if (ctx != null && HystrixRequestContext.isCurrentThreadInitialized()) {
                if (instanceIds.get() != null)
                    ctx.addZuulRequestHeader(INSTANCE_ID, Arrays.toString(instanceIds.get().toArray()));
                if (defultInstanceIds.get() != null)
                    ctx.addZuulRequestHeader(DEFULT_INSTANCE_ID, Arrays.toString(defultInstanceIds.get().toArray()));
                if (excludeInstanceIds.get() != null)
                    ctx.addZuulRequestHeader(EXCLUDE_INSTANCE_ID, Arrays.toString(excludeInstanceIds.get().toArray()));
                if (defultExcludeInstanceIds.get() != null)
                    ctx.addZuulRequestHeader(DEFULT_EXCLUDE_INSTANCE_ID, Arrays.toString(defultExcludeInstanceIds.get().toArray()));
                if (ipAddr != null && ipAddr.length() > 0)
                    ctx.addZuulRequestHeader(IP_ADDR, ipAddr); // 传递给后续微服务
            }
        }
    }

    public void initHystrixRequestContext(String aInstanceId, String hInstanceId, String defultInstanceId, String excludeInstanceId, String defultExcludeInstanceId, String ip) {
        if (!HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.initializeContext();
        }

        List<String> instanceIdList = new ArrayList<>();
        if (!StringUtils.isEmpty(aInstanceId)) {
            instanceIdList.addAll(Arrays.asList(aInstanceId.split(SPLIT)));
        }

        if (!StringUtils.isEmpty(hInstanceId)) {
            instanceIdList.addAll(Arrays.asList(hInstanceId.split(SPLIT)));
        }


        if (instanceIdList.size() > 0) {
            instanceIds.set(instanceIdList);
        } else {
            instanceIds.set(Collections.emptyList());
        }

        if (!StringUtils.isEmpty(defultInstanceId)) {
            defultInstanceIds.set(Arrays.asList(defultInstanceId.split(SPLIT)));
        } else {
            defultInstanceIds.set(Collections.emptyList());
        }

        if (!StringUtils.isEmpty(excludeInstanceId)) {
            excludeInstanceIds.set(Arrays.asList(excludeInstanceId.split(SPLIT)));
        } else {
            excludeInstanceIds.set(Collections.emptyList());
        }

        if (!StringUtils.isEmpty(defultExcludeInstanceId)) {
            defultExcludeInstanceIds.set(Arrays.asList(defultExcludeInstanceId.split(SPLIT)));
        } else {
            defultExcludeInstanceIds.set(Collections.emptyList());
        }

        if (!StringUtils.isEmpty(ip)) {
            ipAddrs.set(Arrays.asList(ip.split(SPLIT)));
        } else {
            ipAddrs.set(Collections.emptyList());
        }
    }

    public void initHystrixRequestContext(String defultInstanceId, String defultExcludeInstanceId) {
        if (!HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.initializeContext();
        }


        if (!StringUtils.isEmpty(defultInstanceId)) {
            defultInstanceIds.set(Arrays.asList(defultInstanceId.split(SPLIT)));
        } else {
            defultInstanceIds.set(Collections.emptyList());
        }

        if (!StringUtils.isEmpty(defultExcludeInstanceId)) {
            defultExcludeInstanceIds.set(Arrays.asList(defultExcludeInstanceId.split(SPLIT)));
        } else {
            defultExcludeInstanceIds.set(Collections.emptyList());
        }
    }

    public void shutdownHystrixRequestContext() {
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.getContextForCurrentThread().shutdown();
        }
    }

    public String getIpAddress(HttpServletRequest request) {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址

        String ip = request.getHeader("X-Forwarded-For");
//        if (log.isInfoEnabled()) {
//            log.info("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);
//        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
//                if (log.isInfoEnabled()) {
//                    log.info("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);
//                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
//                if (log.isInfoEnabled()) {
//                    log.info("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
//                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
//                if (log.isInfoEnabled()) {
//                    log.info("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);
//                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
//                if (log.isInfoEnabled()) {
//                    log.info("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);
//                }
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
//                if (log.isInfoEnabled()) {
//                    log.info("getIpAddress(HttpServletRequest) - getLocalHost - String ip=" + ip + " - getRemoteAddr " + request.getRemoteAddr());
//                }
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
