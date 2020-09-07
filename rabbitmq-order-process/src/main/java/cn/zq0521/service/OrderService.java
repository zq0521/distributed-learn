package cn.zq0521.service;

import cn.zq0521.entity.Order_details;
import cn.zq0521.entity.Order_master;

import java.util.List;

public interface OrderService {

    /**
     * 下订单
     * @param order_master
     * @return
     */
    Object place_Order(Order_master order_master);

    /**
     * 订单状态变更
     * @param order_master
     * @return
     */
    void change_OrderStatus(Order_master order_master);


    /**
     * 订单明细创建
     * @param list
     */
    void addOrderDetail(List<Order_details> list);




}
