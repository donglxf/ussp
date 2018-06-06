package com.ht.ussp.ouc.app.config;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import com.alibaba.druid.support.http.WebStatFilter;
/**
 * 
 * @ClassName: DruidStatFilter
 * @Description: druid监控拦截器，过滤不需要的监控后缀
 * @author wim qiuwenwu@hongte.info
 * @date 2018年3月6日 下午5:05:47
 */

@WebFilter(filterName="druidWebStatFilter",    
urlPatterns="/*",    
initParams={    
    @WebInitParam(name="exclusions",value="*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*"),// 忽略资源    
}) 
public class DruidStatFilter  extends WebStatFilter{

}
