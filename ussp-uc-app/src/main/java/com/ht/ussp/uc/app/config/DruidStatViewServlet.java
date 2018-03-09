package com.ht.ussp.uc.app.config;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.alibaba.druid.support.http.StatViewServlet;
/**
 * 
 * @ClassName: DruidStatViewServlet
 * @Description: 监控视图配置
 * @author wim qiuwenwu@hongte.info
 * @date 2018年3月6日 下午5:07:17
 */

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/druid/*", initParams={    
	    @WebInitParam(name="allow",value=""),// IP白名单    
	    @WebInitParam(name="deny",value=""),// IP黑名单    
	    @WebInitParam(name="loginUsername",value="admin"),// 用户名    
	    @WebInitParam(name="loginPassword",value="123456"),// 密码    
	    @WebInitParam(name="resetEnable",value="true")// 禁用HTML页面上的“Reset All”功能    
	})  
public class DruidStatViewServlet extends StatViewServlet {

}
