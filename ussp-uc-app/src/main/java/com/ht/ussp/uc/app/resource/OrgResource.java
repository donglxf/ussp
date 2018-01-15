/*
 * Copyright (C), 2017-2018 广东鸿特信息咨询有限公司
 * FileName: OrgResource.java
 * Author:   谭荣巧
 * Date:     2018/1/13 10:33
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号             描述
 */
package com.ht.ussp.uc.app.resource;

import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.service.HtBoaInOrgService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 组织机构资源类<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/1/13 10:33
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/org")
@Log4j2
public class OrgResource {
    @Autowired
    private HtBoaInOrgService htBoaInOrgService;

    /**
     * 根据父机构编码获取组织机构树<br>
     *
     * @param parenOrgCode 父组织机构代码
     * @return zTree数据结构
     * @author 谭荣巧
     * @Date 2018/1/13 10:47
     */
    @PostMapping("/tree")
    public List<HtBoaInOrg> getOrgTreeList(String parenOrgCode) {
        return htBoaInOrgService.getOrgTreeList(parenOrgCode);
    }
}
