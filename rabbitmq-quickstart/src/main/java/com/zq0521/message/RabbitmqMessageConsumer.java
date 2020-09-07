package com.zq0521.message;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitmqMessageConsumer {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //创建连接工厂
        ConnectionFactory connectionFactory  = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.155");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("tuling");
        connectionFactory.setUsername("zhangsan");
        connectionFactory.setPassword("123456");

        //创建连接
        Connection connection = connectionFactory.newConnection();

        //创建一个channel
        Channel channel = connection.createChannel();

        String exchangeName = "tuling.directchange";
        String exchangeType = "direct";
        String queueName = "tuling.directqueue";
        String routingKey = "tuling.directchange.key";

        //声明一个交换机
        channel.exchangeDeclare(exchangeName,exchangeType,true,false,null);

        //声明一个队列
        channel.queueDeclare(queueName,true,false,false,null);

        //队列和交换机绑定
        channel.queueBind(queueName,exchangeName,routingKey);

        //创建一个消费者
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        //开始消费
        channel.basicConsume(queueName,queueingConsumer);

        //生产者在消息头上加了属性，在消费者这里可以拿出来
        while (true){
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String reserveMsg = new String(delivery.getBody());
            System.out.println("encoding:"+delivery.getProperties().getContentEncoding());
            System.out.println("company:"+delivery.getProperties().getHeaders().get("company"));
            System.out.println("correlationId:"+delivery.getProperties().getCorrelationId());
        }

    }

}
