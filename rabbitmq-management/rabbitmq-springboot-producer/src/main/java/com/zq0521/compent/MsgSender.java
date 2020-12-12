package com.zq0521.compent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zq0521.entity.Order;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 消息发送组件
 */
@Component
public class MsgSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void sendMsg(Object msg, Map<String, Object> msgProp) {
        MessageHeaders messageHeaders = new MessageHeaders(msgProp);

        //构建消息对象
        org.springframework.messaging.Message message = MessageBuilder.createMessage(msg, messageHeaders);

        //构建correlationDate 用于做可靠性传递，ID：必 须全局唯一，根据业务规则
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        //开启确认模式
        rabbitTemplate.setConfirmCallback(new ConfirmCallBack());
        //开启消息可达监听
        rabbitTemplate.setReturnCallback(new ReturnCallBack());

        //发送消息
        rabbitTemplate.convertAndSend("springboot.direct.exchange", "springboot.key", message, correlationData);
        rabbitTemplate.convertAndSend("springboot.direct.exchange", "springboot.key2", message, correlationData);
    }


    public void sendOrderMsg(Order order) throws JsonProcessingException {

        //构建correlationDate 用于做可靠性传递，ID：必须全局唯一，根据业务规则
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        //开启确认模式
        rabbitTemplate.setConfirmCallback(new ConfirmCallBack());
        //开启消息可达监听
        rabbitTemplate.setReturnCallback(new ReturnCallBack());

        /**
         * 发送core下的message对象
         */
        ObjectMapper objectMapper = new ObjectMapper();
        String orderJson = objectMapper.writeValueAsString(order);
        MessageProperties messageProperties = new MessageProperties();
        Message message = new Message(orderJson.getBytes(), messageProperties);
        rabbitTemplate.convertAndSend("springboot.direct.exchange", "springboot.key3", message, correlationData);

        /**
         * 发送Messaging包下的message对象
         */
        Map<String, Object> map = new HashMap<>();
        map.put("company", "fslh");
        MessageHeaders messageHeaders = new MessageHeaders(map);
        ObjectMapper objectMapper1 = new ObjectMapper();
        String orderJson1 = objectMapper1.writeValueAsString(order);
        org.springframework.messaging.Message message1 = MessageBuilder.createMessage(orderJson1, messageHeaders);
        System.out.println("orderJson1:" + orderJson1);
        rabbitTemplate.convertAndSend("springboot.direct.exchange", "springboot.key3", message1, correlationData);

        /**
         * 直接发送对象
         */
        rabbitTemplate.convertAndSend("springboot.direct.exchange", "springboot.key3", order, correlationData);
    }


    /**
     * 发送延时消息
     *
     * @param order
     */
    public void sendDelayMessage(Order order) {

        //构建correlationDate用于做可靠性传递得: ID必须全局唯一的，根据业务规则
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        //开启确认模式
        rabbitTemplate.setConfirmCallback(new ConfirmCallBack());
        //开启不可达回监听
        rabbitTemplate.setReturnCallback(new ReturnCallBack());
        //传入消息构造器为Json
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        rabbitTemplate.convertAndSend("delayExchange", "springboot.delay.key", order, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-delay", 10000); //设置延时时间
                return message;
            }
        }, correlationData);

    }

}
