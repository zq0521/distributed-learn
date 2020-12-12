package com.zq0521.mapper;

import com.zq0521.entity.MessageContent;
import com.zq0521.mapper.provider.MsgContentProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MsgContentMapper {

    /**
     * 保存消息内容
     *
     * @param messageContent
     * @return
     */
    @Insert("INSERT INTO message_content(msg_id,create_time,update_time,msg_status,exchange,routing_key,err_cause,order_no,max_retry,current_retry,product_no) " +
            "        VALUES(#{msgId},#{createTime},#{updateTime},#{msgStatus},#{exchange},#{routingKey},#{errCause},#{orderNo},#{maxRetry},#{currentRetry},#{productNo})")
    int saveMsgContent(MessageContent messageContent);

    /**
     * 变更消息状态
     *
     * @return
     */
    @UpdateProvider(value = MsgContentProvider.class, method = "updateMsgStatus")
    int updateMsgStatus(MessageContent messageContent);

    /**
     * 查询重试的消息
     *
     * @param status
     * @param timeDiff
     * @return
     */
    @Select(" select * from message_content where TIMESTAMPDIFF(SECOND,create_time,SYSDATE())>#{timeDiff} and msg_status!=#{msgStatus} and current_retry<max_retry")
    List<MessageContent> qryNeedRetryMsg(@Param("msgStatus") Integer status, @Param("timeDiff") Integer timeDiff);

    /**
     * 更新重试消息的次数
     *
     * @param msgId
     */
    @Update("update message_content set current_retry=current_retry+1 where current_retry<max_retry and msg_id=#{msgId}")
    void updateMsgRetryCount(String msgId);


}
