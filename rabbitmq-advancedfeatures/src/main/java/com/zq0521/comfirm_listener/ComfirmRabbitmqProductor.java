package com.zq0521.comfirm_listener;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class ComfirmRabbitmqProductor {

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

        //设置消息投递模式（确认模式）
        channel.confirmSelect();

        //准备发送消息
        String exchangeName = "tuling.confirm.topicexchange";
        String routingKey = "tuling.confirm.key";

        //设置消息属性
        Map<String,Object> mapInfo = new HashMap<>();
        mapInfo.put("company","tuling");
        mapInfo.put("location","长沙");

        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder()
                .deliveryMode(2)
                .correlationId(UUID.randomUUID().toString())
                .contentEncoding("utf-8")
                .timestamp(new Date())
                .headers(mapInfo)
                .build();

        //设置消息体
        String msgBody = "你好 图灵....";

        /**
         * 确认消息监听
         */
        channel.addConfirmListener(new TulingConfirmListener());

        //进行消费
        channel.basicPublish(exchangeName,routingKey,basicProperties,msgBody.getBytes());

        /**
         * 这里不能调用channel.close ,不然消费就不能接受确认了
         */
    }

}
