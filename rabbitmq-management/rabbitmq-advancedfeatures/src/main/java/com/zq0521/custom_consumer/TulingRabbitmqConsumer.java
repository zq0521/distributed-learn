package com.zq0521.custom_consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TulingRabbitmqConsumer {

    /**
     * 连接函数
     * @return
     * @throws IOException
     * @throws TimeoutException
     */
    private static Connection connection() throws IOException, TimeoutException {
        //创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.155");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("zhangsan");
        connectionFactory.setPassword("123456");
        connectionFactory.setVirtualHost("tuling");
        connectionFactory.setConnectionTimeout(100000);

        //创建连接
        Connection connection = connectionFactory.newConnection();
        return connection;

    }


    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = connection();

        //创建信道
        Channel channel = connection.createChannel();

        //声明交换机
        String exchangeName = "tuling.customconsumer.direct";
        String exchangeType = "direct";
        channel.exchangeDeclare(exchangeName,exchangeType,true,false,null);

        //声明队列
        String queueName = "tuling.customconsumer.queue";
        channel.queueDeclare(queueName,true,false,false,null);

        //交换机绑定队列
        String routingKey = "tuling.customconsumer.key";
        channel.queueBind(queueName,exchangeName,routingKey);

        //进行消费
        //在这里，将自定义的消费者传入到里面，就不用一直写死循环了
        channel.basicConsume(queueName,new TulingConsumer(channel));


    }

}
