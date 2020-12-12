package com.zq.rocketmq.basic;

import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * springboot整合rocketMQ :消息生产者
 */
@Component
public class SpringProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    // 发送普通消息方法
    public void sendMessage(String topic, String msg) {
        this.rocketMQTemplate.convertAndSend(topic, msg);
    }

    // 发送事务消息方法
    public void sendMessageInTransaction(String topic, String msg) throws InterruptedException {
        String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
        for (int i = 0; i < 10; i++) {
            // 尝试在header中加入一些自定义属性
            // 注意：这里的Message不再是org.apache.rocketmq.common.message.Message   而是spring的message : org.springframework.messaging.Message
            Message<String> message = MessageBuilder.withPayload(msg)
                    // 设置事务ID
                    .setHeader(RocketMQHeaders.TRANSACTION_ID, "TransID_" + i)
                    // 发到事务消息监听器中后，这个自己设定的TAGS属性会消失，但是上面那个属性不会
                    .setHeader(RocketMQHeaders.TAGS, tags[i % tags.length])
                    // MyProp在事务监听器例也能拿到，为什么只有TAGS属性拿不到了
                    .setHeader("MyProp", "MyProp_" + i)
                    .build();

            // 在Springboot整合rocketMQ中，就不再是topic和tag分开写了，需要拼接到一起
            String destination = topic + ":" + tags[i % tags.length];
            // 这里发送事务消息的时候，还是需要将spring的Message类型转换为RocketMQ的Message类型，再调用rocketMQ的API完成事务消息机制
            // 这里说的转换类型，是在springRocketMQ的底层封装的时候转的，不是我们调用方法直接放一个rocketMQ的message进去
            // 第三个参数是可以传入一个额外值，这里传入了topic+tag的字符串进去
            TransactionSendResult sendResult = rocketMQTemplate.sendMessageInTransaction(destination, message, destination);
            System.out.printf("%s%n", sendResult);

            Thread.sleep(10);
        }
    }
}
