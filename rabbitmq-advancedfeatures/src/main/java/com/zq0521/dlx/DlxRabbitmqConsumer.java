package com.zq0521.dlx;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class DlxRabbitmqConsumer {

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

        //声明正常的队列
        String nomalExchangeName = "tuling.nomaldlx.exchange";
        String exchangeType = "topic";
        String nomalqueueName = "tuling.nomaldex.queue";
        String routingKey = "tuling.dlx.#";

        //声明死信队列
        String dlxExhcangeName = "tuling.dlx.exchange";
        String dlxQueueName = "tuling.dlx.queue";

        //创建正常的交换机
        channel.exchangeDeclare(nomalExchangeName,exchangeType,true,false,null);

        Map<String,Object> queueArgs = new HashMap<>();
        //正常队列上绑定死信队列
        queueArgs.put("x-dead-letter-exchange",dlxExhcangeName);
        queueArgs.put("x-max-length",4);
        channel.queueDeclare(nomalqueueName,true,false,false,queueArgs);
        channel.queueBind(nomalqueueName,nomalExchangeName,routingKey);


        //声明死信队列
        channel.exchangeDeclare(dlxExhcangeName,exchangeType,true,false,null);
        channel.queueDeclare(dlxQueueName,true,false,false,null);
        channel.queueBind(dlxQueueName,dlxExhcangeName,"#");

        //消费消息
        channel.basicConsume(nomalqueueName,false,new DlxConsumer(channel));


    }
}
