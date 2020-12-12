package com.zq0521.quickstartonclass;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


/**
 * 生产者
 */
public class QucikProducter {



    public static void main(String[] args) throws IOException, TimeoutException {
        //首先 -》创建一个连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.155");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("tuling");
        connectionFactory.setUsername("zhangsan");
        connectionFactory.setPassword("123456");
        connectionFactory.setConnectionTimeout(100000);


        //创建连接
        Connection connection = connectionFactory.newConnection();

        //创建我们的channle(通信的管道)
        Channel channel = connection.createChannel();

        //使用for循环发送几次消息过去
        for (int i=0;i<5;i++){
            String message ="hello--"+i;
            channel.basicPublish("","tuling-queue-01",null,message.getBytes());
        }


        //关闭连接
        channel.close();
        connection.close();

    }


}
