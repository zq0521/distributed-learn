package com.zq0521.compent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.zq0521.bo.MsgTxtBo;
import com.zq0521.constants.MqConstants;
import com.zq0521.entity.MessageContent;
import com.zq0521.enumration.MsgStatusEnum;
import com.zq0521.exception.MsgException;
import com.zq0521.service.MessageContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
public class CallBackMqConsumer {

    private static final String LOCK_KEY = "lock:callback:";

    @Autowired
    private MessageContentService messageContentService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 监听库存业务
     *
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = MqConstants.PRODUCT_TO_CALLBACK_QUEUE_NAME)
    @RabbitHandler
    public void consumerConfirmMsg(Message message, Channel channel) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        MsgTxtBo msgTxtBo = objectMapper.readValue(message.getBody(), MsgTxtBo.class);

        long deliveryTag = message.getMessageProperties().getDeliveryTag();


        //分布式锁
        if (redisTemplate.opsForValue().setIfAbsent(LOCK_KEY + msgTxtBo.getMsgId(), msgTxtBo.getMsgId())) {

            try {
                MessageContent messageContent = messageBuilder(msgTxtBo);
                log.info("消费消息确认：{}", msgTxtBo);
                messageContent.setMsgStatus(MsgStatusEnum.CONSUMER_SUCCESS.getCode());
                messageContent.setRoutingKey(message.getMessageProperties().getReceivedRoutingKey());
                messageContent.setExchange(message.getMessageProperties().getReceivedExchange());
                //插入消息
                messageContentService.saveMsgContent(messageContent);

                //签收消息
                channel.basicAck(deliveryTag, false);
            } catch (Exception e) {
                log.error("插入消息记录异常：{}", e);
                //数据业务异常，需要删除锁，进行重新操作
                if (e instanceof MsgException) {
                    //删除锁1
                    redisTemplate.delete(LOCK_KEY + msgTxtBo.getMsgId());
                }

                //记录异常消息，尝试插入数据库
                MsgException exp = (MsgException) e;
                MessageContent errMsgContent = messageBuilder(msgTxtBo);
                errMsgContent.setErrCause(exp.getMessage());
                errMsgContent.setRoutingKey(message.getMessageProperties().getReceivedRoutingKey());
                errMsgContent.setExchange(message.getMessageProperties().getReceivedExchange());
                errMsgContent.setMsgStatus(MsgStatusEnum.CONSUMER_FAIL.getCode());
                //插入消息
                messageContentService.saveMsgContent(errMsgContent);
                //拒绝消息
                channel.basicReject(deliveryTag, false);
            }


        } else {
            log.warn("请不要重复消费消息：{}", msgTxtBo);
            //拒绝确认消息
            channel.basicReject(deliveryTag, false);
            return;
        }

        //删除分布式锁
        redisTemplate.delete(LOCK_KEY + msgTxtBo.getMsgId());


    }

    /**
     * 监听延迟检查消息---->做消息的延迟对比
     *
     * @param message
     * @param channel
     */
    @RabbitListener(queues = MqConstants.ORDER_TO_PRODUCT_DELAY_QUEUE_NAME)
    @RabbitHandler
    public void consumerCheckMsg(Message message, Channel channel) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        MsgTxtBo msgTxtBo = objectMapper.readValue(message.getBody(), MsgTxtBo.class);

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        //取出延时消息的实际消息ID
        String msgId = msgTxtBo.getMsgId().replace("_delay", "");

        //延时检查对比
        MessageContent messageContent = messageContentService.checkDelayMessag(msgTxtBo.getMsgId());

        log.info("消息延时消费：{}", msgTxtBo.toString());

        //如果没有该消息，证明消费者没有发送消息确认，需要回调生产者重新发送消息
        if (messageContent == null) {
            //回调订单服务
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForEntity("http://localhost:8001/retryMsg", msgTxtBo, String.class);
        }

        //签收消息
        channel.basicAck(deliveryTag, false);


    }


    /**
     * 消息内容构造
     *
     * @param msgTxtBo
     * @return
     */
    private MessageContent messageBuilder(MsgTxtBo msgTxtBo) {
        MessageContent messageContent = new MessageContent();
        messageContent.setMsgId(msgTxtBo.getMsgId());
        messageContent.setOrderNo(msgTxtBo.getOrderNo());
        messageContent.setProductNo(msgTxtBo.getProductNo());
        messageContent.setUpdateTime(new Date());
        messageContent.setCreateTime(new Date());
        messageContent.setMaxRetry(MqConstants.MSG_RETRY_COUNT);

        return messageContent;
    }


}
