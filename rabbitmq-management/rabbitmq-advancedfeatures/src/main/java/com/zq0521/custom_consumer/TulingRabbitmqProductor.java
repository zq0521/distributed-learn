package com.zq0521.custom_consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息提供者
 */
public class TulingRabbitmqProductor {

    public static void main(String[] args) throws IOException, TimeoutException {
        //构建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.155");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("tuling");
        connectionFactory.setUsername("zhangsan");
        connectionFactory.setPassword("123456");
        connectionFactory.setConnectionTimeout(100000);

        //创建连接
        Connection connection = connectionFactory.newConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //定义交换机
        String exchangeName = "tuling.customconsumer.direct";
        //定义routingKey
        String routingKey = "tuling.customconsumer.key";

        //定义消息
        String messageBody = "hello-tuling";

        //发布消息
        for (int i = 0; i < 5; i++) {
            channel.basicPublish(exchangeName, routingKey, null,messageBody.getBytes());
        }

    }


}
