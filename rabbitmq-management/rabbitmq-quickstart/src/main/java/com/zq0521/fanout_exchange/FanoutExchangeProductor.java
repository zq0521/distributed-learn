package com.zq0521.fanout_exchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq 广播交换机  消息发布者
 */
public class FanoutExchangeProductor {

    public static void main(String[] args) throws IOException, TimeoutException {

        //创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.155");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("tuling");
        connectionFactory.setUsername("zhangsan");
        connectionFactory.setPassword("123456");
        connectionFactory.setConnectionTimeout(100000);

        //建立连接
        Connection connection = connectionFactory.newConnection();
        //建立信道
        Channel channel = connection.createChannel();

        //定义交换机名称
        String exchangeName = "tuling.fanoutExchange";

        //定义routingKey
        String routingKey = "";

        //消息体内容
        String messageBody = "hello tuling ";
        channel.basicPublish(exchangeName,"123",null,"我是第一条消息".getBytes());
        channel.basicPublish(exchangeName,"456",null,"我是第二条消息".getBytes());
        channel.basicPublish(exchangeName,"789",null,"我是第三条消息".getBytes());

    }
}
