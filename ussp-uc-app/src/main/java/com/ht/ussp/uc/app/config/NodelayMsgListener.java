package com.ht.ussp.uc.app.config;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

@Component
public class NodelayMsgListener {

    private static Logger logger = LoggerFactory.getLogger(NodelayMsgListener.class);
    
    
    /**
     * @Title: processMsgService  
     * @Description: 即时消息处理 AmqpConfig.MSG_NODELAY队列，监控消息队列
     * @param     参数  mq返回的消息
     * @return void    返回类型  
     */
   /* @RabbitListener(queues = AmqpConfig.UC_NODELAY)
    @RabbitHandler
    public void processInternalssNodelayMsgService(Message message) {
        if (logger.isDebugEnabled()) {
			logger.debug("entering processInternalssNodelayMsgService(Message)");
			logger.debug("message: " + message);
		}
		String text = "";
		try {
			text = new String(message.getBody(),"UTF-8");
			System.out.println(text);
		} catch (UnsupportedEncodingException e) {
			text = new String(message.getBody());
			logger.error("发送对象编码错误",e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("exiting processInternalssNodelayMsgService()");
		}
    }*/
}
