package com.zq0521.comfirm_listener;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ComfirmRabbitmqConsumer {

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
        String exchangeName = "tuling.confirm.topicexchange";
        String exchangeType = "topic";
        channel.exchangeDeclare(exchangeName, exchangeType, true, false, null);

        //声明队列
        String queueName = "tuling.confirm.queue";
        channel.queueDeclare(queueName, true, false, false, null);

        //消息绑定至交换机
        String routingKey = "tuling.confirm.#";
        channel.queueBind(queueName, exchangeName, routingKey);

        //创建消费者
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        //进行消费
        channel.basicConsume(queueName, queueingConsumer);

        while (true) {
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            System.out.println("消费端在:" + System.currentTimeMillis() + "消费:" + new String(delivery.getBody()));
            System.out.println("correlationId:" + delivery.getProperties().getCorrelationId());

        }


    }


}
