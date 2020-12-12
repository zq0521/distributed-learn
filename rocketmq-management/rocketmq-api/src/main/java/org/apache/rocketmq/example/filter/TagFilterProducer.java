package org.apache.rocketmq.example.filter;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * 使用Tag过滤消息  生产者
 */
public class TagFilterProducer {

    public static void main(String[] args) {
        DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
        try {
            producer.setNamesrvAddr("192.168.1.155:9876");
            producer.start();

            String[] tags = new String[]{"TagA", "TagB", "TagC"};
            for (int i = 0; i < 15; i++) {
                Message msg = new Message("TopicTest",
                        tags[i % tags.length],
                        "hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));

                SendResult result = producer.send(msg);
                System.out.printf("%s%n", result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.shutdown();
        }
    }


}
