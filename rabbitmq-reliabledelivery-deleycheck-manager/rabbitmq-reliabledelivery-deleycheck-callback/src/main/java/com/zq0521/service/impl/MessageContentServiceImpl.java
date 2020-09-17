package com.zq0521.service.impl;

import com.zq0521.entity.MessageContent;
import com.zq0521.exception.MsgException;
import com.zq0521.mapper.MsgContentMapper;
import com.zq0521.service.MessageContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MessageContentServiceImpl implements MessageContentService {

    @Autowired
    private MsgContentMapper msgContentMapper;

    @Transactional
    @Override
    public void saveMsgContent(MessageContent messageContent) {

        try {
            msgContentMapper.saveMsgContent(messageContent);
        } catch (Exception e) {
            log.error("插入数据异常,{}", e);
            throw new MsgException(0, "插入数据异常");
        }

    }


    @Override
    public MessageContent checkDelayMessag(String msgId) {
        try {
            MessageContent messageContent = msgContentMapper.qryMessageContentById(msgId);
            return messageContent;
        } catch (Exception e) {
            log.error("查询数据异常", e);
            throw new MsgException(0, "查询数据异常");

        }
    }
}
