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
import com.ht.ussp.core.Result;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
 
/**
 * 业务组织机构
 * @author tangxs
 *
 */
@Log4j2
@ControllerAdvice
public class BusinessHelper {
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
     * 根据用户id,手机号，用户姓名，email获取用户机构信息
     * 通过userid,email,mobile,jobnumber查询
     * @param keyword
     * @return
     */
    public Result getUserOrg( String keyword) {
        if (ucClient == null) {
            log.warn("可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        try { 
            return ucClient.getUserOrg(keyword);
        } catch (Exception ex) {
            return Result.buildFailConvert(ex.getLocalizedMessage(), ex.getMessage());
        }
    }
    
    /**
     * 获取所有下级机构信息 
     * @param orgCode 查询指定orgCode下所有子节点，若传空字符串则查询所有机构信息
     * @param busiType 传值1或者2 (1-UC行政机构  2-业务机构)
     * @param keyword 当keyword不为空 orgCode为空，则在所有机构中模糊查询机构名称，机构编码;orgCode不为空则查询所有下级机构
     * @return
     */
    public Result getSubOrgInfoByCode(String orgCode,String busiType,String keyword) {
        if (ucClient == null) {
            log.warn("无法通过机构编码获取下级机构信息，可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        try { 
            return ucClient.getOrgInfo(orgCode, busiType, keyword);
        } catch (Exception ex) {
            log.error("通过机构编码获取下级机构信息发生异常。", ex);
            return Result.buildFailConvert(ex.getLocalizedMessage(), ex.getMessage());
        }
    }
    
     
    /**
     * 根据用户id,手机号，用户姓名，email获取用户角色信息
     * 通过userid,email,mobile,jobnumber查询
     * @param keyword
     * @return
     */
    public Result getBusiUserRole( String keyword) {
        if (ucClient == null) {
            log.warn("可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        try { 
            return ucClient.getBusiUserRole(keyword);
        } catch (Exception ex) {
            return Result.buildFailConvert(ex.getLocalizedMessage(), ex.getMessage());
        }
    }
    
    /**
     * 根据用户id,手机号，用户姓名，email获取用户岗位信息
     * 通过userid,email,mobile,jobnumber查询
     * @param keyword
     * @return
     */
    public Result getBusiUserPosition( String keyword) {
        if (ucClient == null) {
            log.warn("可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        try { 
            return ucClient.getBusiUserPosition(keyword);
        } catch (Exception ex) {
            return Result.buildFailConvert(ex.getLocalizedMessage(), ex.getMessage());
        }
    }
    
    /**
     * 获取业务机构
     * @param orgLevel 20 公司层级  40 片区层级  60 分公司层级  80 部门层级  100 小组层级
     * @param busiOrgCode 传空则查询所有指定orgLevel业务机构信息,如果不为空则查询下级指定的orgLevel
     * @return
     */
    public Result getBusiOrgList( String orgLevel,String busiOrgCode) {
        if (ucClient == null) {
            log.warn("可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        try { 
            return ucClient.getBusiOrgList(orgLevel, busiOrgCode);
        } catch (Exception ex) {
            return Result.buildFailConvert(ex.getLocalizedMessage(), ex.getMessage());
        }
    }
 
}
