package com.zq.rocketmq.handler.impl;

import com.zq.rocketmq.domain.ProductWithPayload;
import com.zq.rocketmq.handler.MessageProcessor;
import org.springframework.stereotype.Service;

@Service
public class ProductWithPayloadProcessorImpl implements MessageProcessor<ProductWithPayload> {


    @Override
    public boolean handleMessage(ProductWithPayload message) {
        System.out.printf("%s%n 实体类序列化处理",message);
        return true;
    }

    @Override
    public Class getClazz() {
        return ProductWithPayload.class;
    }
}
