package cn.zq0521.upstream;

import cn.zq0521.consumer.ConfirmListener;
import cn.zq0521.entity.Order_details;
import cn.zq0521.rabbitmq.RabbitmqConfig;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Component
public class OrderUpstreamService {


    /**
     * 下单成功，通知库存消息
     * orderService ------->stockService
     *
     * @param orderDetails 订单明细
     * @throws IOException
     * @throws TimeoutException
     */
    public void orderSuccess(Order_details orderDetails) throws IOException, TimeoutException {

        RabbitmqConfig rabbitmqConfig = new RabbitmqConfig();

        Connection connection = rabbitmqConfig.getConnection();
        //创建信道
        Channel channel = connection.createChannel();

        //创建交换机
        String exchangeName = "order.success.exchange";
        String exchangeType = "direct";
        channel.exchangeDeclare(exchangeName, exchangeType, true, false, null);

        //定义routingKey
        String routingKey = "order.success.key";

        //定义消息体
        String msgBody = "有订单下单成功，请减扣库存.....";
        //定义头信息
        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("order", orderDetails);

        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder()
                .deliveryMode(2)
                .correlationId(UUID.randomUUID().toString())
                .headers(orderInfo)
                .contentEncoding("utf-8")
                .expiration("10000")  //10秒生存期，10秒后进入死信队列
                .build();

        //进行消息监听
        channel.addConfirmListener(new ConfirmListener());

        //发布消息
        channel.basicPublish(exchangeName, routingKey, basicProperties, msgBody.getBytes());
    }


}
