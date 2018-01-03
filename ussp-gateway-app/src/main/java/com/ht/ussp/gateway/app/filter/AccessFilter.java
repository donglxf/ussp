package com.ht.ussp.gateway.app.filter;

import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * 
 * @ClassName:  AccessFilter   
 * @Description:请求过滤
 * @author: wim 
 * @date:   2017年8月8日 上午11:40:47
 */
public class AccessFilter extends ZuulFilter{
	@Value("${whiteList}")
	private String whiteList;
	
	private static Logger log=LoggerFactory.getLogger(AccessFilter.class);
	
	/**
	 * 定义该过滤器是否要执行
	 */
	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		RequestContext ctx=RequestContext.getCurrentContext();
		HttpServletRequest request=ctx.getRequest();
		ctx.getResponse().setHeader("Content-type", "text/html;charset=UTF-8"); 
		String ip = getIpAddr(request);
		String url=request.getRequestURL()+request.getQueryString(); 
		log.info("------------ip------"+ip);
		log.info("------------url------"+url);
//		Object jwtToken=request.getParameter("jwtToken");
//		if(jwtToken==null){
//			log.warn("jwtToken is empty!");
//			ctx.setSendZuulResponse(false);
//			ctx.setResponseStatusCode(401);
//			ctx.setResponseBody("{\"result\":\"jwtToken is empty!\"}");// 返回错误内容  
//			return null;
//		}
		
		ctx.setSendZuulResponse(true);
		ctx.setResponseStatusCode(200);
		return null;
//		
//		if(whiteList.indexOf(ip)!=-1){
//			log.info("------------您的ip允许访问------");
//			ctx.setSendZuulResponse(true);
//			ctx.setResponseStatusCode(200);
//		   return null;
//		}else{
//			log.info("------------您的ip不允许访问------");
//			ctx.setSendZuulResponse(false);
//			ctx.setResponseStatusCode(401);
//			ctx.setResponseBody("{\"result\":\"拒绝访问!\"}");
//			return null;
//			
//			
//		}
	}

	/**
	 * 过滤器类型，
	 * pre:表示在请求之前执行 
	 * routing: 表示请求时被调用
	 * post:在route和error过滤器之后被调用
	 * error:处理请求发生错误时被调用
 	 */
	@Override
	public String filterType() {
		return "pre";
	}

	/**
	 * 过滤器的执行顺序
	 */
	@Override
	public int filterOrder() {
		return 0;
	}
	
	
	public String getIpAddr(HttpServletRequest request) {
		String ipAddress = request.getHeader("x-forwarded-for");

		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (Exception e) {
					e.printStackTrace();
				}
				ipAddress = inet.getHostAddress();
				log.info("------------ipAddress的值是：------"+ipAddress);
			}
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}

		return ipAddress;
	}
}
