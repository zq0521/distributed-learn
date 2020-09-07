package com.zq0521.comfirm_listener;

import com.rabbitmq.client.ConfirmListener;

import java.io.IOException;

public class TulingConfirmListener implements ConfirmListener {

    /**
     * 消息确认方法
     * 该方法是确认消息被消费了的方法，当消息收到了，就会调用该方法告知生产者消息已经被收到了
     *
     * @param deliveryTag 消息的唯一ID
     * @param multiple    是否批量处理
     * @throws IOException
     */
    @Override
    public void handleAck(long deliveryTag, boolean multiple) throws IOException {
        System.out.println("当前时间：" + System.currentTimeMillis() + ",  TulingConfirmListener handleAck: " + deliveryTag);

    }

    /**
     * 处理异常
     * 消息没有被收到的callback方法
     * 当消息没有被消费，就会调用该方法，当出了问题，就会调用该方法
     *
     * @param deliveryTag
     * @param multiple
     * @throws IOException
     */
    @Override
    public void handleNack(long deliveryTag, boolean multiple) throws IOException {
        System.out.println("TulingConfirmListener handNack: " + deliveryTag);
    }
}
