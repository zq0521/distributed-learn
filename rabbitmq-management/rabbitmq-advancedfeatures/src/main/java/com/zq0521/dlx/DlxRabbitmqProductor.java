package com.zq0521.dlx;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class DlxRabbitmqProductor {

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

        //消息10秒没有被消费，那么就会被转到死信队列上
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder()
                .deliveryMode(2)
                .expiration("10000")
                .build();

        //声明正常队列
        String nomalExchangeName = "tuling.nomaldlx.exchange";
        String routingKey = "tuling.dlx.key1";

        //消息体
        String message = "我是测试死信队列消息";
        for (int i = 0; i < 100; i++) {
            channel.basicPublish(nomalExchangeName, routingKey, basicProperties, message.getBytes());
        }


    }
}
