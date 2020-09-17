package com.zq0521.service;

import com.zq0521.entity.MessageContent;
import com.zq0521.entity.OrderInfo;

public interface OrderInfoService {

    /**
     * 订单保存
     *
     * @param orderInfo      订单实体
     * @param messageContent
     */
    void saveOrderInfo(OrderInfo orderInfo);

    void saveOrderInfoWithMessage(OrderInfo orderInfo);


}
