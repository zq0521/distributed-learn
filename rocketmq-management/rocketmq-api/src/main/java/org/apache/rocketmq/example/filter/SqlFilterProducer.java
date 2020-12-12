package org.apache.rocketmq.example.filter;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * sql过滤消息
 */
public class SqlFilterProducer {

    public static void main(String[] args) {
        DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
        try {
            producer.setNamesrvAddr("192.168.1.155:9876");
            producer.start();
            String[] tags = new String[]{"TagA", "TagB", "TagC"};

            for (int i = 0; i < 15; i++) {
                Message msg = new Message("TopicTest",
                        tags[i % tags.length],
                        ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET)
                );
                // message其实是个map，可以往里面放置自定义属性
                msg.putUserProperty("a", String.valueOf(i));

                SendResult sendResult = producer.send(msg);
                System.out.printf("%s%n", sendResult);
            }

            producer.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
