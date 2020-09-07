package cn.zq0521.consumer;

import cn.zq0521.downstream.DownStreamService;
import cn.zq0521.entity.Order_details;
import cn.zq0521.service.StockService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class QosConsumer extends DefaultConsumer {

    private Channel channel;

    @Autowired
    private StockService stockService;


    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */
    public QosConsumer(Channel channel) {
        super(channel);
        this.channel = channel;
    }


    @SneakyThrows
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

        /**
         * 先处理业务逻辑，再进行ACK
         */
        Order_details order_details =(Order_details) properties.getHeaders().get("order");
        stockService.updateProduct(order_details);

        //手动确认ACK
        channel.basicAck(envelope.getDeliveryTag(),false);
        System.out.println("------>减库操作成功，日志输出....");


        //手动发送消息之回调服务，确定该消息已被消费
        DownStreamService downStreamService = new DownStreamService();
        downStreamService.sendMsgToCallBackService(order_details);






    }
}
