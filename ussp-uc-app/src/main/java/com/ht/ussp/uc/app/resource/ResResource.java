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

import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.service.HtBoaInAppService;
import com.ht.ussp.uc.app.service.HtBoaInResourceService;
import com.ht.ussp.uc.app.vo.AppAndResourceVo;
import com.ht.ussp.uc.app.vo.PageVo;
import com.ht.ussp.uc.app.vo.UserMessageVo;
import com.ht.ussp.util.EncryptUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 资源操作类<br>
 * <br>
 *
 * @author 谭荣巧
 * @Date 2018/1/18 21:40
 */
@RestController
@CrossOrigin(origins = "*")
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
    @PostMapping(value = "/loadListByPage")
    public PageResult<List<UserMessageVo>> loadListByPage(PageVo page) {
        return htBoaInResourceService.getPage(new PageRequest(page.getPage(), page.getLimit()), page.getOrgCode(),
                page.getKeyWord(), "");
    }

    /**
     * 新增用户信息<br>
     *
     * @param user 用户信息数据对象
     * @return com.ht.ussp.core.Result
     * @author 谭荣巧
     * @Date 2018/1/14 12:08
     */
    @PostMapping(value = "/add")
    public Result addAsync(@RequestBody HtBoaInUser user) {
        if (user != null) {
            String userId = UUID.randomUUID().toString().replace("-", "");
            user.setUserId(userId);
            // TODO 需要获取登录信息，设置创建人，修改人
            user.setCreateOperator("10003");
            user.setUpdateOperator("10003");
            user.setDelFlag(0);
            HtBoaInLogin loginInfo = new HtBoaInLogin();
            loginInfo.setLoginId(UUID.randomUUID().toString().replace("-", ""));
            loginInfo.setUserId(userId);
            // TODO 需要获取登录信息，设置创建人，修改人
            loginInfo.setCreateOperator("10003");
            loginInfo.setUpdateOperator("10003");
            loginInfo.setStatus("0");
            loginInfo.setPassword(EncryptUtil.passwordEncrypt("123456"));
            loginInfo.setFailedCount(0);
            loginInfo.setRootOrgCode(user.getRootOrgCode());
            loginInfo.setDelFlag(0);
          //  boolean isAdd = htBoaInUserService.saveUserInfoAndLoginInfo(user, loginInfo);
           // if (isAdd) {
                return Result.buildSuccess();
           // }
        }
        return Result.buildFail();
    }

    @PostMapping("/delete/{userId}")
    public Result delAsync(@PathVariable String userId) {
//        if (userId != null && !"".equals(userId.trim())) {
//            boolean isDel = htBoaInUserService.setDelFlagByUserId(userId);
//            if (isDel) {
//                return Result.buildSuccess();
//            }
//        }
        return Result.buildFail();
    }

    @PostMapping("/view/{userId}")
    public Result viewAsync(@PathVariable String userId) {
//        if (userId != null && !"".equals(userId.trim())) {
//            return Result.buildSuccess(htBoaInUserService.getUserByUserId(userId));
//        }
        return Result.buildFail();
    }

    @PostMapping("/update")
    public Result updateAsync(@RequestBody HtBoaInUser user) {
//        if (user != null) {
//            // TODO 需要获取登录信息，设置修改人
//            user.setUpdateOperator("测试人");
//            boolean isUpdate = htBoaInUserService.updateUserByUserId(user);
//            if (isUpdate) {
//                return Result.buildSuccess();
//            }
//        }
        return Result.buildFail();
    }

}
