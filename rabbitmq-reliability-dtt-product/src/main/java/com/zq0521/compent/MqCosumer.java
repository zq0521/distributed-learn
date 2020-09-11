package com.zq0521.compent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.zq0521.bo.MsgTxtBo;
import com.zq0521.entity.MessageContent;
import com.zq0521.enumration.MsgStatusEnum;
import com.zq0521.exception.BizExp;
import com.zq0521.mapper.MsgContentMapper;
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

    /**
     * 队列名称
     */
    public static final String Order_TO_PRODUCT_QUEUE_NAME = "order-to-product.queue";

    public static final String LOCK_KEY = "LOCK_KEY";

    @Autowired
    private ProductService productService;

    @Autowired
    private MsgContentMapper msgContentMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 没有加分布式锁，可能存在重复消费的问题
     *
     * @param message
     * @param channel
     *//*
    @RabbitListener(queues = {Order_TO_PRODUCT_QUEUE_NAME})
    @RabbitHandler
    public void comsumerMsg(Message message, Channel channel) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        MsgTxtBo msgTxtBo = objectMapper.readValue(message.getBody(), MsgTxtBo.class);

        log.info("消费消息：{}", msgTxtBo);

        Long deliverTage = (Long) message.getMessageProperties().getDeliveryTag();

        try {
            //更新数据库---->更新消息表业务
            productService.updateProductStore(msgTxtBo);
            //消息签收
            channel.basicAck(deliverTage, false);
        } catch (Exception e) {

            //抛出异常，更新消息表失败
            //更新消息表状态
            MessageContent messageContent = new MessageContent();
            messageContent.setMsgStatus(MsgStatusEnum.CONSUMER_FAIL.getCode());
            messageContent.setUpdateTime(new Date());
            messageContent.setMsgId(msgTxtBo.getMsgId());
            //更新数据表
            msgContentMapper.updateMsgStatus(messageContent);
            //拒绝消费该消息
            channel.basicReject(deliverTage, false);
        }
    }*/

    @RabbitListener(queues = {Order_TO_PRODUCT_QUEUE_NAME})
    @RabbitHandler
    public void consumerMsgWithLock(Message message, Channel channel) throws IOException {
        //JSON格式转换
        ObjectMapper objectMapper = new ObjectMapper();
        MsgTxtBo msgTxtBo = objectMapper.readValue(message.getBody(), MsgTxtBo.class);

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        //加分布式锁
        if (redisTemplate.opsForValue().setIfAbsent(LOCK_KEY + msgTxtBo.getMsgId(), msgTxtBo.getMsgId())) {
            log.info("消费消息:{}", msgTxtBo);

            try {
                //业务处理
                productService.updateProductStore(msgTxtBo);

                //模拟抛出异常，签收失败
                //System.out.println(1 / 0);

                //消息签收
                channel.basicAck(deliveryTag, false);
                log.info("消息已确认...------>success");


            } catch (Exception e) {

                /**
                 * 更新数据表异常说明业务没有操作成功，需要删除分布式锁
                 */
                if (e instanceof BizExp) {
                    BizExp bizExp = (BizExp) e;
                    //业务数据异常，删除分布式锁
                    log.info("业务数据异常：{}，即将删除分布式锁：", bizExp.getErrMsg());
                    //删除分布式锁
                    redisTemplate.delete(LOCK_KEY + msgTxtBo.getMsgId());
                }

                MessageContent messageContent = new MessageContent();
                messageContent.setMsgId(msgTxtBo.getMsgId());
                //messageContent.setUpdateTime(new Date());
                messageContent.setMsgStatus(MsgStatusEnum.CONSUMER_FAIL.getCode());
                messageContent.setErrCause(e.getMessage());
                //更新数据表----->消息签收失败的状态
                msgContentMapper.updateMsgStatus(messageContent);
                //拒绝签收
                channel.basicReject(deliveryTag, false);
                return;

            }
        } else {
            log.warn("请不要重复消费消息：{}", msgTxtBo);
            //业务未完成，还是拒绝消费该消息
            channel.basicReject(deliveryTag, false);
            return;
        }

        //删除分布式锁
        redisTemplate.delete(LOCK_KEY+msgTxtBo.getMsgId());
    }


}
