package com.zq0521.topic_exchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq 主题交换机   topicExchange消费端
 */
public class TopicExchangeConsumer {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.155");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("tuling");
        connectionFactory.setUsername("zhangsan");
        connectionFactory.setPassword("123456");
        connectionFactory.setConnectionTimeout(10000);

        //创建连接
        Connection connection = connectionFactory.newConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //声明交换机
        String exchangeName = "tuling.topicExchange";
        String exchangeTyep ="topic";
        channel.exchangeDeclare(exchangeName,exchangeTyep,true,true,null);

        //声明队列
        String queueName = "tuling.topic.queue";
        channel.queueDeclare(queueName,true,false,false,null);

        //声明绑定关系
        String bindingStr = "tuling.*";
        channel.queueBind(queueName,exchangeName,bindingStr);

        //声明一个消费者
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        //开始消费
        channel.basicConsume(queueName,true,queueingConsumer);

        while (true){
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            System.out.println("接受消息："+new String(delivery.getBody()));
        }





    }



}
