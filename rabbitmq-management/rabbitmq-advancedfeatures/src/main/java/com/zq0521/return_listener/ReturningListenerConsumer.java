package com.zq0521.return_listener;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
/**
 * 消息不可达监听 实例
 */
public class ReturningListenerConsumer {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
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

        //声明交换机
        String exchangeName = "tuling.retrun.direct";
        String exchangeType ="direct";
        channel.exchangeDeclare(exchangeName, exchangeType,true,false,null);

        //声明队列
        String queueName = "t04.retrunlistener.queue";
        channel.queueDeclare(queueName,true,false,false,null);


        //交换机绑定队列
        String routingKey = "tuling.retrun.key.ok";
        channel.queueBind(queueName,exchangeName,routingKey);

        //消费者创建
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        //消费消息
        channel.basicConsume(queueName,true,queueingConsumer);

        while (true){
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            System.out.println("接受的消息为："+ new String(delivery.getBody()));
        }




    }

}
