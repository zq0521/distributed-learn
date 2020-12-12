package com.zq0521.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {

    private String orderNo;

    private Date createDt;

    private double payMoney;

    private String userName;
}
