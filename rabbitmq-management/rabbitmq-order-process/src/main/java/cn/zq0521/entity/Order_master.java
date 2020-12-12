package cn.zq0521.entity;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order_master implements Serializable {

    private int order_id;
    //订单编号
    private String order_code;
    //订单类型
    private int order_type;
    //下单人ID
    private Integer customer_id;
    //收货人名称
    private String consignee_Name;
    //收货人电话
    private String consignee_number;
    //收货人地址
    private String consignee_address;
    //订单金额
    private BigDecimal order_money;
    //订单状态
    private Integer order_status;
    //支付方式
    private Integer payment_method;
    //支付时间
    private Timestamp payment_time;
    //订单创建时间
    private Timestamp create_time;
    //订单更新时间
    private Timestamp update_time;










}
