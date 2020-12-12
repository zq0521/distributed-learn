package com.zq0521.return_listener;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * 消息不可达监听 实例
 */
public class ReturningListenerProductor {
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

        //准备发送消息
        String exchangeName = "tuling.retrun.direct";
        String okRoutingKey = "tuling.retrun.key.ok";
        String errorRoutingKey = "tuling.retrun.key.error";

        /**
         * 设置监听不可达消息
         */
        channel.addReturnListener(new TulingReturningListener());

        //设置消息属性
        Map<String,Object> infoMap = new HashMap<>();
        infoMap.put("company","tuling");
        infoMap.put("location","长沙");

        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder()
                .deliveryMode(2)   //设置消息持久化
                .correlationId(UUID.randomUUID().toString())   //消息的唯一标识ID
                .timestamp(new Date())  //时间戳
                .headers(infoMap)   //自定义的消息头
                .build();

        //设置消息体
        String msgBody = "你好 图灵...."+System.currentTimeMillis();

        /**
         * 发送消息
         * mandatory:该属性设置为false,那么不可达消息将会被mq broker删除
         *           设置为true,那么mq会调用我们的returnListener 来告知业务系统，该消息不能被成功发送至消费者
         *
         */
        channel.basicPublish(exchangeName,okRoutingKey,true,basicProperties,msgBody.getBytes());

        String errorMsg1 = msgBody+" mandatory为false....."+System.currentTimeMillis();
        //错误发送  mandatory为fales
        channel.basicPublish(exchangeName,errorRoutingKey,false,basicProperties,errorMsg1.getBytes());


        String errorMsg2 = msgBody+" mandatory为true....."+System.currentTimeMillis();
        //错误发送 mandatory为true
        channel.basicPublish(exchangeName,errorRoutingKey,true,basicProperties,errorMsg2.getBytes());


    }

}
