package com.zq0521.service;

import com.zq0521.entity.MessageContent;

public interface MessageContentService {

    /**
     * 保存消息数据
     *
     * @param messageContent
     */
    void saveMsgContent(MessageContent messageContent);

    /**
     * 根据消息ID查询消息
     *
     * @param msgId
     * @return
     */
    MessageContent checkDelayMessag(String msgId);


}
