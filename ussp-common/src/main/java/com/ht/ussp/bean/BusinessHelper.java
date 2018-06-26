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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import com.ht.ussp.client.UCClient;
import com.ht.ussp.common.OrgBusiTypeEnum;
import com.ht.ussp.core.Result;
import com.ht.ussp.util.JsonUtil;

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
     * @return Result<String>
     */
    public Result<String> getUserOrg( String keyword) {
        if (ucClient == null) {
            log.warn("可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        keyword = keyword==null?"":keyword;
        try { 
            Result result = ucClient.getUserOrg(keyword);
        	if(result!=null) {
        		if(result.getData()!=null) {
        			return Result.buildSuccess(JsonUtil.obj2Str(result.getData()));
        		}
        	}else {
        		return Result.buildFailConvert("9999", "null");
        	}
        } catch (Exception ex) {
            return Result.buildFailConvert(ex.getLocalizedMessage(), ex.getMessage());
        }
        return Result.buildSuccess();
    }
    
    /**
     * 获取所有下级机构信息 
     * @param orgCode 查询指定orgCode下所有子节点，若传空字符串则查询所有机构信息
     * @param busiType 传值1或者2 (1-UC行政机构  2-业务机构)
     * @param keyword 当keyword不为空 orgCode为空，则在所有机构中模糊查询机构名称，机构编码;orgCode不为空则查询所有下级机构
     * @return Result<String>
     */
    public Result<String> getSubOrgInfoByCode(String orgCode,String busiType,String keyword) {
        if (ucClient == null) {
            log.warn("无法通过机构编码获取下级机构信息，可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        if(busiType==null || "".equals(busiType)) {
        	return Result.buildFailConvert("9999", "busiType is null");
        }
        orgCode = orgCode==null?"":orgCode;
        keyword = keyword==null?"":keyword;
        try { 
            Result result = ucClient.getOrgInfo(orgCode, busiType, keyword);
        	if(result!=null) {
        		if(result.getData()!=null) {
        			return Result.buildSuccess(JsonUtil.obj2Str(result.getData()));
        		}
        	}else {
        		return Result.buildFailConvert("9999", "null");
        	}
        } catch (Exception ex) {
            return Result.buildFailConvert(ex.getLocalizedMessage(), ex.getMessage());
        }
        return Result.buildSuccess();
    }
    
     
    /**
     * 根据用户id,手机号，用户姓名，email获取用户角色信息
     * 通过userid,email,mobile,jobnumber查询
     * @param keyword Result<String>
     * @return
     */
    public Result<String> getBusiUserRole( String keyword) {
        if (ucClient == null) {
            log.warn("可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        keyword = keyword==null?"":keyword;
        try { 
            Result result = ucClient.getBusiUserRole(keyword);
        	if(result!=null) {
        		if(result.getData()!=null) {
        			return Result.buildSuccess(JsonUtil.obj2Str(result.getData()));
        		}
        	}else {
        		return Result.buildFailConvert("9999", "null");
        	}
        } catch (Exception ex) {
            return Result.buildFailConvert(ex.getLocalizedMessage(), ex.getMessage());
        }
        return Result.buildSuccess();
    }
    
    /**
     * 根据用户id,手机号，用户姓名，email获取用户岗位信息
     * 通过userid,email,mobile,jobnumber查询
     * @param keyword Result<String>
     * @return
     */
    public Result<String> getBusiUserPosition( String keyword) {
        if (ucClient == null) {
            log.warn("可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        keyword = keyword==null?"":keyword;
        try { 
            Result result = ucClient.getBusiUserPosition(keyword);
        	if(result!=null) {
        		if(result.getData()!=null) {
        			return Result.buildSuccess(JsonUtil.obj2Str(result.getData()));
        		}
        	}else {
        		return Result.buildFailConvert("9999", "null");
        	}
        } catch (Exception ex) {
            return Result.buildFailConvert(ex.getLocalizedMessage(), ex.getMessage());
        }
        return Result.buildSuccess();
    }
    
    /**
     * 获取业务机构
     * @param orgLevel 20 公司层级  40 片区层级  60 分公司层级  80 部门层级  100 小组层级
     * @param busiOrgCode 传空则查询所有指定orgLevel业务机构信息,如果不为空则查询下级指定的orgLevel
     * @return Result<String>
     */
    public Result<String> getBusiOrgList( String orgLevel,String busiOrgCode) {
        if (ucClient == null) {
            log.warn("可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        orgLevel = orgLevel==null?"":orgLevel;
        busiOrgCode = busiOrgCode==null?"":busiOrgCode;
        try { 
        	Result result = ucClient.getBusiOrgList(orgLevel, busiOrgCode);
        	if(result!=null) {
        		if(result.getData()!=null) {
        			return Result.buildSuccess(JsonUtil.obj2Str(result.getData()));
        		}
        	}else {
        		return Result.buildFailConvert("9999", "null");
        	}
        } catch (Exception ex) {
            return Result.buildFailConvert(ex.getLocalizedMessage(), ex.getMessage());
        }
        return Result.buildSuccess();
    }
 
    /**
     * 查询角色下所有用户 
     * @param roleCode
     * @param keyword
     * @return
     */
    public Result<String> getUserInfoForRole(String roleCode,String keyword) {
        if (ucClient == null) {
            log.warn("可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        roleCode = roleCode==null?"":roleCode;
        keyword = keyword==null?"":keyword;
        try { 
        	Result result = ucClient.getUserInfoForRole(roleCode, keyword);
        	if(result!=null) {
        		if(result.getData()!=null) {
        			return Result.buildSuccess(JsonUtil.obj2Str(result.getData()));
        		}
        	}else {
        		return Result.buildFailConvert("9999", "null");
        	}
        } catch (Exception ex) {
            return Result.buildFailConvert(ex.getLocalizedMessage(), ex.getMessage());
        }
        return Result.buildSuccess();
    }
    
    /**
     * 获取指定业务机构下用户列表 getUserListByBusiOrg
     * @param busiOrgCode 查询指定业务机构下用户
     * @param isAllSub 传值（1，2） 1:只当前机构下所有用户  2:包括机构下以及所有子机构用户
     * @return
     */
    public Result<String> getUserListByBusiOrg(String busiOrgCode,String isAllSub) {
        if (ucClient == null) {
            log.warn("可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        busiOrgCode = busiOrgCode==null?"":busiOrgCode;
        isAllSub = isAllSub==null?"":isAllSub;
        try { 
        	Result result = ucClient.getUserBusiListByBusiOrgCode(busiOrgCode, isAllSub);
        	if(result!=null) {
        		if(result.getData()!=null) {
        			return Result.buildSuccess(JsonUtil.obj2Str(result.getData()));
        		}
        	}else {
        		return Result.buildFailConvert("9999", "null");
        	}
        } catch (Exception ex) {
            return Result.buildFailConvert(ex.getLocalizedMessage(), ex.getMessage());
        }
        return Result.buildSuccess();
    }
    
    /**
     * 获取指定业务机构所属的 片区，分公司 (20-公司层级  40-片区层级  60-分公司层级  80-部门层级  100-小组层级)
     *  资源类型枚举值： OrgBusiTypeEnum
     * @return
     */
    public Result<String> getBusiOrgInfoByOrgType(String orgCode,OrgBusiTypeEnum orgType) {
        if (ucClient == null) {
            log.warn("无法获取用户所属机构类型信息，可能没有启用Fegin组件，启用后，请在@EnableFeignClients加入basePackages = {\"com.ht.ussp.client\"}");
        }
        try { 
        	Result result = ucClient.getBusiOrgInfoByOrgType(orgCode,orgType.getReturnCode());
        	if(result!=null) {
        		if(result.getData()!=null) {
        			return Result.buildSuccess(JsonUtil.obj2Str(result.getData()));
        		}
        	}else {
        		return Result.buildFailConvert("9999", "null");
        	}
        } catch (Exception ex) {
            return Result.buildFailConvert(ex.getLocalizedMessage(), ex.getMessage());
        }
        return Result.buildSuccess();
    }
}
