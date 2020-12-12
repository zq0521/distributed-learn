package com.zq0521.direct_exchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq直接交换机directExchange消息消费者
 */
public class DirectExchangeConsumer {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

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

        String exchangeName = "tuling.directchange";
        String exchangeType ="direct";
        String queueName = "tuling.directqueue";
        String routingKey = "tuling.directchange.key";

        /**
         *声明一个交换机
         * exchange: 交换机的名称
         * type: 交换机的类型，常见的有direct,fanout,topic等
         * durable: 设置是否持久化。durable设置为true时表示持久化，反之非持久化。持久化可以将交换器存入到磁盘中，在服务器重启的时候不会丢失相关消息
         * autoDelete: 设置是否自动删除。autoDelete设置为true时，表示自动删除。自动删除的前提条件是至少有一个队列或者交换机与这个交换机绑定，之后，所有与这个交换机绑定的队列或者交换机都与此交换机解绑
         * 不能错误的理解一旦与此交换机连接的客户端断开连接时，RabbitMq就会删除本交换机
         * arguments: 其他一些结构化的参数，如：alternate-exchange
         *
         */
        channel.exchangeDeclare(exchangeName,exchangeType,true,false,null);

        /**
         * 声明一个队列
         * durable: 表示rabbitmq关闭删除队列
         * autoDelete: 表示没有程序和队列建立连接，那么就会自动删除队列
         */
        channel.queueDeclare(queueName,true,false,false,null);

        /**
         * 队列和交换机绑定
         * queueName: 队列名称
         * exchangeName: 交换机名称
         * routingKey: 绑定的key
         */
        channel.queueBind(queueName,exchangeName,routingKey);

        /**
         * 创建一个消费者
         */
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        /**
         * 开始消费
         */
        channel.basicConsume(queueName,true,queueingConsumer);

        while (true){
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String reciverMessage = new String(delivery.getBody());
            System.out.println("消费消息："+reciverMessage);
        }


    }
}
