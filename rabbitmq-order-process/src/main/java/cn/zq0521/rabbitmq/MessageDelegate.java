package cn.zq0521.rabbitmq;

import cn.zq0521.entity.Order_details;
import cn.zq0521.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class MessageDelegate {

    private static Integer order_status = 0;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderService orderService;

    public static MessageDelegate messageDelegate;

    /**
     * 解决@Autowired注入为空问题
     */
    @PostConstruct
    public void init() {
        messageDelegate = this;
        messageDelegate.rabbitTemplate = this.rabbitTemplate;
        messageDelegate.orderService = this.orderService;
    }


    /**
     * 当监听消息不指定处理方式，默认的处理方式
     *
     * @param msgBody
     */
    public void handleMessage(String msgBody) {
        System.out.println("TulingMsgDelegate。。。。。。handleMessage: " + msgBody);
    }

    /**
     * 库存收到订单明细---->进行业务处理
     *
     * @param order_details
     */
    public void orderMessage(Order_details order_details) {
        log.info("库存服务：-------->收到订单服务发送的消息");
        //业务处理，查库，减扣，数据处理
        /**
         * insert(),update()....
         */


        //构造消息，发送至stack.exchange, 回调服务监听该交换机
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

        messageDelegate.rabbitTemplate.convertAndSend("stock.exchange", "stock.key", orderMsg);

    }


    /**
     * 回调服务收到库存减扣消息----->进行业务处理
     */
    public void stackMessage(Order_details order_details) {
        log.info("回调服务：------------>收到库存服务发送的消息");
        //业务处理，数据库记录该条消息，记录订单号;
        /**
         * insert(),update()....
         */

    }

    /**
     * 回调服务收到 订单服务的延时消息----->进行业务处理
     *
     * @param order_details
     */
    public void orderDelayedMessage(Order_details order_details) {
        log.info("回调服务：------------>收到延时检查消息");
        /**
         * 业务处理，查询数据库，比对订单的状态;
         * 订单状态为1，完成流程；
         * 订单状态还是为2，失败，回调order服务
         */

        order_status = 1;

        if (order_status == 1) {
            log.info("延时检查通过------->订单完成，订单状态已变更，已通知用户发货成功");
        } else {
            log.info("订单未完成，重新回调Order接口服务");
            messageDelegate.orderService.sendMessageToBroker(order_details);
        }


    }


}
