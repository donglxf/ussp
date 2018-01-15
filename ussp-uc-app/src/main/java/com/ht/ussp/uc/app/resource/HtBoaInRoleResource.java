package com.ht.ussp.uc.app.resource;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.uc.app.domain.HtBoaInRole;
import com.ht.ussp.uc.app.model.BoaInRoleInfo;
import com.ht.ussp.uc.app.model.Codes;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInRoleService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @ClassName: HtBoaInRoleResource
 * @Description: 角色信息
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月12日 下午02:06:13
 */

@RestController
@RequestMapping(value = "/role")
public class HtBoaInRoleResource {

    private static final Logger log = LoggerFactory
            .getLogger(HtBoaInRoleResource.class);

    @Autowired
    private HtBoaInRoleService htBoaInRoleService;
    
    @ApiOperation(value = "对内：角色记录查询", notes = "列出所有角色记录列表信息")
    @ApiImplicitParam(name = "pageConf", value = "分页信息实体", required = true, dataType = "PageConf")
    @RequestMapping(value = {
            "/in/list" }, method = RequestMethod.POST)
    public ResponseModal list(@RequestBody PageConf pageConf) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/list param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "pageConf: " + pageConf, sl);
        Object o = htBoaInRoleService.findAllByPage(pageConf);
        el = System.currentTimeMillis();
        log.info(logEnd, "pageConf: " + pageConf, msg, el, el - sl);
        return new ResponseModal(200, msg, o);
    }
    
    @ApiOperation(value = "对内：新增/编辑角色记录", notes = "提交角色基础信息新增/编辑角色")
    @ApiImplicitParam(name = "boaInRoleInfo", value = "角色信息实体", required = true, dataType = "BoaInRoleInfo")
    @RequestMapping(value = {
            "/in/add" }, method = RequestMethod.POST)
    public ResponseModal add(@RequestBody BoaInRoleInfo boaInRoleInfo) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "角色记录查询：role/in/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "boaInRoleInfo: " + boaInRoleInfo, sl);
        HtBoaInRole u = null;
        if (null != boaInRoleInfo.getRoleCode()
                && 0 < boaInRoleInfo.getRoleCode().trim().length()) {
            Set<String> codes = new HashSet<String>(0);
            codes.add(boaInRoleInfo.getRoleCode().trim());
            List<HtBoaInRole> list = htBoaInRoleService
                    .findByRoleCodeIn(codes);
            r = exceptionReturn(logEnd,
                    "boaInRoleInfo: " + boaInRoleInfo, list, sl, "角色信息",
                    1);
            if (null != r)
                return r;
            u = list.get(0);
        } else {
            u = new HtBoaInRole();
        }
        u.setCreatedDatetime(new Date());
        u.setLastModifiedDatetime(new Date());
        u.setRoleName(boaInRoleInfo.getRoleName());
        u.setRoleNameCn(boaInRoleInfo.getRoleNameCn());
        u.setRootOrgCode(boaInRoleInfo.getROrgCode());
        u.setStatus(boaInRoleInfo.getStatus());
        if (null == u.getRoleCode())
            u = htBoaInRoleService.add(u);
        else
            u = htBoaInRoleService.update(u);
        el = System.currentTimeMillis();
        log.info(logEnd, "boaInRoleInfo: " + boaInRoleInfo, msg, el, el - sl);
        return new ResponseModal(200, msg, u);
    }
    
    @ApiOperation(value = "对内：删除角色记录", notes = "提交角色编号，可批量删除")
    @ApiImplicitParam(name = "codes", value = "角色编号集", required = true, dataType = "Codes")
    @RequestMapping(value = {
            "/in/delete" }, method = RequestMethod.DELETE)
    public ResponseModal delete(@RequestBody Codes codes) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "角色记录删除：role/in/delete param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "codes: " + codes, sl);
        htBoaInRoleService.delete(codes.getCodes());
        el = System.currentTimeMillis();
        log.info(logEnd, "codes: " + codes, msg, el, el - sl);
        return new ResponseModal(200, msg);
    }

    protected ResponseModal exceptionReturn(String logEnd, String param,
            List<?> list, long sl, String exInfo, int row) {
        if (null == exInfo)
            exInfo = "";
        if (null == list || list.isEmpty()) {
            String msg = "无效参数，" + exInfo + "查无信息体";
            long el = System.currentTimeMillis();
            log.error(logEnd, param, msg, el, el - sl);
            return new ResponseModal(500, msg);
        } else if (row != list.size()) {
            String msg = "查询异常！查出" + exInfo + "记录数不符合要求";
            long el = System.currentTimeMillis();
            log.error(logEnd, param, msg, el, el - sl);
            return new ResponseModal(500, msg);
        }
        return null;
    }

}
