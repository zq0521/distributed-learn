package org.apache.rocketmq.example.ordermessage;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 顺序消息： 消息生产者
 */
public class Producer {

    public static final String TAG_KEY = "order_";
    public static final String KEYS = "order_";

    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException {
        try {
            DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
            producer.setNamesrvAddr("192.168.1.155:9876");
            producer.start();

            for (int i = 0; i < 10; i++) {
                int orderId = i;

                for (int j = 0; j < 5; j++) {
                    Message msg = new Message("OrderTopicTest",
                            TAG_KEY + orderId,
                            KEYS + orderId, (TAG_KEY + orderId + " step " + j).getBytes(RemotingHelper.DEFAULT_CHARSET));

                    /**
                     * 逻辑说明：
                     *      在MessageQueueSelector接口中，在send()的时候，传入了一个orderId进去，就是select()方法里面的arg这个参数
                     *      然后在该select()方法中，对orderId进行取模操作，保证同一个orderId的消息可以传入同一个messageQueue中
                     */
                    producer.send(msg, new MessageQueueSelector() {
                        @Override
                        public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                            Integer id = (Integer) arg;
                            int index = id % mqs.size();
                            return mqs.get(index);
                        }
                    }, orderId);
                }

            }
            producer.shutdown();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        }

    }


}
