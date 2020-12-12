package org.apache.rocketmq.example.simple;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * 简单样例：单向发送消息
 * 使用该模式发送的消息，没有回调，没有反馈，只管将消息发出即可
 */
public class OnewayProducer {

    public static void main(String[] args) throws MQClientException, InterruptedException {
        {
            // 1.创建消息生产者，并指定生产者组名
            DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
            // 2.这里在环境变量中指定了nameServerAddr了，所以这一步缺省了
            producer.setNamesrvAddr("192.168.1.155:9876");
            // 3.启动producer
            producer.start();
            for (int i = 0; i < 20; i++) {
                try {
                    // 4. 创建消息体，并指定Topic，Tag和消息体
                    Message msg = new Message("TopicTest",
                            "TagA",
                            "OrderID188", "HelloWord".getBytes(RemotingHelper.DEFAULT_CHARSET));

                    // 5.1 sendOneWay() 该方法没有返回值
                    // call send message to deliver message to one of brokers
                    producer.sendOneway(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // wait for sending to complete
            Thread.sleep(5000);
            producer.shutdown();
        }
    }

}
