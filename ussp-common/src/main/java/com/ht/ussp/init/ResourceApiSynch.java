/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: ResourceApiSynch.java
 * Author:   谭荣巧
 * Date:     2018/1/18 9:43
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.init;

import com.ht.ussp.client.UCClient;
import com.ht.ussp.client.dto.ApiResourceDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

/**
 * API资源同步类<br>
 * 用于自动同步微服务中包含的API地址<br>
 * 开启同步，需要在微服务配置文件中（application.yml或者bootstrap.yml）增加ht.config.auth.api.synch: true参数，
 * 并且还需配置扫描的包ht.config.auth.api.packages，如果不配置该参数，将扫描所有的类<br>
 *
 * @author 谭荣巧
 * @Date 2018/1/18 9:43
 */
@Log4j2
@Component
public class ResourceApiSynch {

    @Autowired(required = false)
    private UCClient ucClient;

    @Autowired
    public ResourceApiSynch(ApplicationContext applicationContext, @Value("${ht.config.uc.api.synch:false}") boolean synch_api, @Value("${ht.config.uc.api.packages:com.ht}") String packages, @Value("${ht.config.uc.api.app:}") String app) {
        System.out.println("是否同步：" + synch_api + "\t" + packages + "\t" + app);
        if (synch_api) {
            if (StringUtils.isEmpty(app)) {
                log.warn("同步API资源到用户权限中心，需要指定API资源所属系统，否则无法同步，请通过ht.config.uc.api.app配置。");
            } else {
                initResourceApiSynchThread(applicationContext, packages, app, false);
            }
        }
    }

    private boolean initResourceApiSynchThread(ApplicationContext applicationContext, String packages, String app, boolean isSynchronized) {
        if (applicationContext != null) {
            if (isSynchronized) {
                new ResourceApiAynchThread().init(applicationContext, packages, app).run();
            } else {
                new Thread(new ResourceApiAynchThread().init(applicationContext, packages, app)).start();
            }
            return true;
        }
        return false;
    }

    class ResourceApiAynchThread implements Runnable {

        private ApplicationContext applicationContext;
        //扫描的包
        private String packages;
        //系统编号
        private String app;

        @Override
        public void run() {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
            }
            initHandler();
        }

