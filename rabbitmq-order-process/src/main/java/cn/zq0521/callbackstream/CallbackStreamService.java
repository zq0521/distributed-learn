package cn.zq0521.callbackstream;

import cn.zq0521.consumer.QosConsumer;
import cn.zq0521.rabbitmq.RabbitmqConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CallbackStreamService {


     RabbitmqConfig rabbitmqConfig = new RabbitmqConfig();

    /**
     * 监听库存服务消息
     *
     * @throws IOException
     * @throws TimeoutException
     */
    public void ListenToStockMessage() throws IOException, TimeoutException {
        Connection connection = rabbitmqConfig.getConnection();

        Channel channel = connection.createChannel();
        //监听的交换机
        String exchangeName = "stock.success.exchange";
        String exchangeType = "direct";

        channel.exchangeDeclare(exchangeName, exchangeType, true, false, null);

        //监听的队列
        String routingKey = "stock.key";
        String queueName = "stock.queue";
        channel.queueDeclare(queueName, true, false, false, null);
        //队列绑定
        channel.queueBind(queueName, exchangeName, routingKey);

        //消费消息
        channel.basicConsume(queueName, new QosConsumer(channel));


    }

}
