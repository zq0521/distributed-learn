package org.apache.rocketmq.example.simple;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * 简单样例：同步发送消息
 */
public class Producer {

    public static void main(String[] args) throws MQClientException {
        // 1.创建消息生产者，并指定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("simpleProduceGroupName");
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
                producer.sendOneway(msg);

                // 5.2 send() 该方法有返回值
                //SendResult result = producer.send(msg);
                //System.out.printf("%s%n", result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        producer.shutdown();
    }

}
