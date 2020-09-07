package com.zq0521.consumer_limit;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class TulingQosConsumer extends DefaultConsumer {

    private Channel channel;
    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */
    public TulingQosConsumer(Channel channel) {
        super(channel);
        this.channel = channel;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.out.println("consumerTag:"+consumerTag);
        System.out.println("envelope:"+envelope);
        System.out.println("properties:"+properties);
        System.out.println("body:"+new String(body));

        /**
         * multiple: fales标识不批量签收
         * 当注释掉下面的那段代码后，由于第一条消息没有被签收，就会阻塞在那里，下一条消息就不会到达，服务器就不会因为大量消息的涌入而宕机
         */
        //channel.basicAck(envelope.getDeliveryTag(),false);

        //当处理完业务之后，再调用channel.basicAck()方法来进行手动签收，如果抛出了异常，可以调用channel.basicNack() 或者 channel.basicReject(),一个是notAck,另一个是拒绝该消息
    }
}
