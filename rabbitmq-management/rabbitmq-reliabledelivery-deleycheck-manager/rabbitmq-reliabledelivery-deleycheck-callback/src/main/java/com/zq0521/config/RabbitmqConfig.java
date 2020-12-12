package com.zq0521.config;

import com.zq0521.constants.MqConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitmqConfig {


    //------------------------------------------------交换机实例化-----------------------------------------------------------

    /**
     * 订单->库存 交换机
     *
     * @return
     */
    @Bean
    public DirectExchange directExchange() {
        DirectExchange directExchange = new DirectExchange(MqConstants.ORDER_TO_PRODUCT_EXCHANGE_NAME, true, false);
        return directExchange;
    }

    /**
     * 库存->回调 交换机
     *
     * @return
     */
    @Bean
    public DirectExchange productToCallbackExchange() {
        DirectExchange directExchange = new DirectExchange(MqConstants.PRODUCT_TO_CALLBACK_EXCHANGE_NAME, true, false);
        return directExchange;
    }

    /**
     * 订单->库存 延迟消息交换机
     *
     * @return
     */
    @Bean
    public CustomExchange orderToProductDelayExchange() {
        Map<String, Object> args = new HashMap();
        args.put("x-delayed-type", "direct");
        CustomExchange delayExchange = new CustomExchange(MqConstants.ORDER_TO_PRODUCT_DELAY_EXCHANGE_NAME, "x-delayed-message", true, false, args);
        return delayExchange;
    }


    //------------------------------------------------队列实例化-----------------------------------------------------------

    /**
     * 订单->库存 队列
     *
     * @return
     */
    @Bean
    public Queue orderToProductQueue() {
        Queue queue = new Queue(MqConstants.ORDER_TO_PRODUCT_QUEUE_NAME, true, false, false);
        return queue;
    }

    /**
     * 库存->回调 队列
     *
     * @return
     */
    @Bean
    public Queue productToCallbackQueue() {
        Queue queue = new Queue(MqConstants.PRODUCT_TO_CALLBACK_QUEUE_NAME, true, false, false);
        return queue;
    }


    /**
     * 订单->库存 延迟队列
     *
     * @return
     */
    @Bean
    public Queue orderToProductDelayQueue() {
        Queue queue = new Queue(MqConstants.ORDER_TO_PRODUCT_DELAY_QUEUE_NAME, true, false, false);
        return queue;
    }


//------------------------------------------------消息绑定-----------------------------------------------------------

    /**
     * 订单->库存 消息绑定
     *
     * @return
     */
    @Bean
    public Binding orderToProductBinding() {
        return BindingBuilder.bind(orderToProductQueue()).to(directExchange()).with(MqConstants.ORDER_TO_PRODUCT_ROUTING_KEY);
    }

    /**
     * 库存->回调 消息绑定
     *
     * @return
     */
    @Bean
    public Binding productToCallbackBinding() {
        return BindingBuilder.bind(productToCallbackQueue()).to(productToCallbackExchange()).with(MqConstants.PRODUCT_TO_CALLBACK_ROUTING_KEY);
    }

    /**
     * 订单->库存 延迟消息绑定
     *
     * @return
     */
    @Bean
    public Binding orderToProductDelayBinding() {
        return BindingBuilder.bind(orderToProductDelayQueue()).to(orderToProductDelayExchange()).with(MqConstants.ORDER_TO_PRODUCT_DELAY_ROUTING_KEY).noargs();
    }


}
