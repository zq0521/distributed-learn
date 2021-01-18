package com.zq.rocketmq.handler;

import java.io.IOException;

public interface MessageProcessor<T> {

    // 处理消息接口
    boolean handleMessage(T message);

    // 使用对应消息的实体类
    Class<T> getClazz();

    default T transferMessage(String message) throws IOException {
        return JsonUtil.fromJson(message, getClazz());
    }

}
