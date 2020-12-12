package com.zq0521.config;

import com.zq0521.constants.MqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

    @Bean
    public DirectExchange directExchange() {
        DirectExchange directExchange = new DirectExchange(MqConstants.ORDER_TO_PRODUCT_EXCHANGE_NAME, true, false);
        return directExchange;
    }

    @Bean
    public Queue orderToProductQueue() {
        Queue queue = new Queue(MqConstants.ORDER_TO_PRODUCT_QUEUE_NAME, true, false, false);
        return queue;
    }

    @Bean
    public Binding orderToProductBinding() {
       return BindingBuilder.bind(orderToProductQueue()).to(directExchange()).with(MqConstants.ORDER_TO_PRODUCT_ROUTING_KEY);
    }



}
