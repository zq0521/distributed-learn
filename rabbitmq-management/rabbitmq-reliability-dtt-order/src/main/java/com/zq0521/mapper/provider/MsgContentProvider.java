package com.zq0521.mapper.provider;

import com.zq0521.entity.MessageContent;

public class MsgContentProvider {


    /**
     * 变更消息状态
     *
     * @param messageContent
     * @return
     */
    public String updateMsgStatus(MessageContent messageContent) {
        StringBuilder sql = new StringBuilder("update message_content set ");

        if (messageContent.getMsgStatus() != null && !messageContent.getMsgStatus().equals("")) {
            sql.append(" msg_status=" + messageContent.getMsgStatus());
        }
        if (messageContent.getUpdateTime() != null) {
            sql.append(", update_time=" + messageContent.getUpdateTime());
        }

        if (messageContent.getErrCause() != null && messageContent.getErrCause() != "") {
            sql.append(",err_cause=" + messageContent.getErrCause());
        }

        sql.append(" where msg_id='" + messageContent.getMsgId()+"'");
        sql.append(" and  current_retry<max_retry and msg_status!=4");

        return sql.toString();
    }
}
