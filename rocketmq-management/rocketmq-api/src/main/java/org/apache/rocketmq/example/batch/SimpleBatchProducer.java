package org.apache.rocketmq.example.batch;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量消息样例：
 */
public class SimpleBatchProducer {

    public static void main(String[] args) throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer("BatchProducerGroupName");
        producer.setNamesrvAddr("192.168.1.155:9876");
        producer.start();

        try {
            String topic = "TopicTest";
            List<Message> messageList = new ArrayList<>();
            messageList.add(new Message(topic, "Tag", "OrderId001", "Hello world 0".getBytes(RemotingHelper.DEFAULT_CHARSET)));
            messageList.add(new Message(topic, "Tag", "OrderId002", "Hello world 1".getBytes(RemotingHelper.DEFAULT_CHARSET)));
            messageList.add(new Message(topic, "Tag", "OrderId003", "Hello world 2".getBytes(RemotingHelper.DEFAULT_CHARSET)));
            messageList.add(new Message(topic, "Tag", "OrderId004", "Hello world 3".getBytes(RemotingHelper.DEFAULT_CHARSET)));

            producer.send(messageList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.shutdown();
        }
    }


}
