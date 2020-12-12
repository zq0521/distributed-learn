package com.zq0521.consumer_limit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息的手工签收 + confirmSelect 的模式
 */
public class TulingQosRabbitmqProductor {

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

        //定义交换机的名称
        String exchangeName = "tuling.qos.direct";
        String routingKey = "tuling.qos.key";
        //定义消息体
        String msgBody = "你好tuilng";

        //消息发布
        for (int i = 0; i < 100; i++) {
            channel.basicPublish(exchangeName, routingKey, null, (msgBody + i).getBytes());
        }


    }
}
