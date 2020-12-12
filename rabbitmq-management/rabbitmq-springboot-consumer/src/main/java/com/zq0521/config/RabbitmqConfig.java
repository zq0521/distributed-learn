package com.zq0521.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitmqConfig {

    /**
     * 创建 direct交换机
     *
     * @return
     */
    @Bean
    public DirectExchange zqBootDirectExchange() {
        DirectExchange directExchange = new DirectExchange("springboot.direct.exchange", true, false);
        return directExchange;
    }

    /**
     * 创建 延迟交换机
     *
     * @return
     */
    @Bean
    public CustomExchange delayExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange("delayExchange", "x-delayed-message", true, false, args);
    }

    //声明队列
    @Bean
    public Queue zqBootQueue() {
        Queue queue = new Queue("zqBootQueue", true, false, false);
        return queue;
    }

    @Bean
    public Queue zqClusterQueue() {
        Queue queue = new Queue("zqClusterQueue", true, false, false);
        return queue;
    }

    @Bean
    public Queue zqBootDelayQueue() {
        Queue queue = new Queue("zqBootDelayQueue", true, false, false);
        return queue;
    }

    //声明绑定关系
    @Bean
    public Binding zqBootBinder() {
        return BindingBuilder.bind(zqBootQueue()).to(zqBootDirectExchange()).with("springboot.key");
    }

    @Bean
    public Binding zqClusterBinder() {
        return BindingBuilder.bind(zqClusterQueue()).to(zqBootDirectExchange()).with("rabbitmq.cluster.key");
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(zqBootDelayQueue()).to(delayExchange()).with("springboot.delay.key").noargs();
    }

}
