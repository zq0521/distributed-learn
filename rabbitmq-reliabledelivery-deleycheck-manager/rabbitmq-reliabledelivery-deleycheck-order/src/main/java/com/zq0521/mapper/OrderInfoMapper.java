package com.zq0521.mapper;

import com.zq0521.entity.OrderInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OrderInfoMapper {

    @Insert(" INSERT INTO order_info(order_no,create_time,update_time,user_name,money,product_no) " +
            "        VALUES(#{orderNo},#{createTime},#{updateTime},#{userName},#{money},#{productNo})")
    int saveOrderInfo(OrderInfo orderInfo);

    /**
     * 删除订单
     *
     * @param orderNo
     * @param orderStatus
     * @return
     */
    @Update("update order_info set order_status=#{order_status} where order_no = #{orderNo}" +
            "        and order_stauts=0")
    int updateOrderStatusById(Long orderNo, Integer orderStatus);

}
