package org.apache.rocketmq.example.simple;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * 简单用例： 使用推模式进行消费消息
 */
public class PushConsumer {

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("CID_JODIE_1");
        consumer.setNamesrvAddr("192.168.1.155:9876");
        // 订阅主题:TopicTest，tag指定所有的tag
        consumer.subscribe("TopicTest", "*");
        // 指定从队列的哪个位置进行消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // wrong time format 2017_0422_221800
        consumer.setConsumeTimestamp("20201212221800");
        // 消费者指定消息的监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Message: %s  %n", Thread.currentThread().getName(), msgs);
                // 客户端需要返回消费状况给broker;
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
        System.out.printf("consumer Started. %n");
    }

}
