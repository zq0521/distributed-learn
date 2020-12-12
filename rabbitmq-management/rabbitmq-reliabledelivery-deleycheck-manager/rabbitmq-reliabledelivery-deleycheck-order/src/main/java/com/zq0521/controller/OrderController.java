package com.zq0521.controller;

import com.zq0521.bo.MsgTxtBo;
import com.zq0521.compent.MsgSender;
import com.zq0521.entity.OrderInfo;
import com.zq0521.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
public class OrderController {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private MsgSender msgSender;

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

    /**
     * 订单重试接口
     *
     * @param msgTxtBo
     * @return
     */
    @RequestMapping("/retryMsg")
    public String retryMsg(@RequestBody MsgTxtBo msgTxtBo) {
        log.info("消息重发：{}", msgTxtBo);

        //第一次发送消息
        msgSender.senderMsg(msgTxtBo);

        //发送延时消息
        msgSender.sendDelayCheckMsg(msgTxtBo);

        return "ok";

    }

}
