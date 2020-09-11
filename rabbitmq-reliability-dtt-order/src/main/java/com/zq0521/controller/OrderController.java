package com.zq0521.controller;

import com.zq0521.entity.OrderInfo;
import com.zq0521.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class OrderController {

    @Autowired
    private OrderInfoService orderInfoService;

    @RequestMapping("/saveOrder")
    public String saveOrder() {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(System.currentTimeMillis());
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        orderInfo.setUserName("smlz");
        orderInfo.setMoney(10000);
        orderInfo.setProductNo(1);

        orderInfoService.saveOrderInfoWithMessage(orderInfo);
        return "ok";

    }

}
