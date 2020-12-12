package com.zq0521.fanout_exchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq 广播交换机  消息消费者
 */
public class FanoutExchangeConsumer {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

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

        //声明交换机
        String exchangeName = "tuling.fanoutExchange";
        String exchangeTyep ="fanout";
        channel.exchangeDeclare(exchangeName,exchangeTyep,true,true,null);

        //声明队列
        String queueName = "tuling.fanout.queue";
        channel.queueDeclare(queueName,true,true,false,null);

        //声明绑定关系
        String bindingStr = "aaddss";
        channel.queueBind(queueName,exchangeName,bindingStr);

        //声明消费者
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        //开始消费
        channel.basicConsume(queueName,queueingConsumer);

        while (true){
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            System.out.println("接收到消息："+new String(delivery.getBody()));
        }


    }


}
