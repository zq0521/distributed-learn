package com.zq.rocketmq.basic;

import com.zq.rocketmq.handler.MessageProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageListen implements MessageListenerConcurrently {

    //MessageProcessor接口的实现类放进map集合 key：tag value：MessageProcessor实现类
    private Map<String, MessageProcessor> handleMap = new HashMap<>();

    // 将实现类注册至该map中
    public void registerHandler(String tags, MessageProcessor messageProcessor) {
        handleMap.put(tags, messageProcessor);
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        MessageExt ext = msgs.get(0);
        String message = new String(ext.getBody());
        // 获取对应的tag
        String tags = ext.getTags();

        if (StringUtils.isEmpty(tags)) {
            throw new RuntimeException("tags不能为空");
        }

        // 根据tags从handlerMap中获取到我们的处理类
        MessageProcessor messageProcessor = handleMap.get(tags);
        Object obj = null;
        // 将String类型的message反序列化成对象
        try {
            obj = messageProcessor.transferMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("%s%n 反序列化失败");
        }

        // 处理消息
        boolean result = messageProcessor.handleMessage(message);
        if (!result) {
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
