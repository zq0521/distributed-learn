package com.zq0521.compent;

import com.zq0521.entity.MessageContent;
import com.zq0521.enumuration.OrderStatusEnum;
import com.zq0521.mapper.OrderInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息确认组件
 */
@Component
@Slf4j
public class MsgConfirm implements RabbitTemplate.ConfirmCallback {

    @Autowired
    private OrderInfoMapper orderInfoMapper;


    /**
     * 消息ack结果处理
     *
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("消息ACK结果处理------------->");
        //消息的ID，该ID代表该消息的表示，必须全局唯一
        String msgId = correlationData.getId();
        if (ack) {
            log.info("成功消费消息：{}", msgId);
        } else {
            dealMsgNack(msgId);
        }

    }

    /**
     * 删除订单
     */
    private void updateOrderStatus(Long orderNo) {
        orderInfoMapper.updateOrderStatusById(orderNo, OrderStatusEnum.ERROR.getCode());
    }

    /**
     * 消息未ACK
     *
     * @param msgId 消息ID
     */
    private void dealMsgNack(String msgId) {

        //表示是业务消息没有发送到broker中，那么我们就需要删除订单（真正场景是改变订单的状态为：作废）
        if (!msgId.contains("delay")) {
            log.info("发送业务消息失败：{}", msgId);
            long orderNo = Long.parseLong(msgId.split("_")[1]);

            //删除订单
            updateOrderStatus(orderNo);
        } else {
            //检查消息发送失败，那么不做可靠性检查，需要重新发送消息
            log.info("延迟消息发送没有成功：{}", msgId);
        }
    }
}
