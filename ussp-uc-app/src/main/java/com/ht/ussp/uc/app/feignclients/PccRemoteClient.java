package com.ht.ussp.uc.app.feignclients;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.vo.NextChildDto;


/**
 * 参数配置中心client
 * @author:喻尊龙
 * @date: 2018/6/11
 */
@FeignClient(name = "pcc-core")
public interface PccRemoteClient {

    /**
     * 获取所有省份信息
     * @param params
     * @return
     */
    @RequestMapping(value = "/biz/getCurrTypeValList",headers = {"app=SMS"},consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Result getProvince(@RequestBody Map<String,Object> params);

    @RequestMapping(value = "/biz/getNextChildValList",headers = {"app=SMS"},consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Result getNextChildres(@RequestBody NextChildDto dto);
}
