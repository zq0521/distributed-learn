package cn.zq0521.service.impl;

import cn.zq0521.dao.OrderMapper;
import cn.zq0521.entity.Order_details;
import cn.zq0521.entity.Order_master;
import cn.zq0521.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public Object place_Order(Order_master order_master) {
        int order_id = 0;
        try {
            orderMapper.addOrder(order_master);
            order_id = order_master.getOrder_id();
        } catch (Exception e) {
            throw new RuntimeException("新增订单失败，sql异常", e);
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
            throw new RuntimeException("订单明细创建失败，sql异常", e);
        }
    }


    @Override
    public void sendMessageToBroker(Order_details order_details) {
        ObjectMapper objectMapper = new ObjectMapper();
        String orderJson = "";
        try {
            orderJson = objectMapper.writeValueAsString(order_details);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //构造消息头
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        messageProperties.getHeaders().put("__TypeId__", "cn.zq0521.entity.Order_details");

        //放置消息体及消息头
        Message orderMsg = new Message(orderJson.getBytes(), messageProperties);
        //发送
        rabbitTemplate.convertAndSend("order.exchange", "order.key", orderMsg);

        //模拟发送延时消息
        try {
            Thread.sleep(10000);
            rabbitTemplate.convertAndSend("callback.exchange", "callback.key", orderMsg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
