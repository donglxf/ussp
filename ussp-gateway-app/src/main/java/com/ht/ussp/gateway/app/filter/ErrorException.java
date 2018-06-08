package com.ht.ussp.gateway.app.filter;

import javax.servlet.http.HttpServletResponse;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import lombok.extern.log4j.Log4j2;

/**
 * 
 * @ClassName: ErrorException
 * @Description: 统一异常处理
 * @author wim qiuwenwu@hongte.info
 * @date 2018年6月8日 下午2:43:43
 */

@Log4j2
public class ErrorException extends ZuulFilter {
	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		String url = ctx.getRequest().getRequestURL().toString();
		log.error("========request's url========" + url);
        Throwable throwable = ctx.getThrowable();
        log.error("this is a ErrorFilter : {}", throwable.getCause().getMessage());
        ctx.set("error.status_code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ctx.set("error.exception", throwable.getCause());
        return null;

	}

	@Override
	public String filterType() {
		return "error";
	}

	@Override
	public int filterOrder() {
		return 0;
	}

}
