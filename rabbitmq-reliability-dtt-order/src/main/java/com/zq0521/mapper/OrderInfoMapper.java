package com.zq0521.mapper;

import com.zq0521.entity.OrderInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderInfoMapper {

    @Insert(" INSERT INTO order_info(order_no,create_time,update_time,user_name,money,product_no) " +
            "        VALUES(#{orderNo},#{createTime},#{updateTime},#{userName},#{money},#{productNo})")
    int saveOrderInfo(OrderInfo orderInfo);

}
