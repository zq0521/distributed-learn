package com.zq.rocketmq.config;

import com.zq.rocketmq.basic.MessageListen;
import com.zq.rocketmq.constant.MessageConstant;
import com.zq.rocketmq.handler.impl.OrderPaidEventProcessorImpl;
import com.zq.rocketmq.handler.impl.ProductWithPayloadProcessorImpl;
import com.zq.rocketmq.handler.impl.UserProcessImpl;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 消费者配置类
 */
@Component
public class RocketMQConsumer {

    @Autowired
    private UserProcessImpl userProcess;

    @Autowired
    private OrderPaidEventProcessorImpl orderPaidEventProcessor;

    @Autowired
    private ProductWithPayloadProcessorImpl productWithPayloadProcessor;

    // nameSrv
    private String namesrvAddr = "192.168.1.155:9876";

    // consumerGroup
    private String groupName = "consumer_group_1";
    // topicName
    private String topic = "TopicTest";
    // tag
    private String userTag = "user";
    private String orderPaidTag = "orderPaidTag";
    //


    @Bean
    public DefaultMQPushConsumer getRocketMQConsumer() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setVipChannelEnabled(false);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setMessageModel(MessageModel.CLUSTERING);

        MessageListen messageListen = new MessageListen();
        // 往监听类中添加消息处理类
        messageListen.registerHandler(MessageConstant.USER_TAG, userProcess);
        messageListen.registerHandler(MessageConstant.ORDER_PAID_TAG, orderPaidEventProcessor);
        messageListen.registerHandler(MessageConstant.PRODUCT_WITH_PAYLOAD_TAG, productWithPayloadProcessor);

        consumer.registerMessageListener(messageListen);

        try {
            consumer.subscribe(MessageConstant.SIMPLE_TOPIC, MessageConstant.SIMPLE_TAG);
            consumer.subscribe(MessageConstant.ORDER_PAID_TOPIC, "*");
            consumer.subscribe(MessageConstant.PRODUCT_WITH_PAYLOAD_TOPIC, "*");

            consumer.start();
            System.out.printf("consumer is start  !!! groupName: %s, topic:%s, namesrvAddr:%s %n", groupName, topic, namesrvAddr);
        } catch (Exception e) {
            System.out.printf("consumer is start error !!! groupName: %s,topic:%s,namesrvAddr:%s %n", groupName, topic, namesrvAddr);
            e.printStackTrace();
            consumer.shutdown();
        }
        return consumer;
    }

}
