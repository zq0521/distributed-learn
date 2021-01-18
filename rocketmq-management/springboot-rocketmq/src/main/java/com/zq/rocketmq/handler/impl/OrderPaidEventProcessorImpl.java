package com.zq.rocketmq.handler.impl;

import com.zq.rocketmq.domain.OrderPaidEvent;
import com.zq.rocketmq.handler.MessageProcessor;
import org.springframework.stereotype.Service;

@Service
public class OrderPaidEventProcessorImpl implements MessageProcessor<OrderPaidEvent> {


    @Override
    public boolean handleMessage(OrderPaidEvent message) {
        System.out.printf(" OrderPaidEvent receive %s %n", message.toString());
        return true;
    }

    @Override
    public Class<OrderPaidEvent> getClazz() {
        return OrderPaidEvent.class;
    }
}
