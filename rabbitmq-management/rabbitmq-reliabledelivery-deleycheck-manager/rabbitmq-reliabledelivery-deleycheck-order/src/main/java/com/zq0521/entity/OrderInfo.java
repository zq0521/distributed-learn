package com.zq0521.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class OrderInfo {

    private long orderNo;

    private Date createTime;

    private Date updateTime;

    private String userName;

    private double money;

    private Integer productNo;

}
