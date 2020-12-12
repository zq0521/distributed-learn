package cn.zq0521.entity;

import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Warehouse_product implements Serializable {
    //商品库存ID
    private int warehouse_product_id;
    //商品ID
    private int product_id;
    //仓库ID
    private int w_id;
    //当前库存量
    private int current_cnt;
    //最后修改时间
    private Timestamp modified_time;

}
