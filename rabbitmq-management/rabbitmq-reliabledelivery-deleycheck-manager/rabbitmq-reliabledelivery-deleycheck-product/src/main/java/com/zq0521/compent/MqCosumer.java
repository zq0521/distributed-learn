package com.zq0521.compent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.zq0521.bo.MsgTxtBo;
import com.zq0521.constants.MqConstants;
import com.zq0521.exception.BizExp;
import com.zq0521.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class MqCosumer {

    private static final String LOCK_KEY = "lock.key";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductService productService;

    @Autowired
    private MsgSender msgSender;


    @RabbitListener(queues = MqConstants.ORDER_TO_PRODUCT_QUEUE_NAME)
    @RabbitHandler
    public void consumerMsgWithLock(Message message, Channel channel) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        MsgTxtBo msgTxtBo = objectMapper.readValue(message.getBody(), MsgTxtBo.class);

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        if (redisTemplate.opsForValue().setIfAbsent(LOCK_KEY + msgTxtBo.getMsgId(), msgTxtBo.getMsgId())) {
            log.info("消费消息：{}", msgTxtBo);

            try {
                productService.updateProductStore(msgTxtBo);

                //发送一条确认消息至callBack服务上
                msgSender.sendMsg(msgTxtBo);

                //消息签收
                channel.basicAck(deliveryTag, false);
            } catch (Exception e) {
                if (e instanceof BizExp) {
                    BizExp bizExp = (BizExp) e;
                    log.info("业务数据异常：{}", ((BizExp) e).getCode());
                    redisTemplate.delete(LOCK_KEY + msgTxtBo.getMsgId());
                }
            }

        } else {
            log.warn("消息被重复消费，该消息为：{}", msgTxtBo);
            channel.basicReject(deliveryTag, false);
            return;
        }

        //删除锁
        redisTemplate.delete(LOCK_KEY + msgTxtBo.getMsgId());

    }
 

}
