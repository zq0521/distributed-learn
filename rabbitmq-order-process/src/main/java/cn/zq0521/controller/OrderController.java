package cn.zq0521.controller;

import cn.zq0521.entity.Order_details;
import cn.zq0521.entity.Order_master;
import cn.zq0521.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/put")
    public Object shopping() throws IOException, TimeoutException {
        List<Order_details> list = new ArrayList<>();

        Order_details order_details1 = new Order_details();
        order_details1.setProduct_cnt(2);
        order_details1.setProduct_price(new BigDecimal("20.3"));
        order_details1.setProduct_id(1001);
        order_details1.setW_id(751);
        order_details1.setProduct_name("牙膏");

//        Order_details order_details2 = new Order_details();
//        order_details2.setProduct_cnt(3);
//        order_details2.setProduct_price(new BigDecimal("5.4"));
//        order_details2.setProduct_id(1002);
//        order_details2.setW_id(761);
//        order_details2.setProduct_name("牙刷");

        list.add(order_details1);
//        list.add(order_details2);


        BigDecimal countMAX = new BigDecimal("0");
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Order_details order_details = list.get(i);
                BigDecimal price = order_details.getProduct_price().multiply(new BigDecimal(order_details.getProduct_cnt()));
                countMAX = price.add(countMAX);
            }
        }

        //订单主表处理
        Order_master order_master = new Order_master();
        order_master.setConsignee_address("佛山市莉华电子科技");
        order_master.setConsignee_Name("张强");
        order_master.setConsignee_number("136xxxx2960");
        order_master.setCreate_time(getNowTimestamp());
        order_master.setCustomer_id(1);
        order_master.setOrder_code(UUID.randomUUID().toString());
        order_master.setOrder_id(1);
        order_master.setOrder_type(1);
        order_master.setOrder_money(countMAX);
        order_master.setOrder_status(1);

        //下单操作
        Integer order_id = null;
        try {
            order_id = (Integer) orderService.place_Order(order_master);
        } catch (Exception e) {
            log.error("创建订单失败..", e);
        }


        //明细表操作
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Order_details order_details = list.get(i);
                order_details.setOrder_id(order_id);
            }
        }

        try {
            orderService.addOrderDetail(list);
        } catch (Exception e) {
            log.error("订单明细创建失败..", e);
        }

        //发送消息至broker---->通知库存
        orderService.sendMessageToBroker(order_details1);


        return "订单创建成功，订单为：" + order_master.getOrder_code();


    }

    //时间戳工具
    public static Timestamp getNowTimestamp() {
        Date date = new Date();
        Timestamp nousedate = new Timestamp(date.getTime());
        return nousedate;
    }


}
