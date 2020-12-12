package org.apache.rocketmq.example.simple;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 简单样例： 异步发送消息
 */
public class AsyncProducer {


    public static void main(String[] args) throws MQClientException, InterruptedException {


        // 1. 创建消息生产者，并指定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("simpleProduceGroupName");
        // 2. 指定nameServer
        producer.setNamesrvAddr("192.168.1.155:9876");
        // 3.启动producer
        producer.start();
        // 发送失败重试次数
        producer.setRetryTimesWhenSendAsyncFailed(0);

        int messageCount = 100;
        // 由于是异步发送，这里引入一个countDownLatch，保证所有producer发送的消息回调方法都执行完成了再停止producer服务。
        final CountDownLatch countDownLatch = new CountDownLatch(messageCount);

        for (int i = 0; i < messageCount; i++) {
            try {
                final int index = i;
                // 4. 创建消息体，并指定Topic，Tag和消息体
                Message msg = new Message("TopicTest",
                        "TagA",
                        "OrderID188", "HelloWord".getBytes(RemotingHelper.DEFAULT_CHARSET));

                producer.send(msg, new SendCallback() {
                    // 消息发送成功回调方法
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        countDownLatch.countDown();
                        System.out.printf("%-10d OK %s %n", index, sendResult.getMsgId());
                    }

                    // 消息发送出现异常， 回调方法
                    @Override
                    public void onException(Throwable e) {
                        countDownLatch.countDown();
                        System.out.printf("%-10d %s %n", index, e);
                    }
                });

                System.out.println("消息发送完成");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        countDownLatch.await(5, TimeUnit.SECONDS);
        producer.shutdown();

    }

}
