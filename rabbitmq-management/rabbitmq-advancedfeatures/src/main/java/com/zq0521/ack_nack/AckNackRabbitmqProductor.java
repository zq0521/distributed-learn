package com.zq0521.ack_nack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
/**
 * 手动签收(ack) 和nack(requueu===>true|fasle)
 */
public class AckNackRabbitmqProductor {

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

        //定义交换机
        String exchangeName = "tuling.ack.direct";
        String routingKey = "tuling.ack.key";

        //定义消息体
        String msgBody = "你好tuling";

        for (int i = 0;i<10;i++){
            Map<String,Object> infoMap = new HashMap<>();
            infoMap.put("mark",i);

            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder()
                    .deliveryMode(2)   //消息持久化   1:非持久化  2：持久化
                    .contentEncoding("utf-8")
                    .correlationId(UUID.randomUUID().toString())
                    .headers(infoMap)
                    .build();

            channel.basicPublish(exchangeName,routingKey,basicProperties,(msgBody+i).getBytes());
        }

    }
}
