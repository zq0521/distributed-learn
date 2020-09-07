package com.zq0521.quickstartonclass;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消费者
 */
public class QuickConsumer {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

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

        //声明队列名称
        String queueName = "tuling-queue-01";
        //创建队列
        //参数说明：         队列的名称，消息是否持久化，是否是资源独占的，是否自动删除
        channel.queueDeclare(queueName,true,false,false,null);

        //创建我们的消费者
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        channel.basicConsume(queueName,true,queueingConsumer);

        //死循环进行消费
        while(true){
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            System.out.println("消费消息:"+new String(delivery.getBody()));

        }


    }
}
