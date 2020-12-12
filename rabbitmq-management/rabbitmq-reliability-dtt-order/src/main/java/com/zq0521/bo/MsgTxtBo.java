package com.zq0521.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 消息文本对象
 */
@Data
public class MsgTxtBo implements Serializable {
    private long orderNo;

    private int productNo;

    private String msgId;

}
