package com.ht.ussp.uc.app.feignclients;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 数据权限
 */
@FeignClient(value = "dss-app")
public interface DssClient {

 
    @RequestMapping(value = "/bu/conf/rule/list")
    public Map<String, Object> getRuleInfos(@RequestBody Map<String, Object> paramter);
 
    @RequestMapping(value = "/bu/conf/rule")
    public Map<String, Object> getRuleInfoByUserId(@RequestBody Map<String, Object> paramter);
}
