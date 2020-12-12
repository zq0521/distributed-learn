package com.zq0521.compent;

import com.zq0521.bo.MsgTxtBo;
import com.zq0521.constants.MqConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MsgSender implements InitializingBean {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 发送消息
     *
     * @param msgTxtBo
     */
    public void sendMsg(MsgTxtBo msgTxtBo) {
        log.info("发送的消息ID:{}", msgTxtBo.getMsgId());
        CorrelationData correlationData = new CorrelationData(msgTxtBo.getMsgId() + "_" + msgTxtBo.getOrderNo());
        rabbitTemplate.convertAndSend(MqConstants.PRODUCT_TO_CALLBACK_EXCHANGE_NAME, MqConstants.PRODUCT_TO_CALLBACK_ROUTING_KEY, msgTxtBo, correlationData);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        //设置消息转换器
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
    }
}