        /**
         * 判断是否扫描通过<br>
         *
         * @param scanPackage
         * @return boolean
         * @author 谭荣巧
         * @Date 2018/1/18 9:39
         */
        private boolean isScanPass(String scanPackage) {
            if (packages != null && packages.trim().length() > 0) {
                String[] ps = packages.split(",");
                for (String pk : ps) {
                    if (scanPackage.startsWith(pk)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private void initHandler() {
            if (log.isDebugEnabled()) {
                log.debug("开启API资源同步...");
            }
            ApiResourceDto apiDto = new ApiResourceDto();
            Map<String, Object> result = applicationContext.getBeansWithAnnotation(Controller.class);
            for (Iterator<String> it = result.keySet().iterator(); it.hasNext(); ) {

                String key = it.next();
                String aPackage = result.get(key).getClass().getPackage().getName();
                String tmpClazz = result.get(key).getClass().getName();
                // 判断是否扫描通过（只扫描packages中的包,如果packages为空，则扫描所有包含com.ht的包）
                if (!isScanPass(aPackage)) {
                    continue;
                }

                GetMapping controllerGetMapping = AnnotatedElementUtils.findMergedAnnotation(result.get(key).getClass(), GetMapping.class);
                PostMapping controllerPostMapping = AnnotatedElementUtils.findMergedAnnotation(result.get(key).getClass(), PostMapping.class);
                RequestMapping controllerMapping = AnnotatedElementUtils.findMergedAnnotation(result.get(key).getClass(), RequestMapping.class);
                String clasz;
                String mapping;
                if (controllerMapping != null && controllerMapping.value().length > 0) {
                    clasz = tmpClazz.substring(0, tmpClazz.indexOf("$") > -1 ? tmpClazz.indexOf("$") : tmpClazz.length());
                    mapping = StringUtils.arrayToCommaDelimitedString(controllerMapping.value());
                } else if (controllerPostMapping != null && controllerPostMapping.value().length > 0) {
                    clasz = tmpClazz.substring(0, tmpClazz.indexOf("$") > -1 ? tmpClazz.indexOf("$") : tmpClazz.length());
                    mapping = StringUtils.arrayToCommaDelimitedString(controllerPostMapping.value());
                } else if (controllerGetMapping != null && controllerGetMapping.value().length > 0) {
                    clasz = tmpClazz.substring(0, tmpClazz.indexOf("$") > -1 ? tmpClazz.indexOf("$") : tmpClazz.length());
                    mapping = StringUtils.arrayToCommaDelimitedString(controllerGetMapping.value());
                } else {
                    clasz = tmpClazz.substring(0, tmpClazz.indexOf("$") > -1 ? tmpClazz.indexOf("$") : tmpClazz.length());
                    mapping = "/";
                }
                Method[] methods = result.get(key).getClass().getMethods();
                for (Method m : methods) {
                    AnnotationAttributes annotationAttributes = AnnotatedElementUtils.getMergedAnnotationAttributes(m, "io.swagger.annotations.ApiOperation");
                    MultiValueMap<String, Object> map = AnnotatedElementUtils.getAllAnnotationAttributes(m, "io.swagger.annotations.ApiOperation");
                    RequestMapping rm = AnnotatedElementUtils.findMergedAnnotation(m, RequestMapping.class);
                    GetMapping gm = AnnotatedElementUtils.findMergedAnnotation(m, GetMapping.class);
                    PostMapping pm = AnnotatedElementUtils.findMergedAnnotation(m, PostMapping.class);
                    PutMapping pm2 = AnnotatedElementUtils.findMergedAnnotation(m, PutMapping.class);
                    DeleteMapping dm = AnnotatedElementUtils.findMergedAnnotation(m, DeleteMapping.class);
                    String method = m.toString().replace(tmpClazz, clasz).replace("final ", "");
                    String methodMapping;
                    String apiDescribe = annotationAttributes != null ? annotationAttributes.getString("value") : "";
                    if (rm != null) {
                        methodMapping = StringUtils.arrayToCommaDelimitedString(rm.value());
                    } else if (gm != null) {
                        methodMapping = StringUtils.arrayToCommaDelimitedString(gm.value());
                    } else if (pm != null) {
                        methodMapping = StringUtils.arrayToCommaDelimitedString(pm.value());
                    } else if (pm2 != null) {
                        methodMapping = StringUtils.arrayToCommaDelimitedString(pm2.value());
                    } else if (dm != null) {
                        methodMapping = StringUtils.arrayToCommaDelimitedString(dm.value());
                    } else {
                        continue;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("api描述:\t\t" + apiDescribe + "\t" + (map != null ? map.get("value") : ""));
                        log.debug("方法映射：\t\t" + mapping + methodMapping);
                        log.debug("方法名：\t\t" + method);
                    }
                    apiDto.add(mapping.concat(methodMapping), method, apiDescribe);
                }
            }
            if (apiDto != null && apiDto.getApiInfoList().size() > 0) {
                if (ucClient == null) {
                    log.warn("无法同步API资源到用户权限中心，可能没有启用Fegin组件，请启用后，加入basePackages = {\"com.ht.ussp.client\"}");
                } else {
                    apiDto.setApp(app);
                    try {
                        ucClient.resourceApiAynch(apiDto);
                    } catch (Exception e) {
                        log.warn("同步API资源发生异常，请尝试重启服务。异常：" + e.getMessage());
                    }
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("同步API资源完成，共计扫描到" + apiDto.getApiInfoList().size() + "个API。");
            }
        }

        public ResourceApiAynchThread init(ApplicationContext applicationContext, String packages, String app) {
            this.applicationContext = applicationContext;
            this.packages = packages;
            this.app = app;
            return this;
        }
    }
}
