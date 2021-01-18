package com.zq.rocketmq.handler.impl;

import com.zq.rocketmq.domain.User;
import com.zq.rocketmq.handler.MessageProcessor;
import org.springframework.stereotype.Service;

@Service
public class UserProcessImpl implements MessageProcessor<User> {

    @Override
    public boolean handleMessage(User message) {
        System.out.printf("User receive %s%n ", message.toString());
        return true;
    }

    @Override
    public Class<User> getClazz() {
        return User.class;
    }


}
