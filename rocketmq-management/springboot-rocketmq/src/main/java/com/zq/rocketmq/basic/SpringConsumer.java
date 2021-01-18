package com.zq.rocketmq.basic;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * springboot整合rocketMQ,消息消费者
 */
@Component
// 在这里指定了消费者进行监听，需要指定消费者组、主题、消费消息的顺序（同时消费和排序消费）
@RocketMQMessageListener(consumerGroup = "MyConsumerGroup", topic = "TopicTest", consumeMode = ConsumeMode.CONCURRENTLY)
public class SpringConsumer implements RocketMQListener<String> {


    @Override
    public void onMessage(String message) {
        System.out.printf("%s%n", message);
    }


}
