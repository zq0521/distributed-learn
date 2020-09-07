package com.zq0521.message;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class RabbitmqMessageProductor {

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

        //创建头部信息
        Map<String,Object> headMap = new HashMap<>();
        headMap.put("company","fslh");
        headMap.put("author","zq");

        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties();
        basicProperties.builder()
                .deliveryMode(2)  //2：标识持久化消息   1：标识 重启服务后 消息不会被持久化
                .expiration("10000") //消息过期 10s
                .contentEncoding("utf-8")
                .correlationId(UUID.randomUUID().toString())
                .headers(headMap)
                .build();


        //通过channel发送消息
        for(int i =0;i<5;i++){
            String message = "hello --"+i;
            channel.basicPublish("tuling.directchange","tuling.directchange.key",basicProperties,message.getBytes());
        }

        //关闭连接
        channel.close();
        connection.close();
    }

}
