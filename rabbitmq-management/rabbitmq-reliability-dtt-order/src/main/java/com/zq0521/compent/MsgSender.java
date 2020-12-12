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

/**
 * 消息发送组件
 */
@Component
@Slf4j
public class MsgSender implements InitializingBean {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MsgConfirm msgConfirm;

    @Autowired
    private MsgReturnListener msgReturnListener;

    /**
     * 真正的发送消息
     *
     * @param msgTxtBo
     */
    public void senderMsg(MsgTxtBo msgTxtBo) {
        log.info("发送的消息ID:{}", msgTxtBo.getMsgId());
        //设置消息的头ID
        CorrelationData correlationData = new CorrelationData(msgTxtBo.getMsgId());
        //发送消息
        rabbitTemplate.convertAndSend(MqConstants.ORDER_TO_PRODUCT_EXCHANGE_NAME, MqConstants.ORDER_TO_PRODUCT_ROUTING_KEY, msgTxtBo, correlationData);


    }

    /**
     * 在Bean进行初始化的时候，就设置rabbitmqTemplate的属性
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        rabbitTemplate.setConfirmCallback(msgConfirm);
        rabbitTemplate.setReturnCallback(msgReturnListener);

        //设置消息转换器--->json格式
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
    }
}
