package cn.zq0521.downstream;

import cn.zq0521.consumer.QosConsumer;
import cn.zq0521.entity.Order_details;
import cn.zq0521.rabbitmq.RabbitmqConfig;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;


public class DownStreamService {

     RabbitmqConfig rabbitmqConfig = new RabbitmqConfig();

    /**
     * 监听业务服务的消息
     *
     * @throws IOException
     * @throws TimeoutException
     */
    public void ListenToOrderService() throws IOException, TimeoutException {
        //链接
        Connection connection = rabbitmqConfig.getConnection();
        //信道
        Channel channel = connection.createChannel();

        //声明交换机
        String exchangeName = "order.success.exchange";
        String exchangeType = "direct";
        channel.exchangeDeclare(exchangeName, exchangeType, true, false, null);

        //声明消息队列
        String queueName = "order.success.queue";
        channel.queueDeclare(queueName, true, false, false, null);

        //消息绑定之交换机
        String routingKey = "order.success.#";
        channel.queueBind(queueName, exchangeName, routingKey);

        //消息限流，1条1条来
        channel.basicQos(0, 1, false);

        //消费消息
        channel.basicConsume(queueName, new QosConsumer(channel));
    }


    /**
     * 业务成功后，发送消息之回调服务
     * @param order_details
     * @throws IOException
     * @throws TimeoutException
     */
    public void sendMsgToCallBackService(Order_details order_details) throws IOException, TimeoutException {
        Connection connection = rabbitmqConfig.getConnection();
        //信道
        Channel channel = connection.createChannel();

        //声明交换机
        String exchangeName = "stock.success.exchange";
        String exchangeType = "direct";
        channel.exchangeDeclare(exchangeName, exchangeType, true, false, null);

        //声明routingKey
        String routingKey = "stock.key";
        //定义消息体
        String msgBody = "库存业务成功----->";

        //定义消息头
        Map<String, Object> detailsMap = new HashMap<>();
        detailsMap.put("order", order_details);
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder()
                .headers(detailsMap)
                .build();

        //发布消息
        channel.basicPublish(exchangeName, routingKey, basicProperties, msgBody.getBytes());


    }

}
