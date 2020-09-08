package cn.zq0521.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * order消息发送成功后，手动ACK确认
 */
@Slf4j
public class OrdermqConsumer implements ChannelAwareMessageListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {

        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        log.info("订单发送消息至broker已消费，ACK--------->");

        //发送消息----->broker ,确认已消费完毕
        rabbitTemplate.convertAndSend("stack.exchange","stack.key",message);
    }
}
