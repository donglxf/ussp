package com.ht.ussp.uc.app.resource;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.service.HtBoaInOperatorLogService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @ClassName: HtBoaInOperatorLogResource
 * @Description: 操作日志信息
 * @author adol yaojiehong@hongte.info
 * @date 2018年1月13日 下午04:16:03
 */

@RestController
@RequestMapping(value = "/operator_log")
public class OperatorLogResource {

    private static final Logger log = LoggerFactory
            .getLogger(OperatorLogResource.class);

    @Autowired
    private HtBoaInOperatorLogService htBoaInOperatorLogService;
    
    @ApiOperation(value = "对内：操作日志查询", notes = "列出所有操作日志列表信息")
    @ApiImplicitParam(name = "pageConf", value = "分页信息实体", required = true, dataType = "PageConf")
    @RequestMapping(value = { "/in/list" }, method = RequestMethod.POST)
    public ResponseModal list(@RequestBody PageConf pageConf) {
        long sl = System.currentTimeMillis(), el = 0L;
        String msg = "成功";
        String logHead = "操作日志查询：operator_log/in/list param-> {}";
        String logStart = logHead + " | START:{}";
        String logEnd = logHead + " {} | END:{}, COST:{}";
        log.debug(logStart, "pageConf: " + pageConf, sl);
        Object o = htBoaInOperatorLogService.findAllByPage(pageConf);
        el = System.currentTimeMillis();
        log.debug(logEnd, "pageConf: " + pageConf, msg, el, el - sl);
        return new ResponseModal("200", msg, o);
    }

    protected ResponseModal exceptionReturn(String logEnd, String param,  List<?> list, long sl, String exInfo, int row) {
        if (null == exInfo)
            exInfo = "";
        if (null == list || list.isEmpty()) {
            String msg = "无效参数，" + exInfo + "查无信息体";
            long el = System.currentTimeMillis();
            log.error(logEnd, param, msg, el, el - sl);
            return new ResponseModal("500", msg);
        } else if (row != list.size()) {
            String msg = "查询异常！查出" + exInfo + "记录数不符合要求";
            long el = System.currentTimeMillis();
            log.error(logEnd, param, msg, el, el - sl);
            return new ResponseModal("500", msg);
        }
        return null;
    }

}
