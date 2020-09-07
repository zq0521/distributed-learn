package cn.zq0521.consumer;

import java.io.IOException;

public class ConfirmListener implements com.rabbitmq.client.ConfirmListener {


    @Override
    public void handleAck(long deliveryTag, boolean multiple) throws IOException {
        //broker收到消息，记录该日志
        System.out.println("订单业务----->通知库存业务成功");

    }

    @Override
    public void handleNack(long deliveryTag, boolean multiple) throws IOException {
        //broker未收到消息，记录该日志
        System.out.println("订单业务---->通知库存业务失败");
    }
}
