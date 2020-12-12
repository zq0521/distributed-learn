package cn.zq0521.dao;

import cn.zq0521.entity.Order_details;
import cn.zq0521.entity.Order_master;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    //新增订单
    @Insert("insert into order_master" +
            "(order_code,order_type,customer_id,consignee_Name,consignee_number,consignee_address,order_money,order_status,create_time)" +
            "values(#{order_code},#{order_type},#{customer_id},#{consignee_Name},#{consignee_number},#{consignee_address},#{order_money},#{order_status},#{create_time})")
    @SelectKey(statement = "select last_insert_id()", keyProperty = "order_id", keyColumn = "order_id", before = false, resultType = int.class)
    void addOrder(Order_master order_master);

    //订单状态变更
    @UpdateProvider(type = OrderProvider.class, method = "updateOrderStatus")
    void updateOrder(Order_master order_master);

    //订单明细表新增
    @Insert({
            "<script>",
            "insert into order_details(order_id,product_id,product_name,product_cnt,product_price,w_id,modified_time)",
            "values",
            "<foreach collection='list' item='item' index='index' separator=','>",
            "(#{item.order_id},#{item.product_id},#{item.product_name},#{item.product_cnt},#{item.product_price},#{item.w_id},#{item.modified_time})",
            "</foreach>",
            "</script>"
    })
    void addOrderDetail(@Param("list") List<Order_details> list);


}
