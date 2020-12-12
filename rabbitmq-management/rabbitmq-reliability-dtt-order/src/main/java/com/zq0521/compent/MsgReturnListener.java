package com.zq0521.compent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zq0521.bo.MsgTxtBo;
import com.zq0521.entity.MessageContent;
import com.zq0521.enumuration.MsgStatusEnum;
import com.zq0521.mapper.MsgContentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * 消息不可达监听
 */
@Component
@Slf4j
public class MsgReturnListener implements RabbitTemplate.ReturnCallback {

    @Autowired
    private MsgContentMapper msgContentMapper;


    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            //将数据转换为对象
            MsgTxtBo msgTxtBo = objectMapper.readValue(message.getBody(), MsgTxtBo.class);
            log.info("无法路由消息内容：{}，case:{}", msgTxtBo, replyText);

            //构建消息对象
            MessageContent messageContent = new MessageContent();
            messageContent.setErrCause(replyText);
            messageContent.setUpdateTime(new Date());
            messageContent.setMsgStatus(MsgStatusEnum.SENDING_FAIL.getCode());
            messageContent.setMsgId(msgTxtBo.getMsgId());

            //更新数据库------>消息表
            msgContentMapper.updateMsgStatus(messageContent);

        } catch (IOException e) {
           log.error("更新消息表异常",e);
        }

    }
}
