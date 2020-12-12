package com.zq0521.entity;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Order {

    private String orderNo;

    private Date createDt;

    private double payMoney;

    private String userName;

}
