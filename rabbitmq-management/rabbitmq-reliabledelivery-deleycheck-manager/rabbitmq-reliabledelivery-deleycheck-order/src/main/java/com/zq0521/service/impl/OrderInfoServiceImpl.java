package com.zq0521.service.impl;

import com.zq0521.bo.MsgTxtBo;
import com.zq0521.compent.MsgSender;
import com.zq0521.constants.MqConstants;
import com.zq0521.entity.MessageContent;
import com.zq0521.entity.OrderInfo;
import com.zq0521.enumuration.MsgStatusEnum;
import com.zq0521.mapper.MsgContentMapper;
import com.zq0521.mapper.OrderInfoMapper;
import com.zq0521.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class OrderInfoServiceImpl implements OrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private MsgContentMapper msgContentMapper;

    @Autowired
    private MsgSender msgSender;


    @Override
    @Transactional
    public void saveOrderInfo(OrderInfo orderInfo) {

        try {

            orderInfoMapper.saveOrderInfo(orderInfo);

        } catch (Exception e) {
            log.error("操作数据库失败:{}", e);
            throw new RuntimeException("操作数据库失败");
        }
    }

    @Override
    public void saveOrderInfoWithMessage(OrderInfo orderInfo) {
        //构建消息对象---->消息发送中....
        MessageContent messageContent = builderMessageContent(orderInfo.getOrderNo(), orderInfo.getProductNo(), MsgStatusEnum.SENDING.getCode());

        //保存消息至数据库
        saveOrderInfo(orderInfo);

        //构建消息发送对象
        MsgTxtBo msgTxtBo = new MsgTxtBo();
        msgTxtBo.setMsgId(messageContent.getMsgId());
        msgTxtBo.setOrderNo(orderInfo.getOrderNo());
        msgTxtBo.setProductNo(orderInfo.getProductNo());

        //发送业务消息
        msgSender.senderMsg(msgTxtBo);

        //发送延迟消息
        msgSender.sendDelayCheckMsg(msgTxtBo);

    }


    /**
     * 构建消息对象
     *
     * @param orderNo       订单编号
     * @param productNo     库存编号
     * @param messageStatus 消息状态
     * @return
     */
    private MessageContent builderMessageContent(long orderNo, Integer productNo, Integer messageStatus) {
        MessageContent messageContent = new MessageContent();
        //消息ID，确保该值是全局唯一,在实际业务中，需要使用订单相关的ID做锁
        String msgId = UUID.randomUUID().toString();

        messageContent.setMsgId(msgId);
        messageContent.setCreateTime(new Date());
        messageContent.setUpdateTime(new Date());
        messageContent.setExchange(MqConstants.ORDER_TO_PRODUCT_EXCHANGE_NAME);
        messageContent.setRoutingKey(MqConstants.ORDER_TO_PRODUCT_ROUTING_KEY);
        messageContent.setMsgStatus(messageStatus);
        messageContent.setOrderNo(orderNo);
        messageContent.setProductNo(productNo);
        messageContent.setMaxRetry(MqConstants.MSG_RETRY_COUNT);

        return messageContent;

    }
}
