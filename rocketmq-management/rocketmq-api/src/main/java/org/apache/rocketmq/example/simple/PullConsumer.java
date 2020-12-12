package org.apache.rocketmq.example.simple;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 简单样例： pull模式的消费者
 */
public class PullConsumer {

    // 构造一个set集合，对messageQueue的offset指针进行维护
    private static final Map<MessageQueue, Long> OFFSET_TABLE = new HashMap<MessageQueue, Long>();

    public static void main(String[] args) throws MQClientException {
        // 1.创建消费者Consumer，指定消费者组名
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("please_rename_unique_group_name_5");
        // 2.指定NameServer地址
        consumer.setNamesrvAddr("192.168.1.155:9876");
        // 3.启动消费者consumer
        consumer.start();

        // 4.订阅主题Topic和Tag
        Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues("TopicTest");
        for (MessageQueue mq : mqs) {
            System.out.printf("Consume form queue %s%n", mq);
            SINGLE_MQ:
            while (true) {
                try {

                    /**
                     * pullBlockIfNotFound()参数说明：
                     *      mq: 指定的消息队列;
                     *      subExpression：指定的tag,这里没有对tag进行指定;
                     *      offset：从消息队列的哪个位置进行消费;
                     *      maxNum: 一次拉取多少条消息进行消费;
                     *
                     */
                    PullResult pullResult = consumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 32);
                    System.out.printf("%s%n", pullResult);

                    // 这里在拉取完消息后，手动维护Mq的offset的位置,是为了其他消费者在消费消息的时候，找到该位置
                    putMessageQueueSet(mq, pullResult.getNextBeginOffset());

                    switch (pullResult.getPullStatus()) {
                        case FOUND:
                            break;
                        case NO_MATCHED_MSG:
                            break;
                        case NO_NEW_MSG:
                            break SINGLE_MQ;
                        case OFFSET_ILLEGAL:
                            break;
                        default:
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 5.关闭消费者
        consumer.shutdown();
    }

    // 从消息队列中拿 最后的消费指针
    private static long getMessageQueueOffset(MessageQueue mq) {
        Long offset = OFFSET_TABLE.get(mq);
        if (offset != null) {
            return offset;
        }
        return 0;
    }

    // 放置消息指针到消息队列
    private static void putMessageQueueSet(MessageQueue mq, long offset) {
        OFFSET_TABLE.put(mq, offset);
    }

}
