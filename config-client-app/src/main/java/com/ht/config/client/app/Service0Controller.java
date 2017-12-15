package com.ht.config.client.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 摘要:
 *
 * @author xyt
 * @create 2017-12-15 下午3:29
 **/

@RefreshScope
@RestController
public class Service0Controller {
    @Value("${from}")
    String from1;

    @RequestMapping("froms")
    public String froms() {
        return from1;
    }

}
