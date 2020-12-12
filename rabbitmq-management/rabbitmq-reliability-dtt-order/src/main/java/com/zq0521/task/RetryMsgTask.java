package com.zq0521.task;

import com.zq0521.bo.MsgTxtBo;
import com.zq0521.compent.MsgSender;
import com.zq0521.constants.MqConstants;
import com.zq0521.entity.MessageContent;
import com.zq0521.enumuration.MsgStatusEnum;
import com.zq0521.mapper.MsgContentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消息重发 定时任务
 */
@Component
@Slf4j
@EnableScheduling
public class RetryMsgTask {

    @Autowired
    private MsgSender msgSender;

    @Autowired
    private MsgContentMapper msgContentMapper;

    /**
     * 查询5分钟消息状态还没有完结的消息
     * 延时5秒启动
     * 周期10秒一次
     */
    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void retrySend() {
        log.info("定时任务启动-------------------->started");
        List<MessageContent> list = msgContentMapper.qryNeedRetryMsg(MsgStatusEnum.CONSUMER_SUCCESS.getCode(), MqConstants.TIME_DIFF);

        for (MessageContent content : list) {
            if (content.getMaxRetry() > content.getCurrentRetry()) {
                MsgTxtBo msgTxtBo = new MsgTxtBo();
                msgTxtBo.setMsgId(content.getMsgId());
                msgTxtBo.setProductNo(content.getProductNo());
                msgTxtBo.setOrderNo(content.getOrderNo());

                //更新消息重试次数
                msgContentMapper.updateMsgRetryCount(msgTxtBo.getMsgId());
                msgSender.senderMsg(msgTxtBo);
            } else {
                log.warn("消息：{} 已达到最大重试重试次数", content);
            }
        }

    }

}
