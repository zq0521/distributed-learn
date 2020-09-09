package com.zq0521.compent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.zq0521.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 消息接受组件
 */
@Slf4j
@Component
public class MsgReceiver {

    @RabbitListener(queues = "zqBootQueue")
    @RabbitHandler
    public void consumerMsg(Message message, Channel channel) throws IOException {
        log.info("消费消息：{}", message.getPayload());

        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        log.info("接受deliverTag:{}", deliveryTag);
        channel.basicAck(deliveryTag, false);
    }

    /**
     * 监听延迟队列消息
     *
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = "zqBootDelayQueue")
    @RabbitHandler
    public void consumerDelayMsg(org.springframework.amqp.core.Message message, Channel channel) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ObjectMapper objectMapper = new ObjectMapper();
        Order order = objectMapper.readValue(message.getBody(), Order.class);
        log.info("在{},签收：{}", sdf.format(new Date()), order);

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }


    @RabbitListener(bindings =
    @QueueBinding(
            value = @Queue(
                    name = "zqBootQueue2",
                    durable = "true",
                    autoDelete = "false",
                    exclusive = "false"
            ),
            exchange = @Exchange(
                    name = "springboot.direct.exchange",
                    type = "direct",
                    durable = "true",
                    autoDelete = "fasle"),
            key = {"springboot.key2"}
    )
    )
    @RabbitHandler
    public void consumerMsg2(Message message, Channel channel) throws IOException {
        log.info("consumerMsg2===消费消息:{}", message.getPayload());
        //手工签收
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        log.info("consumerMsg2===接受deliveryTag:{}", deliveryTag);
        channel.basicAck(deliveryTag, false);
    }


    /**
     * 手动签收消息，使用core包的message
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = {"zqBootQueue2"})
    @RabbitHandler
    public void consumerOrder(org.springframework.amqp.core.Message message, Channel channel) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Order order = objectMapper.readValue(message.getBody(), Order.class);
        log.info("order:{}", order.toString());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }

}
