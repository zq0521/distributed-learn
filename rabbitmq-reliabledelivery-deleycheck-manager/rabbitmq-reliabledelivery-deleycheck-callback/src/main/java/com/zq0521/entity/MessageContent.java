package com.zq0521.entity;

import lombok.Data;

import java.util.Date;

/**
 * 消息内容 实体类
 */
@Data
public class MessageContent {

    private String msgId;

    private long orderNo;

    private Date createTime;

    private Date updateTime;

    private Integer msgStatus;

    private String exchange;

    private String routingKey;

    private String errCause;

    private Integer maxRetry;

    private Integer currentRetry = 0;

    private Integer productNo;

}
