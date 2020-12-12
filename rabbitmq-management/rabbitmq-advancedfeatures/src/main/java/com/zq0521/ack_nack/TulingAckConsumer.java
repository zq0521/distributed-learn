package com.zq0521.ack_nack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class TulingAckConsumer extends DefaultConsumer {

    private Channel channel;

    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */
    public TulingAckConsumer(Channel channel) {
        super(channel);
        this.channel = channel;
    }


    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        //模拟业务
        try {
            Integer mark = (Integer) properties.getHeaders().get("mark");
            if (mark != 0) {
                System.out.println("消费消息：" + new String(body));
            } else {
                throw new RuntimeException("模拟业务异常");
            }

        } catch (Exception e) {
            System.out.println("异常消费消息：" + new String(body));
            //重回队列
            //参数说明：第一个参数：指的是这个消息的唯一标识 ;   multiple:是否批量签收/拒绝 消息    requeue:是否重回消息队列
            //channel.basicNack(envelope.getDeliveryTag(), false, true);
            //不重回队列
            channel.basicNack(envelope.getDeliveryTag(),false,false);

        }


    }
}
