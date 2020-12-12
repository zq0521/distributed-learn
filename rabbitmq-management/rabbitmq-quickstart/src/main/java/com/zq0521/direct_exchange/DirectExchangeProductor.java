package com.zq0521.direct_exchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq直接交换机direct 消息生产者
 */
public class DirectExchangeProductor {

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

        //创建通信管道
        Channel channel = connection.createChannel();

        //定义交换机名称
        String exchangeName = "tuling.directchange";

        //定义routingKey
        String routingKey = "tuling.directchange.key";

        //创建消息体
        String messageBody = "hello tuling ";

        //通过信道将消息发送到交换机上去
        channel.basicPublish(exchangeName, routingKey, null, messageBody.getBytes());

        //释放
        channel.close();
        connection.close();

    }

}
