package com.ht.ussp.uc.app.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

@Configuration
public class AmqpConfig {

	public final static String UC_DELAY = "uc.delay"; //延迟
    public final static String UC_NODELAY = "uc.nodelay";//非延迟
    public final static String UC_ROUTING_DELAY = "uc.delay"; //延迟
    public final static String UC_ROUTING_NODELAY = "uc.nodelay";//非延迟
    public final static String UC_EXCHANGE = "ucExchange";
    //创建队列
    @Bean
    public Queue queueUCDelay() {
    	Map<String, Object> arguments = new HashMap<>();
    	arguments.put("x-max-priority", 1);
        return new Queue(AmqpConfig.UC_DELAY,true, false, false, arguments);
    }

    //创建队列
    @Bean
    public Queue queueUCNodelay() {
    	Map<String, Object> arguments = new HashMap<>();
    	arguments.put("x-max-priority", 10);
        return new Queue(AmqpConfig.UC_NODELAY,true, false, false, arguments);
    }

    //创建交换器
    @Bean
    TopicExchange exchange() {
        return new TopicExchange(AmqpConfig.UC_EXCHANGE);
    }
    
    //对列绑定并关联到UC_ROUTING_DELAY
    @Bean
    Binding bindingExchangeMessage(Queue queueUCDelay, TopicExchange exchange) {
        return BindingBuilder.bind(queueUCDelay).to(exchange).with(AmqpConfig.UC_ROUTING_DELAY);
    }

    //对列绑定并关联到UC_ROUTING_NODELAY
    @Bean
    Binding bindingExchangeMessages(Queue queueUCNodelay, TopicExchange exchange) {
        return BindingBuilder.bind(queueUCNodelay).to(exchange).with(AmqpConfig.UC_ROUTING_NODELAY);//*表示一个词,#表示零个或多个词
    }


    @Bean
    public MappingJackson2MessageConverter jackson2Converter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        return converter;
    }

    /**
     * 生产者用
     * @return
     */
    @Bean
    public RabbitMessagingTemplate rabbitMessagingTemplate(RabbitTemplate rabbitTemplate) {
        RabbitMessagingTemplate rabbitMessagingTemplate = new RabbitMessagingTemplate();
        rabbitMessagingTemplate.setMessageConverter(jackson2Converter());
        rabbitMessagingTemplate.setRabbitTemplate(rabbitTemplate);
        return rabbitMessagingTemplate;
    }

}