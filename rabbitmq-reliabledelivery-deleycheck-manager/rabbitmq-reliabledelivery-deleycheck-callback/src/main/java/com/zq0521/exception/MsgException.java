package com.zq0521.exception;

import lombok.Getter;

@Getter
public class MsgException extends RuntimeException {

    private Integer code;
    private String msgStatus;

    public MsgException(Integer code, String msgStatus) {
        super();
        this.code = code;
        this.msgStatus = msgStatus;
    }

}
