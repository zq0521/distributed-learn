package com.zq.rocketmq;

import com.alibaba.fastjson.JSON;
import com.zq.rocketmq.constant.MessageConstant;
import com.zq.rocketmq.domain.OrderPaidEvent;
import com.zq.rocketmq.domain.User;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MimeTypeUtils;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringRocketMQTest {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public static final String TOPIC = "TopicTest";

    /**
     * 发送基本消息
     * 字符串类型、对象类型
     */
    @Test
    public void sendBasicMessage() {
        //  发送字符串类型消息  ----> 同步发送
        SendResult result = null;
        result = rocketMQTemplate.syncSend(TOPIC, "Hello RocketMQ");
        System.out.printf("发送字符串类型消息：syncSend to topic %s sendResult=%s %n", TOPIC, result);

        // 发送对象类型消息 ————————>同步发送
        result = rocketMQTemplate.syncSend(TOPIC, new User().setUserAge((byte) 22).setUserName("Kitty"));
        System.out.printf("发送对象类型消息：syncSend to topic %s sendResult=%s %n", TOPIC, result);

        // 发送对象类型消息 ——————————>同步发送
        rocketMQTemplate.syncSend(TOPIC, MessageBuilder.withPayload(
                new User().setUserAge((byte) 21).setUserName("zhangsan"))
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE).build());

        // 格式化进行发送
        rocketMQTemplate.convertAndSend(TOPIC, "Hello RocketMQ---2222");
        // 格式化进行发送
        rocketMQTemplate.convertAndSend(TOPIC, MessageBuilder.withPayload(
                new User().setUserAge((byte) 18).setUserName("lisi"))
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_HTML_VALUE).build());
        System.out.printf("%n%s","lisi");
    }

    @Test
    public void sendObjectMessage(){
        String dis = MessageConstant.ORDER_PAID_TAG+":"+"orderPaidEvent";
        //发送对象消息
        rocketMQTemplate.asyncSend(dis, JSON.toJSONString(new OrderPaidEvent("T_001", new BigDecimal("88.00"))).getBytes(), new SendCallback() {
            @Override
            public void onSuccess(SendResult var1) {
                System.out.printf("async onSucess SendResult=%s %n", var1);
            }

            @Override
            public void onException(Throwable var1) {
                System.out.printf("async onException Throwable=%s %n", var1);
            }
        });
    }

}
