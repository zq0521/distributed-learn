package com.zq0521.constants;

/**
 * MQ配置常量
 */
public class MqConstants {

    /**
     * 交换机名称
     */
    //订单->库存 交换机
    public static final String ORDER_TO_PRODUCT_EXCHANGE_NAME = "order-to-product.exchange";
    //订单->库存 延迟交换机
    public static final String ORDER_TO_PRODUCT_DELAY_EXCHANGE_NAME = "order-to-product.delayExchange";

    /**
     * 队列名称
     */
    //订单->库存 队列
    public static final String ORDER_TO_PRODUCT_QUEUE_NAME = "order-to-product.queue";
    //库存->回调 队列
    public static final String PRODUCT_TO_CALLBACK_QUEUE_NAME = "product-to-callback.queue";
    //订单->库存 延迟队列
    public static final String ORDER_TO_PRODUCT_DELAY_QUEUE_NAME = "order-to-product.delayQueue";


    /**
     * 路由key
     */
    //订单->库存 key
    public static final String ORDER_TO_PRODUCT_ROUTING_KEY = "order-to-product.key";
    //库存->回调 路由key
    public static final String PRODUCT_TO_CALLBACK_ROUTING_KEY = "product-to-callback.key";
    //订单->库存 延迟路由key
    public static final String ORDER_TO_PRODUCT_DELAY_ROUTING_KEY = "order-to-product.delayKey";


    /**
     * 消息重发最大次数
     */
    public static final Integer MSG_RETRY_COUNT = 5;
    /**
     * 订单创建时间与定时任务时间差
     */
    public static final Integer TIME_DIFF = 30;

    /**
     * 延迟消息时间
     */
    public static final Integer DELAY_TIME = 30000;


}
