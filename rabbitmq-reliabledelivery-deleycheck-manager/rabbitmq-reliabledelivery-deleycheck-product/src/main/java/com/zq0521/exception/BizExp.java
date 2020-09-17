package com.zq0521.exception;

import lombok.Data;

@Data
public class BizExp extends RuntimeException {

    private Integer code;

    private String errMsg;

    public BizExp(Integer code, String errMsg) {
        super();
        this.code = code;
        this.errMsg = errMsg;
    }


}
