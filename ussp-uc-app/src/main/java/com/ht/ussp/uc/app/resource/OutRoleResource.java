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

import com.ht.ussp.uc.app.domain.HtBoaOutRole;
import com.ht.ussp.uc.app.model.BoaOutRoleInfo;
import com.ht.ussp.uc.app.model.Codes;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaOutRoleService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @ClassName: HtBoaOutRoleResource
 * @Description: 角色信息
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月12日 下午02:06:13
 */

@RestController
@RequestMapping(value = "/role")
public class OutRoleResource {

    private static final Logger log = LoggerFactory
            .getLogger(OutRoleResource.class);

    @Autowired
    private HtBoaOutRoleService htBoaOutRoleService;
    
    @ApiOperation(value = "对外：角色记录查询", notes = "列出所有角色记录列表信息")
    @ApiImplicitParam(name = "pageConf", value = "分页信息实体", required = true, dataType = "PageConf")
    @RequestMapping(value = {
            "/out/list" }, method = RequestMethod.POST)
    public ResponseModal list(@RequestBody PageConf pageConf) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "角色记录查询：role/out/list param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "pageConf: " + pageConf, sl);
        Object o = htBoaOutRoleService.findAllByPage(pageConf);
        el = System.currentTimeMillis();
        log.info(logEnd, "pageConf: " + pageConf, msg, el, el - sl);
        return new ResponseModal(200, msg, o);
    }
    
    @ApiOperation(value = "对外：新增/编辑角色记录", notes = "提交角色基础信息新增/编辑角色")
    @ApiImplicitParam(name = "BoaOutRoleInfo", value = "角色信息实体", required = true, dataType = "BoaOutRoleInfo")
    @RequestMapping(value = {
            "/out/add" }, method = RequestMethod.POST)
    public ResponseModal add(@RequestBody BoaOutRoleInfo BoaOutRoleInfo) {
        long sl = System.currentTimeMillis(), el = 0L;
        ResponseModal r = null;
        String msg = "成功";
        String logHead = "角色记录查询：role/out/add param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "BoaOutRoleInfo: " + BoaOutRoleInfo, sl);
        HtBoaOutRole u = null;
        if (null != BoaOutRoleInfo.getRoleCode()
                && 0 < BoaOutRoleInfo.getRoleCode().trim().length()) {
            Set<String> codes = new HashSet<String>(0);
            codes.add(BoaOutRoleInfo.getRoleCode().trim());
            List<HtBoaOutRole> list = htBoaOutRoleService
                    .findByRoleCodeIn(codes);
            r = exceptionReturn(logEnd,
                    "BoaOutRoleInfo: " + BoaOutRoleInfo, list, sl, "角色信息",
                    1);
            if (null != r)
                return r;
            u = list.get(0);
        } else {
            u = new HtBoaOutRole();
        }
        u.setCreatedDatetime(new Date());
        u.setLastModifiedDatetime(new Date());
        u.setRoleName(BoaOutRoleInfo.getRoleName());
        u.setRoleNameCn(BoaOutRoleInfo.getRoleNameCn());
        u.setStatus(BoaOutRoleInfo.getStatus());
        if (null == u.getRoleCode())
            u = htBoaOutRoleService.add(u);
        else
            u = htBoaOutRoleService.update(u);
        el = System.currentTimeMillis();
        log.info(logEnd, "BoaOutRoleInfo: " + BoaOutRoleInfo, msg, el, el - sl);
        return new ResponseModal(200, msg, u);
    }
    
    @ApiOperation(value = "对外：删除角色记录", notes = "提交角色编号，可批量删除")
    @ApiImplicitParam(name = "codes", value = "角色编号集", required = true, dataType = "Codes")
    @RequestMapping(value = {
            "/out/delete" }, method = RequestMethod.DELETE)
    public ResponseModal delete(@RequestBody Codes codes) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "角色记录删除：role/out/delete param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.info(logStart, "codes: " + codes, sl);
        htBoaOutRoleService.delete(codes.getCodes());
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
