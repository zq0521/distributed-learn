package com.zq0521.topic_exchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq 主题交换机   topicExchange服务端
 */
public class TopicExchangeProductor {

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接工厂
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

        //发送消息
        String exchangeName = "tuling.topicExchange";

        String routingKey1 = "tuling.key1";
        String routingKey2 = "tuling.key2";
        String routingKey3 = "tuling.key.key3";

        channel.basicPublish(exchangeName,routingKey1,null,"我是第一条消息".getBytes());
        channel.basicPublish(exchangeName,routingKey2,null,"我是第二条消息".getBytes());
        channel.basicPublish(exchangeName,routingKey3,null,"我是第三条消息".getBytes());

        //释放
        channel.close();
        connection.close();

    }

}
