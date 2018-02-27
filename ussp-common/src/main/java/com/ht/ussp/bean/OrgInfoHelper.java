/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: LoginUserInfoHelper.java
 * Author:   谭荣巧
 * Date:     2018/2/6 17:32
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.bean;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import com.ht.ussp.client.UCClient;
import com.ht.ussp.client.dto.HtBoaInOrgDto;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * 机构信息<br>
 *
 * @author 谭荣巧
 * @Date 2018/2/6 17:32
 */
@Log4j2
@ControllerAdvice
public class OrgInfoHelper {
    @Getter
    private String userId;
    
    @Getter
    private String app;

    @Autowired(required = false)
    private UCClient ucClient;

    @ModelAttribute
    public void intLogin(@RequestHeader(value = "app", required = false) String app) {
        this.app = app;
    }
 
    @ModelAttribute
    public void intLogin2(@RequestHeader(value = "userId", required = false) String userId) {
        this.userId = userId;
    }

    /**
     * 通过机构编码获取下级机构信息<br>
     *
     * @author 谭荣巧
     * @Date 2018/1/31 10:41
     */
    public List<HtBoaInOrgDto> getSubOrgInfoByCode(String parentOrgCode) {
        if (ucClient == null) {
            log.warn("无法通过机构编码获取下级机构信息，可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        try {
            return ucClient.getSubOrgInfoByCode(parentOrgCode);
        } catch (Exception ex) {
            log.error("通过机构编码获取下级机构信息发生异常。", ex);
            return null;
        }
    }
    
    /**
     * 通过机构编码获取机构信息
     * @return
     */
    public HtBoaInOrgDto getOrgInfoByCode(String orgCode) {
        if (ucClient == null) {
            log.warn("无法通过机构编码获取机构信息，可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        try {
            return ucClient.getOrgInfoByCode(orgCode);
        } catch (Exception ex) {
            log.error("通过机构编码获取机构信息发生异常。", ex);
            return null;
        }
    }
     
}
