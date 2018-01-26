/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: ResResource.java
 * Author:   谭荣巧
 * Date:     2018/1/18 21:40
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.resource;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInResource;
import com.ht.ussp.uc.app.service.HtBoaInAppService;
import com.ht.ussp.uc.app.service.HtBoaInResourceService;
import com.ht.ussp.uc.app.vo.AppAndResourceVo;
import com.ht.ussp.uc.app.vo.RelevanceApiVo;
import com.ht.ussp.uc.app.vo.ResourcePageVo;
import com.ht.ussp.uc.app.vo.UserMessageVo;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

/**
 * 资源操作类<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/1/18 21:40
 */
@RestController
@RequestMapping(value = "/resource")
@Log4j2
public class ResResource {
    @Autowired
    private HtBoaInResourceService htBoaInResourceService;
    @Autowired
    private HtBoaInAppService htBoaInAppService;


    @PostMapping("/app/load")
    public List<AppAndResourceVo> loadAppAndResourceTreeData() {
        return htBoaInAppService.loadAppAndResourceVoList();
    }

    /**
     * 用户信息分页查询<br>
     *
     * @param page 分页参数对象
     * @return 结果对象
     * @author 谭荣巧
     * @Date 2018/1/12 9:01
     */
    @ApiOperation(value = "用户信息分页查询")
    @PostMapping(value = "/page/load", produces = {"application/json"})
    public PageResult<List<UserMessageVo>> loadListByPage(ResourcePageVo page) {
        return htBoaInResourceService.getPage(page.getPageRequest(), page.getApp(), page.getParentCode(), page.getResType(), page.getKeyWord());
    }

    /**
     * 新增资源信息<br>
     *
     * @param resource 资源信息
     * @return com.ht.ussp.core.Result
     * @author 谭荣巧
     * @Date 2018/1/14 12:08
     */
    @PostMapping(value = "/add")
    public Result addAsync(@RequestBody HtBoaInResource resource) {
        if (resource != null) {
            if ("menu".equals(resource.getResType()) && StringUtils.isEmpty(resource.getResContent())) {
                resource.setResType("group");
            } else if ("menu".equals(resource.getResType())) {
                resource.setResType("view");
            }
            resource.setStatus("0");
            // TODO 需要获取登录信息，设置创建人，修改人
            resource.setCreateOperator("10003");
            resource.setUpdateOperator("10003");
            resource.setDelFlag(0);
            resource = htBoaInResourceService.save(resource);
            if (resource != null && !StringUtils.isEmpty(resource.getId())) {
                return Result.buildSuccess();
            }
        }
        return Result.buildFail();
    }

    @PostMapping("/delete")
    public Result delAsync(Long id) {
        HtBoaInResource resource = htBoaInResourceService.getOne(id);
        if (resource != null) {
            List<HtBoaInResource> resourceList = htBoaInResourceService.getByResParent(resource.getResCode());
            if (resourceList != null && resourceList.size() > 0) {
                return new Result().returnCode("10000").codeDesc("该资源存在下级资源，请先删除下级资源 。");
            }
            htBoaInResourceService.delete(id);
        }
        return Result.buildSuccess();
    }

    @PostMapping("/view")
    public Result viewAsync(Long id) {
        HtBoaInResource resource = htBoaInResourceService.getOne(id);
        if (resource != null) {
            return Result.buildSuccess(resource);
        }
        return Result.buildFail();
    }

    @PostMapping("/update")
    public Result updateAsync(@RequestBody HtBoaInResource resource) {
        if (resource != null) {
            if ("menu".equals(resource.getResType()) && StringUtils.isEmpty(resource.getResContent())) {
                resource.setResType("group");
            } else if ("menu".equals(resource.getResType())) {
                resource.setResType("view");
            }
            // TODO 需要获取登录信息，设置修改人
            resource.setUpdateOperator("测试人");
            resource = htBoaInResourceService.save(resource);
            if (resource != null) {
                return Result.buildSuccess();
            }
        }
        return Result.buildFail();
    }

    /**
     * 菜单关联API资源<br>
     *
     * @param relevanceApiVo 关联的api资源对象
     * @return 请求结果
     * @author 谭荣巧
     * @Date 2018/1/20 15:22
     */
    @PostMapping("/relevance")
    public Result relevance(@RequestBody RelevanceApiVo relevanceApiVo) {
        List<HtBoaInResource> newList = new ArrayList<>();
        List<HtBoaInResource> resourceList = relevanceApiVo.getResourceList();
        for (HtBoaInResource resource : resourceList) {
            if (!StringUtils.isEmpty(resource.getResParent())) {
                resource.setId(null);
            }
            resource.setResParent(relevanceApiVo.getParentCode());
            newList.add(resource);
        }
        List<HtBoaInResource> resultList = htBoaInResourceService.save(newList);
        if (resultList != null && resultList.size() > 0) {
            return Result.buildSuccess();
        }
        return Result.buildFail();
    }

}
