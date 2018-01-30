/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: RoleResResource.java
 * Author:   谭荣巧
 * Date:     2018/1/25 15:54
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.resource;

import com.ht.ussp.uc.app.domain.HtBoaInResource;
import com.ht.ussp.uc.app.service.HtBoaInAppService;
import com.ht.ussp.uc.app.service.HtBoaInResourceService;
import com.ht.ussp.uc.app.vo.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色资源接口类<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/1/25 15:54
 */
@RestController
@RequestMapping(value = "/role/res")
@Log4j2
public class RoleResResource {
    @Autowired
    private HtBoaInResourceService htBoaInResourceService;
    @Autowired
    private HtBoaInAppService htBoaInAppService;

    /**
     * 加载系统角色树数据<br>
     *
     * @author 谭荣巧
     * @Date 2018/1/25 15:56
     */
    @ApiOperation(value = "加载系统角色树数据")
    @PostMapping("/app/load")
    public List<AppAndRoleVo> loadAppAndRoleTreeData() {
        return htBoaInAppService.loadAppAndRoleVoList();
    }

    @ApiOperation(value = "加载资源树数据")
    @RequestMapping("/auth/load")
    public ResourceTreePageVo loadByRole() {
        ResourceTreePageVo page = new ResourceTreePageVo();
        List<ResourceTreeVo> rtvList = new ArrayList<>();
        List<HtBoaInResource> allList = htBoaInResourceService.getByAppAndStatusAndDelFlag("UC", "0", 0);
        List<HtBoaInResource> menuList = allList.stream().filter(res -> "group".equals(res.getResType()) || "view".equals(res.getResType())).collect(Collectors.toList());
        ResourceTreeVo rtv;
        List<ResourceTreeItemVo> btnRtv;
        List<ResourceTreeItemVo> tabRtv;
        for (HtBoaInResource menu : menuList) {
            rtv = new ResourceTreeVo();
            rtv.setApp(menu.getApp());
            rtv.setCode(menu.getResCode());
            rtv.setName(menu.getResName());
            rtv.setNameCn(menu.getResNameCn());
            rtv.setParentCode(menu.getResParent());
            btnRtv = new ArrayList<>();
            tabRtv = new ArrayList<>();
            if ("view".equals(menu.getResType())) {
                List<HtBoaInResource> btnResourceList = allList.stream().filter(btn -> "btn".equals(btn.getResType()) && menu.getResCode().equals(btn.getResParent())).collect(Collectors.toList());
                List<HtBoaInResource> tabResourceList = allList.stream().filter(tab -> "tab".equals(tab.getResType()) && menu.getResCode().equals(tab.getResParent())).collect(Collectors.toList());
                for (HtBoaInResource btn : btnResourceList) {
                    btnRtv.add(new ResourceTreeItemVo(btn.getResCode(), btn.getResName(), btn.getResNameCn(), false));
                }
                for (HtBoaInResource tab : tabResourceList) {
                    tabRtv.add(new ResourceTreeItemVo(tab.getResCode(), tab.getResName(), tab.getResNameCn(), false));
                }
            }
            rtv.setBtns(btnRtv);
            rtv.setTabs(tabRtv);
            rtvList.add(rtv);
        }
        page.setTotal(rtvList.size());
        page.setRows(rtvList);
        page.setFlag(true);
        return page;
    }
}
