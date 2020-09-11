package com.zq0521.compent;

import com.zq0521.entity.MessageContent;
import com.zq0521.enumuration.MsgStatusEnum;
import com.zq0521.mapper.MsgContentMapper;
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
    private MsgContentMapper msgContentMapper;

    public static final String LOCK_KEY = "LOCK_KEY";

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
            log.info("消息ID:{}对应的消息被broker签收成功", msgId);
            updateMsgStatusWithAck(msgId);
        } else {
            log.warn("消息Id:{}对应的消息被broker签收失败:{}", msgId, cause);
            updateMsgStatusWithNack(msgId, cause);
        }
    }

    /**
     * 更新消息表状态------>已签收
     */
    private void updateMsgStatusWithAck(String msgId) {
        MessageContent messageContent = builderUpdateContent(msgId);
        messageContent.setMsgStatus(MsgStatusEnum.SENDING_SUCCESS.getCode());

        //更新数据库---->消息表
        msgContentMapper.updateMsgStatus(messageContent);
    }


    /**
     * 更新消息状态------>未签收
     *
     * @param msgId
     * @param cause
     */
    private void updateMsgStatusWithNack(String msgId, String cause) {
        MessageContent messageContent = builderUpdateContent(msgId);
        messageContent.setMsgStatus(MsgStatusEnum.CONSUMER_FAIL.getCode());
        messageContent.setErrCause(cause);

        //更新数据库---->消息表
        msgContentMapper.updateMsgStatus(messageContent);

    }

    /**
     * 消息更新构建
     *
     * @param msgId
     * @return
     */
    private MessageContent builderUpdateContent(String msgId) {
        MessageContent messageContent = new MessageContent();
        messageContent.setMsgId(msgId);
        //messageContent.setUpdateTime(new Date());
        return messageContent;
    }


}
