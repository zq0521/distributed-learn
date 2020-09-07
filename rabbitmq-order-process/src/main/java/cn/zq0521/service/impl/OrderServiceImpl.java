package cn.zq0521.service.impl;

import cn.zq0521.dao.OrderMapper;
import cn.zq0521.entity.Order_details;
import cn.zq0521.entity.Order_master;
import cn.zq0521.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional
    public Object place_Order(Order_master order_master) {
        int order_id = 0;
        try {
            orderMapper.addOrder(order_master);
            order_id = order_master.getOrder_id();
        } catch (Exception e) {
            throw new RuntimeException("新增订单失败，sql异常",e);
        }
        return order_id;
    }

    @Override
    public void change_OrderStatus(Order_master order_master) {

        try {
            orderMapper.updateOrder(order_master);
        } catch (Exception e) {
            throw new RuntimeException("订单状态变更失败，sql异常");
        }
        log.info("订单状态已变更,订单状态为：=======>" + order_master.getOrder_status());
    }


    @Override
    public void addOrderDetail(List<Order_details> list) {
        try {
            orderMapper.addOrderDetail(list);
        } catch (Exception e) {
            throw new RuntimeException("订单明细创建失败，sql异常",e);
        }
    }
}
