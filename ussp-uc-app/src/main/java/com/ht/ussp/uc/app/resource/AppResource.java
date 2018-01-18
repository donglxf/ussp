package com.ht.ussp.uc.app.resource;

import com.ht.ussp.core.Result;
import com.ht.ussp.uc.app.vo.AppAndAuthVo;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ht.ussp.uc.app.domain.HtBoaInApp;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInAppService;

import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInAppResource
 * @Description: 系统信息
 * @date 2018年1月5日 下午6:36:47
 */

@RestController
@RequestMapping(value = "/system")
@CrossOrigin(origins = "*")
public class AppResource {

    private static final Logger log = LoggerFactory.getLogger(EchoResouce.class);

    @Autowired
    private HtBoaInAppService htBoaInAppService;

    @ApiOperation("查询系统信息")
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
    public ResponseModal getHtBoaInApp(@PathVariable Long id) {
        ResponseModal rm = new ResponseModal();
        HtBoaInApp htBoaInApp = htBoaInAppService.findById(id);
        rm.setStatus_code(200);
        rm.setResult(htBoaInApp);
        log.info("====htBoaInApp====" + htBoaInApp);
        return rm;
    }

    @PostMapping("/appandauth/load")
    public List<AppAndAuthVo> loadAppAndAuthTreeData() {
        return htBoaInAppService.loadAppAndAuthVo();
    }
}
