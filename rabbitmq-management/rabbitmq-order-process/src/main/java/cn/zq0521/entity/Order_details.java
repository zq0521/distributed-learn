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
public class Order_details implements Serializable {

    private int order_details_id;
    //订单ID
    private int order_id;
    //商品ID
    private int product_id;
    //商品名称
    private String product_name;
    //商品数量
    private int product_cnt;
    //商品单价
    private BigDecimal product_price;
    //仓库ID
    private int w_id;
    //最后修改时间
    private Timestamp modified_time;


}
