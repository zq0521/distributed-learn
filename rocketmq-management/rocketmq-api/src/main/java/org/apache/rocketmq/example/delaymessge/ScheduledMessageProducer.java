package org.apache.rocketmq.example.delaymessge;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * 延迟消息 生产者样例
 */
public class ScheduledMessageProducer {

    public static void main(String[] args) {
        DefaultMQProducer producer = new DefaultMQProducer("DelayMessageProducerGroup");
        try {
            producer.setNamesrvAddr("192.168.1.155:9876");
            producer.start();
            int totalMessageToSend = 100;
            for (int i = 0; i < totalMessageToSend; i++) {
                Message msg = new Message("TopicTest", "Hello World".getBytes(RemotingHelper.DEFAULT_CHARSET));
                // 在开源版本的rocketMQ中，只支持18个等级的延迟时间，具体详见：
                // 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
                msg.setDelayTimeLevel(3);
                producer.send(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.shutdown();
        }

    }

}
