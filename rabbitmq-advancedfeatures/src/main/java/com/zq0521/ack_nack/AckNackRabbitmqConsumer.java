package com.zq0521.ack_nack;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AckNackRabbitmqConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.155");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("zhangsan");
        connectionFactory.setPassword("123456");
        connectionFactory.setVirtualHost("tuling");
        connectionFactory.setConnectionTimeout(100000);

        //创建连接
        Connection connection = connectionFactory.newConnection();

        //创建一个信道
        Channel channel = connection.createChannel();

        //声明交换机
        String exchangeName = "tuling.ack.direct";
        String exchangeType = "direct";
        channel.exchangeDeclare(exchangeName,exchangeType,true,false,null);

        //声明队列
        String queueName = "tuling.ack.queue";
        channel.queueDeclare(queueName,true,false,false,null);

        //队列绑定交换机
        String routingKey = "tuling.ack.key";
        channel.queueBind(queueName,exchangeName,routingKey);

        //消费端进行消费
        //消费端限流 需要关闭消息自动签收

        channel.basicConsume(queueName,false,new TulingAckConsumer(channel));





    }

}
