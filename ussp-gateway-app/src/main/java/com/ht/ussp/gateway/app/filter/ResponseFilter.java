package com.ht.ussp.gateway.app.filter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.springframework.util.StreamUtils;

import com.esotericsoftware.minlog.Log;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class ResponseFilter extends ZuulFilter{

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
        try {
        	RequestContext ctx = RequestContext.getCurrentContext();
            InputStream stream = ctx.getResponseDataStream();
            String body = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
            Log.info("------------"+body);
            ctx.setResponseBody("new body: "+body);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;}


		

	@Override
	public String filterType() {
		return "post";
	}

	@Override
	public int filterOrder() {
		return 999;
	}

}
